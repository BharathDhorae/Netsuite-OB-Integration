package com.promanatia.CamelDemo.schedulars;

import com.promanatia.CamelDemo.DTO.EntityMasterDTO;
import com.promanatia.CamelDemo.DTO.FieldMappingDTO;
import com.promanatia.CamelDemo.Exception.InfrastructureException;
import com.promanatia.CamelDemo.Exception.RowValidationException;
import com.promanatia.CamelDemo.repository.EntityMasterRepository;
import com.promanatia.CamelDemo.repository.FieldMappingRepository;
import com.promanatia.CamelDemo.service.*;
import com.promanatia.CamelDemo.utility.CsvAggregationStrategy;
import com.promanatia.CamelDemo.utility.CsvParser;
import com.promanatia.CamelDemo.utility.CsvValidator;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.EndpointConsumerBuilder;
import org.apache.camel.builder.EndpointProducerBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;

/**
 * Everything that is identical between the two directional schedulers (OB ->
 * NetSuite and NetSuite -> OB) lives here: the exception-handling policy, the
 * CSV parse/validate/map steps, and small helpers.
 *
 * Each concrete subclass supplies only the direction-specific wiring: which
 * SFTP endpoint to read from, which direct: URI its own private pipeline runs
 * on, which SFTP endpoints to write success/error output to, and which FlowType
 * pair to use for the entity lookup.
 *
 * Because each subclass owns its own direct: URI and therefore its own
 * route/aggregator instance, the two directions no longer need to be correlated
 * on flowDirection — each one's completionSize/Timeout batch is naturally
 * isolated from the other's.
 */
public abstract class AbstractCsvSchedulerRoute extends RouteBuilder {

	protected static final Logger logger = LoggerFactory.getLogger(AbstractCsvSchedulerRoute.class);

	protected final CsvAggregationStrategy csvAggregationStrategy;
	protected final CsvValidator csvValidatorService;
	protected final CsvMappingService csvMappingService;
	protected final ErrorCsvService errorCsvService;
	protected final ApplicationLoggerService loggerService;
	protected final SftpUploadService sftpUploadService;
	protected final CsvParser csvParser;
	protected final FieldMappingRepository fieldMappingRepository;
	protected final EntityMasterRepository entityMasterRepository;
	protected final LookupService lookupService;

	protected AbstractCsvSchedulerRoute(CsvAggregationStrategy csvAggregationStrategy, CsvValidator csvValidatorService,
			CsvMappingService csvMappingService, ErrorCsvService errorCsvService,
			ApplicationLoggerService loggerService, SftpUploadService sftpUploadService, CsvParser csvParser,
			FieldMappingRepository fieldMappingRepository, EntityMasterRepository entityMasterRepository,
			LookupService lookupService) {

		this.csvAggregationStrategy = csvAggregationStrategy;
		this.csvValidatorService = csvValidatorService;
		this.csvMappingService = csvMappingService;
		this.errorCsvService = errorCsvService;
		this.loggerService = loggerService;
		this.sftpUploadService = sftpUploadService;
		this.csvParser = csvParser;
		this.fieldMappingRepository = fieldMappingRepository;
		this.entityMasterRepository = entityMasterRepository;
		this.lookupService = lookupService;
	}

	// -----------------------------------------------------------------
	// Direction-specific hooks each subclass must provide.
	// -----------------------------------------------------------------

	/** SFTP endpoint this scheduler reads incoming files from. */
	protected abstract EndpointConsumerBuilder getSourceSftpEndpoint();

	/**
	 * Unique direct: URI for this scheduler's own private pipeline (must not
	 * collide with the other scheduler's).
	 */
	protected abstract String getProcessingDirectUri();

	/** SFTP endpoint the mapped/valid output CSV is written to. */
	protected abstract EndpointProducerBuilder getSuccessOutputSftpEndpoint();

	/** SFTP endpoint the error CSV is written to. */
	protected abstract EndpointProducerBuilder getErrorOutputSftpEndpoint();

