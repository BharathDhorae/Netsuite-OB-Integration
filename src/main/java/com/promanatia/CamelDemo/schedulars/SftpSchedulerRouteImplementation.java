package com.promanatia.CamelDemo.schedulars;

import com.promanatia.CamelDemo.DTO.EntityMasterDTO;
import com.promanatia.CamelDemo.DTO.FieldMappingEntity;
import com.promanatia.CamelDemo.DTO.FlowType;
import com.promanatia.CamelDemo.Exception.InfrastructureException;
import com.promanatia.CamelDemo.Exception.RowValidationException;
import com.promanatia.CamelDemo.config.S3Config;
import com.promanatia.CamelDemo.config.SftpConfig;
import com.promanatia.CamelDemo.repository.EntityMasterRepository;
import com.promanatia.CamelDemo.repository.FieldMappingRepository;
import com.promanatia.CamelDemo.service.*;
import com.promanatia.CamelDemo.utility.ApplicationLoggerService;
import com.promanatia.CamelDemo.utility.CsvAggregationStrategy;
import com.promanatia.CamelDemo.utility.CsvParser;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

@Component
public class SftpSchedulerRouteImplementation extends RouteBuilder {

	private static final Logger logger = LoggerFactory.getLogger(SftpSchedulerRouteImplementation.class);

	private final SftpConfig sftpConfig;
	private final S3Config s3Config;
	private final CsvAggregationStrategy csvAggregationStrategy;
	private final CsvValidator csvValidatorService;
	private final CsvMappingService csvMappingService;
	private final S3UploadService s3UploadService;
	private final ErrorCsvService errorCsvService;
	private final ApplicationLoggerService loggerService;
	private final SftpUploadService sftpUploadService;
	private final CsvParser csvParser;
	private final FieldMappingRepository fieldMappingRepository;
	private final EntityMasterRepository entityMasterRepository;

	public SftpSchedulerRouteImplementation(SftpConfig sftpConfig, S3Config s3Config,
			CsvAggregationStrategy csvAggregationStrategy, CsvValidator csvValidatorService,
			CsvMappingService csvMappingService, S3UploadService s3UploadService, ErrorCsvService errorCsvService,
			ApplicationLoggerService loggerService, SftpUploadService sftpUploadService, CsvParser csvParser,
			FieldMappingRepository fieldMappingRepository, EntityMasterRepository entityMasterRepository) {

		this.sftpConfig = sftpConfig;
		this.s3Config = s3Config;
		this.csvAggregationStrategy = csvAggregationStrategy;
		this.csvValidatorService = csvValidatorService;
		this.csvMappingService = csvMappingService;
		this.s3UploadService = s3UploadService;
		this.errorCsvService = errorCsvService;
		this.loggerService = loggerService;
		this.sftpUploadService = sftpUploadService;
		this.csvParser = csvParser;
		this.fieldMappingRepository = fieldMappingRepository;
		this.entityMasterRepository = entityMasterRepository;
	}