	/** FlowType (as string) of the source system, used for the entity lookup. */
	protected abstract String getSourceFlowType();

	/** FlowType (as string) of the target system, used for the entity lookup. */
	protected abstract String getTargetFlowType();

	/** Short label used for routeIds and logging, e.g. "OB_TO_NETSUITE". */
	protected abstract String getDirectionLabel();

	@Override
	public void configure() {

		// ---------------------------------------------------------------
		// 1) Infrastructure failures (DB down, S3 down, network timeout):
		// retry with backoff. If retries are exhausted, the exchange
		// remains failed (handled=false) so the SFTP consumer's own
		// moveFailed=errorDirectory kicks in automatically — no manual
		// file tracking needed since each file is its own exchange.
		// ---------------------------------------------------------------
		onException(InfrastructureException.class).maximumRedeliveries(3).redeliveryDelay(5000).backOffMultiplier(2.0)
				.retryAttemptedLogLevel(LoggingLevel.WARN).logExhausted(true)
				.log(LoggingLevel.ERROR, "[" + getDirectionLabel()
						+ "] Infra failure processing file ${header.CamelFileName} after retries exhausted: ${exception.message}")
				.process(exchange -> {
					String fileName = exchange.getIn().getHeader("CamelFileName", String.class);
					loggerService.error("N/A", "N/A", "N/A",
							"Infrastructure failure (DB/S3/network) processing file: " + fileName,
							exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class).getMessage());
				}).handled(false);

		// ---------------------------------------------------------------
		// 2) File-level data validation failures that make the whole file
		// unusable (bad header, unknown entity, etc). Not retried —
		// exchange stays failed so moveFailed=errorDirectory applies.
		// ---------------------------------------------------------------
		onException(RowValidationException.class)
				.log(LoggingLevel.WARN,
						"[" + getDirectionLabel()
								+ "] Validation failure for file ${header.CamelFileName}: ${exception.message}")
				.process(exchange -> {
					String fileName = exchange.getIn().getHeader("CamelFileName", String.class);
					loggerService.error("N/A", "N/A", "N/A", "Validation failure processing file: " + fileName,
							exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class).getMessage());
				}).handled(false);

		// ---------------------------------------------------------------
		// 3) Anything else unexpected: log fully, do not silently swallow.
		// ---------------------------------------------------------------
		onException(Exception.class)
				.log(LoggingLevel.ERROR,
						"[" + getDirectionLabel()
								+ "] Unhandled error processing file ${header.CamelFileName}: ${exception.message}")
				.handled(false);

		// =================================================================
		// Reader: pulls files from this direction's own SFTP endpoint,
		// resolves the entity, then hands off to this scheduler's OWN
		// private processing pipeline.
		// =================================================================
		from(getSourceSftpEndpoint()).routeId(getDirectionLabel() + "-sftp-reader")
				.process(this::resolveEntityAndFileId).to(getProcessingDirectUri());

		// =================================================================
		// Pipeline: parsing/validation/mapping logic. Runs on this
		// scheduler's own direct: URI/route, so its completionSize /
		// completionTimeout batch is isolated from the other direction.
		// =================================================================
		from(getProcessingDirectUri()).routeId(getDirectionLabel() + "-csv-processor")

				.convertBodyTo(String.class).process(exchange -> {
					String body = exchange.getIn().getBody(String.class);
					int totalRows = body.split("\\r?\\n").length - 1;
					logger.info("[{}] CSV loaded successfully. Total data rows : {}", getDirectionLabel(), totalRows);
				}).aggregate(simple("${exchangeProperty.entityName}"), csvAggregationStrategy).completionSize(10)
				.completionTimeout(15000).process(exchange -> lookupService.clearCache())

				.process(this::validateFileAndHeaders).process(this::validateRows).process(this::generateMappedCsv)

				.choice().when(simple("${exchangeProperty.validRows.size} > 0"))
				.process(sftpUploadService::uploadSuccessFile).to(getSuccessOutputSftpEndpoint()).end()

				.choice().when(simple("${exchangeProperty.errorRows.size} > 0"))
				.process(errorCsvService::generateErrorCsv).process(sftpUploadService::uploadErrorFile)
				.to(getErrorOutputSftpEndpoint()).end();
	}

	// ---------------------------------------------------------------------
	// Resolves the entity for this direction and sets the deterministic fileId.
	// ---------------------------------------------------------------------
	private void resolveEntityAndFileId(Exchange exchange) {

		String fileName = exchange.getIn().getHeader("CamelFileName", String.class);
		String entityName = fileName.split("_")[0];

		// Deterministic per-file identifier for idempotent downstream writes.
		// Reusing the source file name (not a random UUID) means a retried
		// redelivery of THIS exchange always targets the same S3/SFTP key,
		// so retries overwrite in place instead of creating a duplicate.
		String fileId = fileName.contains(".") ? fileName.substring(0, fileName.lastIndexOf('.')) : fileName;
		exchange.setProperty("fileId", fileId);

		EntityMasterDTO entity;
		try {
			entity = entityMasterRepository.findByEntityName(entityName, getSourceFlowType(), getTargetFlowType());
		} catch (Exception e) {
			// DB lookup blew up (connection refused, timeout, etc.) — infra issue.
			throw wrapAsInfrastructure("Entity lookup failed for " + entityName, e);
		}

		if (entity == null) {
			// DB responded fine, there's just no such entity — that's bad data.
			throw new RowValidationException("No entity mapping found for entity name: " + entityName + " (direction: "
					+ getDirectionLabel() + ")");
		}

		exchange.setProperty("entity", entity);
		exchange.setProperty("entityName", entity.getEntityName());
		logger.info("Started processing file [{}] direction [{}]", fileName, getDirectionLabel());
	}

	// ---------------------------------------------------------------------
	// Shared step: file-level + header validation.
	// ---------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	private void validateFileAndHeaders(Exchange exchange) {

		String fileContent = exchange.getIn().getBody(String.class);
		String[] rows = fileContent.split("\\r?\\n");
		EntityMasterDTO entity = exchange.getProperty("entity", EntityMasterDTO.class);

		List<FieldMappingDTO> mappings;
		String identityColumn;
		try {
			mappings = fieldMappingRepository.getMappings(entity.getSourceTableName());
			identityColumn = fieldMappingRepository.getIdentificationColumn(entity.getSourceTableName());

		} catch (Exception e) {
			throw wrapAsInfrastructure("Failed to load field mappings for " + entity.getSourceTableName(), e);
		}

		try {
			csvValidatorService.validateFile(rows);
			String[] headers = csvParser.parseCsvLine(rows[0]);
			csvValidatorService.validateHeader(headers, mappings);
			logger.info(entity.getEntityName(), "Header validation successful.");
			exchange.setProperty("mappings", mappings);
			exchange.setProperty("identityColumn", identityColumn);
			exchange.setProperty("headers", headers);
			exchange.setProperty("rows", rows);
		} catch (InfrastructureException ie) {
			throw ie;
		} catch (Exception e) {
			// Malformed file / bad headers — data problem, not infra.
			throw new RowValidationException(
					"File/header validation failed for file " + entity.getEntityName() + ": " + e.getMessage(), e);
		}
	}

	// ---------------------------------------------------------------------
	// Shared step: per-row validation, splitting rows into valid/error sets.
	// ---------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	private void validateRows(Exchange exchange) {

		String[] rows = exchange.getProperty("rows", String[].class);
		String[] headers = exchange.getProperty("headers", String[].class);
		List<String> validRows = new ArrayList<>();
		List<String> errorRows = new ArrayList<>();
		Set<String> failedOrders = new HashSet<>();

		EntityMasterDTO entity = exchange.getProperty("entity", EntityMasterDTO.class);
		List<FieldMappingDTO> mappings = exchange.getProperty("mappings", List.class);
		String identityColumn = exchange.getProperty("identityColumn", String.class);

		for (int i = 1; i < rows.length; i++) {
			String row = rows[i];
			if (row == null || row.trim().isEmpty()) {
				continue;
			}

			String[] cols = csvParser.parseCsvLine(row);

			String documentNo = getColumnValue(headers, cols, identityColumn);
			String productId = getColumnValue(headers, cols, identityColumn);

			logger.info(productId, entity.getEntityName(), documentNo, "Started processing CSV file.");

			try {
				List<String> validationErrors = csvValidatorService.validateRow(cols, headers, i, row, mappings);

				if (!validationErrors.isEmpty()) {
					failedOrders.add(documentNo);
					String errorMessage = String.join(" | ", validationErrors);
					logger.error("Validation failed for Row {} : {}", i + 1, errorMessage);
					loggerService.error(productId, entity.getEntityName(), documentNo, "CSV Validation Failed",
							errorMessage);
				} else {
					logger.info(productId, entity.getEntityName(), documentNo, "Row {} validated successfully.", i + 1);
				}
			} catch (Exception e) {
				if (isInfrastructureFailure(e)) {
					logger.error("Infrastructure failure during row validation, aborting file: {}", e.getMessage());
					throw wrapAsInfrastructure("Infrastructure failure validating row " + (i + 1)
							+ " of file for entity " + entity.getEntityName(), e);
				}
				throw e;
			}
		}

		for (int i = 1; i < rows.length; i++) {
			String row = rows[i];
			if (row == null || row.trim().isEmpty()) {
				continue;
			}

			String[] cols = csvParser.parseCsvLine(row);
			String documentNo = getColumnValue(headers, cols, identityColumn);
			if (failedOrders.contains(documentNo)) {
				errorRows.add(row);
			} else {
				validRows.add(row);
			}
		}

		exchange.setProperty("validRows", validRows);
		exchange.setProperty("errorRows", errorRows);
		exchange.setProperty("failedOrders", failedOrders);
	}

	// ---------------------------------------------------------------------
	// Shared step: builds the mapped CSV for valid rows only.
	// ---------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	private void generateMappedCsv(Exchange exchange) {

		EntityMasterDTO entity = exchange.getProperty("entity", EntityMasterDTO.class);
		String[] headers = exchange.getProperty("headers", String[].class);
		List<String> validRows = exchange.getProperty("validRows", List.class);
		List<FieldMappingDTO> mappings = exchange.getProperty("mappings", List.class);

		if (validRows != null && !validRows.isEmpty()) {
			try {
				String mappedCsv = csvMappingService.generateMappedCsv(mappings, headers, validRows);
				exchange.setProperty("mappedCsv", mappedCsv);
			} catch (Exception e) {
				throw wrapAsInfrastructure("Failed generating mapped CSV for " + entity.getSourceTableName(), e);
			}
		}
	}

	private InfrastructureException wrapAsInfrastructure(String message, Exception cause) {
		return new InfrastructureException(message, cause);
	}

	/**
	 * Heuristic to distinguish "the system is broken" from "the data is wrong".
	 * Extend this as you find more infra-related exception types in your stack
	 * (e.g. AWS SDK client exceptions, specific driver exceptions).
	 */
	private boolean isInfrastructureFailure(Throwable e) {
		Throwable current = e;
		while (current != null) {
			if (current instanceof SQLException || current instanceof DataAccessException
					|| current instanceof SocketTimeoutException || current instanceof IOException) {
				return true;
			}
			String className = current.getClass().getName();
			if (className.contains("amazonaws") || className.contains("awssdk") || className.contains("S3Exception")
					|| className.contains("ConnectException") || className.contains("UnknownHostException")) {
				return true;
			}
			current = current.getCause();
		}
		return false;
	}

	private String getColumnValue(String[] headers, String[] cols, String columnName) {

		for (int i = 0; i < headers.length; i++) {
			if (headers[i].equalsIgnoreCase(columnName)) {
				return i < cols.length ? cols[i].replace("\"", "").trim() : "";
			}
		}

		return "";
	}
}