	@Override
	public void configure() {

		// ---------------------------------------------------------------
		// 1) Infrastructure failures (DB down, S3 down, network timeout):
		// retry with backoff. If retries are exhausted, the exchange
		// remains failed (handled=false) so the SFTP consumer's own
		// moveFailed=errorDirectory kicks in automatically — no manual
		// file tracking needed now that each file is its own exchange.
		// ---------------------------------------------------------------
		onException(InfrastructureException.class).maximumRedeliveries(3).redeliveryDelay(5000).backOffMultiplier(2.0)
				.retryAttemptedLogLevel(LoggingLevel.WARN).logExhausted(true)
				.log(LoggingLevel.ERROR,
						"Infra failure processing file ${header.CamelFileName} after retries exhausted: ${exception.message}")
				.process(exchange -> {
					String fileName = exchange.getIn().getHeader("CamelFileName", String.class);
					loggerService.error("N/A", "N/A", "N/A",
							"Infrastructure failure (DB/S3/network) processing file: " + fileName,
							exchange.getProperty(org.apache.camel.Exchange.EXCEPTION_CAUGHT, Exception.class)
									.getMessage());
				}).handled(false);

		// ---------------------------------------------------------------
		// 2) File-level data validation failures that make the whole file
		// unusable (bad header, unknown entity, etc). Not retried —
		// exchange stays failed so moveFailed=errorDirectory applies.
		// ---------------------------------------------------------------
		onException(RowValidationException.class)
				.log(LoggingLevel.WARN, "Validation failure for file ${header.CamelFileName}: ${exception.message}")
				.process(exchange -> {
					String fileName = exchange.getIn().getHeader("CamelFileName", String.class);
					loggerService.error("N/A", "N/A", "N/A", "Validation failure processing file: " + fileName, exchange
							.getProperty(org.apache.camel.Exchange.EXCEPTION_CAUGHT, Exception.class).getMessage());
				}).handled(false);

		// ---------------------------------------------------------------
		// 3) Anything else unexpected: log fully, do not silently swallow.
		// ---------------------------------------------------------------
		onException(Exception.class).log(LoggingLevel.ERROR,
				"Unhandled error processing file ${header.CamelFileName}: ${exception.message}").handled(false);

		from(sftpConfig.getSftpEndpoint()).routeId("sftp-file-reader")

				.process(exchange -> {

					String fileName = exchange.getIn().getHeader("CamelFileName", String.class);
					String entityName = fileName.split("_")[0];

					// Deterministic per-file identifier for idempotent downstream
					// writes. Reusing the source file name (not a random UUID) means
					// a retried redelivery of THIS exchange always targets the same
					// S3/SFTP key, so retries overwrite in place instead of creating
					// a duplicate.
					String fileId = fileName.contains(".") ? fileName.substring(0, fileName.lastIndexOf('.'))
							: fileName;
					exchange.setProperty("fileId", fileId);

					EntityMasterDTO entity;
					try {
						entity = entityMasterRepository.findByEntityName(entityName, FlowType.OPENBRAVO.toString(),
								FlowType.NETSUITE.toString());
					} catch (Exception e) {
						// DB lookup blew up (connection refused, timeout, etc.) — infra issue.
						throw wrapAsInfrastructure("Entity lookup failed for " + entityName, e);
					}

					if (entity == null) {
						// DB responded fine, there's just no such entity — that's bad data.
						throw new RowValidationException("No entity mapping found for entity name: " + entityName);
					}

					exchange.setProperty("entity", entity);
					exchange.setProperty("entityName", entity.getEntityName());
					logger.info("Started processing file : " + fileName);
				})

				.convertBodyTo(String.class).process(exchange -> {
					String body = exchange.getIn().getBody(String.class);
					int totalRows = body.split("\\r?\\n").length - 1;
					logger.info("CSV loaded successfully. Total data rows : " + totalRows);
				}).aggregate(exchangeProperty("entityName"), csvAggregationStrategy).completionSize(10)
				.completionTimeout(15000)

				.process(exchange -> {

					String fileContent = exchange.getIn().getBody(String.class);
					String[] rows = fileContent.split("\\r?\\n");
					EntityMasterDTO entity = exchange.getProperty("entity", EntityMasterDTO.class);

					List<FieldMappingEntity> mappings;
					try {
						mappings = fieldMappingRepository.getMappings(entity.getSourceTableName());
					} catch (Exception e) {
						throw wrapAsInfrastructure("Failed to load field mappings for " + entity.getSourceTableName(),
								e);
					}

					try {
						csvValidatorService.validateFile(rows);
						String[] headers = csvParser.parseCsvLine(rows[0]);
						csvValidatorService.validateHeader(headers, mappings);
						logger.info(entity.getEntityName(), "Header validation successful.");
						exchange.setProperty("mappings", mappings);
						exchange.setProperty("headers", headers);
						exchange.setProperty("rows", rows);
					} catch (InfrastructureException ie) {
						throw ie;
					} catch (Exception e) {
						// Malformed file / bad headers — data problem, not infra.
						throw new RowValidationException("File/header validation failed for file "
								+ entity.getEntityName() + ": " + e.getMessage(), e);
					}
				}).process(exchange -> {

					String[] rows = exchange.getProperty("rows", String[].class);
					String[] headers = exchange.getProperty("headers", String[].class);
					List<String> validRows = new ArrayList<>();
					List<String> errorRows = new ArrayList<>();
					Set<String> failedOrders = new HashSet<>();

					EntityMasterDTO entity = exchange.getProperty("entity", EntityMasterDTO.class);
					List<FieldMappingEntity> mappings = exchange.getProperty("mappings", List.class);
					for (int i = 1; i < rows.length; i++) {
						String row = rows[i];
						if (row == null || row.trim().isEmpty()) {
							continue;
						}

						String[] cols = csvParser.parseCsvLine(row);

						String documentNo = cols.length > 0 ? cols[0].replace("\"", "").trim() : "UNKNOWN";
						String productId = cols.length > 4 ? cols[4].replace("\"", "").trim() : "UNKNOWN";

						logger.info(productId, entity.getEntityName(), documentNo, "Started processing CSV file.");

						try {
							csvValidatorService.validateRow(cols, headers, i, row, mappings);
							logger.info(productId, entity.getEntityName(), documentNo,
									"Rows " + i + "validated successfully.");
						} catch (Exception e) {
							if (isInfrastructureFailure(e)) {
								// DB/network blip mid-file: abort the whole file, don't
								// mark every remaining row as "invalid data".
								logger.error("Infrastructure failure during row validation, aborting file: "
										+ e.getMessage());
								throw wrapAsInfrastructure("Infrastructure failure validating row " + i
										+ " of file for entity " + entity.getEntityName(), e);
							}

							failedOrders.add(documentNo);
							logger.error("Validation failed Reason : " + e.getMessage());
							loggerService.error(productId, entity.getEntityName(), documentNo,
									"Error processing CSV columnn file.", e.getMessage());

						}
					}

					for (int i = 1; i < rows.length; i++) {
						String row = rows[i];
						if (row == null || row.trim().isEmpty()) {
							continue;
						}

						String[] cols = csvParser.parseCsvLine(row);
						String documentNo = cols.length > 0 ? cols[0].replace("\"", "").trim() : "UNKNOWN";

						if (failedOrders.contains(documentNo)) {
							errorRows.add(row);
						} else {
							validRows.add(row);
						}
					}

					exchange.setProperty("validRows", validRows);
					exchange.setProperty("errorRows", errorRows);
					exchange.setProperty("failedOrders", failedOrders);

				})

				.process(exchange -> {

					EntityMasterDTO entity = exchange.getProperty("entity", EntityMasterDTO.class);
					String[] headers = exchange.getProperty("headers", String[].class);
					List<String> validRows = exchange.getProperty("validRows", List.class);
					List<FieldMappingEntity> mappings = exchange.getProperty("mappings", List.class);
					if (validRows != null && !validRows.isEmpty()) {
						try {
							String mappedCsv = csvMappingService.generateMappedCsv(mappings, headers, validRows);
							exchange.setProperty("mappedCsv", mappedCsv);
						} catch (Exception e) {
							throw wrapAsInfrastructure(
									"Failed generating mapped CSV for " + entity.getSourceTableName(), e);
						}
					}
				})

				.choice().when(simple("${exchangeProperty.validRows.size} > 0"))
				.process(s3UploadService::uploadSuccessFile).toD(s3Config.getWriteUri()).end().choice()
				.when(simple("${exchangeProperty.errorRows.size} > 0")).process(errorCsvService::generateErrorCsv)
				.process(sftpUploadService::uploadErrorFile).toD(sftpConfig.getErrorSftpEndpoint()).end();
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
}