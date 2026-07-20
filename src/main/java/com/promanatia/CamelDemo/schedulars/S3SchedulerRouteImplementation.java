package com.promanatia.CamelDemo.schedulars;

import com.promanatia.CamelDemo.DTO.EntityMasterDTO;
import com.promanatia.CamelDemo.DTO.FieldMappingEntity;
import com.promanatia.CamelDemo.DTO.FlowType;
import com.promanatia.CamelDemo.config.S3Config;
import com.promanatia.CamelDemo.service.*;
import com.promanatia.CamelDemo.utility.ApplicationLoggerService;
import com.promanatia.CamelDemo.utility.CsvAggregationStrategy;
import com.promanatia.CamelDemo.utility.CsvParser;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import com.promanatia.CamelDemo.config.SftpConfig;
import com.promanatia.CamelDemo.repository.EntityMasterRepository;
import com.promanatia.CamelDemo.repository.FieldMappingRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class S3SchedulerRouteImplementation extends RouteBuilder {

	private final SftpConfig sftpConfig;
	private final S3Config s3Config;
	private final CsvAggregationStrategy csvAggregationStrategy;
	private final CsvValidator csvValidatorService;
	private final CsvMappingService csvMappingService;
	private final SftpUploadService sftpUploadService;
	private final ErrorCsvService errorCsvService;
	private final S3UploadService s3UploadService;
	private final ApplicationLoggerService loggerService;
	private final CsvParser csvParser;
	private final FieldMappingRepository fieldMappingRepository;
	private final EntityMasterRepository entityMasterRepository;

	public S3SchedulerRouteImplementation(S3Config s3Config, CsvAggregationStrategy csvAggregationStrategy,
			CsvValidator csvValidatorService, CsvMappingService csvMappingService, SftpUploadService sftpUploadService,
			ErrorCsvService errorCsvService, S3UploadService s3UploadService, ApplicationLoggerService loggerService,
			SftpConfig sftpConfig, CsvParser csvParser, FieldMappingRepository fieldMappingRepository,
			EntityMasterRepository entityMasterRepository) {

		this.s3Config = s3Config;
		this.csvAggregationStrategy = csvAggregationStrategy;
		this.csvValidatorService = csvValidatorService;
		this.csvMappingService = csvMappingService;
		this.sftpUploadService = sftpUploadService;
		this.errorCsvService = errorCsvService;
		this.s3UploadService = s3UploadService;
		this.loggerService = loggerService;
		this.sftpConfig = sftpConfig;
		this.csvParser = csvParser;
		this.fieldMappingRepository = fieldMappingRepository;
		this.entityMasterRepository = entityMasterRepository;
	}

	@Override
	public void configure() {

		onException(Exception.class).log("Error processing file: ${header.CamelFileName}").log("${exception.message}")
				.handled(false);

		from(s3Config.getReadUri()).routeId("s3-file-reader")

				.process(exchange -> {
					String key = exchange.getIn().getHeader("CamelAwsS3Key", String.class);

					if (key == null || key.endsWith("/")) {
						log.info("Skipping S3 folder: {}", key);
						exchange.setProperty(Exchange.ROUTE_STOP, Boolean.TRUE);
						return;
					}

					String fileName = key.substring(key.lastIndexOf('/') + 1);
					exchange.getIn().setHeader("CamelFileName", fileName);

					String entityName = fileName.split("_")[0];

					EntityMasterDTO entity = entityMasterRepository.findByEntityName(entityName,
							FlowType.NETSUITE.toString(), FlowType.OPENBRAVO.toString());

					exchange.setProperty("entity", entity);
					exchange.setProperty("entityName", entity.getEntityName());
				}).filter(exchange -> exchange.getProperty(Exchange.ROUTE_STOP) == null).convertBodyTo(String.class)
				.process(exchange -> {
					String body = exchange.getIn().getBody(String.class);
					log.info("DIAGNOSTIC pre-aggregate: bodyLength={}, bodyPreview={}",
							body == null ? "null" : body.length(),
							body == null ? "null" : body.substring(0, Math.min(200, body.length())));
				}).aggregate(exchangeProperty("entityName"), csvAggregationStrategy).ignoreInvalidCorrelationKeys()
				.completionSize(10).completionTimeout(15000)

				.process(exchange -> {

					exchange.getIn().removeHeader("Content-Length");
					exchange.getIn().removeHeader("CamelAwsS3ContentLength");
				}).process(exchange -> {
					String fileContent = exchange.getIn().getBody(String.class);
					log.info("DIAGNOSTIC post-aggregate: bodyLength={}, bodyPreview={}",
							fileContent == null ? "null" : fileContent.length(), fileContent == null ? "null"
									: fileContent.substring(0, Math.min(200, fileContent.length())));

					String[] rows = fileContent.split("\\r?\\n");
					EntityMasterDTO entity = exchange.getProperty("entity", EntityMasterDTO.class);
					List<FieldMappingEntity> mappings = fieldMappingRepository.getMappings(entity.getSourceTableName());
					csvValidatorService.validateFile(rows);
					String[] headers = csvParser.parseCsvLine(rows[0]);
					csvValidatorService.validateHeader(headers, mappings);
					exchange.setProperty("headers", headers);
					exchange.setProperty("rows", rows);

					log.info("DIAGNOSTIC after split: rowCount={}, headerCount={}", rows.length, headers.length);
				}).process(exchange -> {

					String[] rows = exchange.getProperty("rows", String[].class);
					String[] headers = exchange.getProperty("headers", String[].class);
					List<String> validRows = new ArrayList<>();
					List<String> errorRows = new ArrayList<>();
					Set<String> failedOrders = new HashSet<>();
					EntityMasterDTO entity = exchange.getProperty("entity", EntityMasterDTO.class);
					;
					List<FieldMappingEntity> mappings = fieldMappingRepository.getMappings(entity.getSourceTableName());
					for (int i = 1; i < rows.length; i++) {
						String row = rows[i];
						if (row == null || row.trim().isEmpty()) {
							continue;
						}

						String[] cols = csvParser.parseCsvLine(row);
						String documentNo = cols.length > 0 ? cols[0].replace("\"", "").trim() : "UNKNOWN";
						String productId = cols.length > 0 ? cols[4].replace("\"", "").trim() : "UNKNOWN";
						try {
							csvValidatorService.validateRow(cols, headers, i, row, mappings);
						} catch (Exception e) {
							log.warn("DIAGNOSTIC row {} FAILED validation. documentNo={}, reason={}, row={}", i,
									documentNo, e.getMessage(), row);
							loggerService.error(productId, entity.getSourceTableName(), documentNo,
									"Error processing CSV columnn file.", e.getMessage());
							failedOrders.add(documentNo);
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
					log.info(
							"DIAGNOSTIC validation summary: totalDataRows={}, validRows={}, errorRows={}, failedOrders={}",
							rows.length - 1, validRows.size(), errorRows.size(), failedOrders);
				})

				.process(exchange -> {
					EntityMasterDTO entity = exchange.getProperty("entity", EntityMasterDTO.class);
					String[] headers = exchange.getProperty("headers", String[].class);
					List<String> validRows = exchange.getProperty("validRows", List.class);
					List<FieldMappingEntity> mappings = exchange.getProperty("mappings", List.class);
					if (validRows != null && !validRows.isEmpty()) {
						String mappedCsv = csvMappingService.generateMappedCsv(mappings, headers, validRows);
						exchange.setProperty("mappedCsv", mappedCsv);
					}
				})

				.choice().when(exchange -> {
					List<String> validRows = exchange.getProperty("validRows", List.class);
					return validRows != null && !validRows.isEmpty();
				}).process(sftpUploadService::uploadSuccessFile).toD(sftpConfig.getInSftpEndpoint())
				.log("Uploaded ${exchangeProperty.validRows.size()} valid row(s) to SFTP for file: ${header.CamelFileName}")
				.end()

				.choice().when(exchange -> {
					List<String> errorRows = exchange.getProperty("errorRows", List.class);
					return errorRows != null && !errorRows.isEmpty();
				}).process(errorCsvService::generateErrorCsv).process(s3UploadService::uploadErrorFile)
				.to(s3Config.getWriteUri())
				.log("Uploaded ${exchangeProperty.errorRows.size()} error row(s) to S3 error folder for file: ${header.CamelFileName}")
				.end()

				.choice().when(exchange -> {
					List<String> validRows = exchange.getProperty("validRows", List.class);
					List<String> errorRows = exchange.getProperty("errorRows", List.class);
					return (validRows == null || validRows.isEmpty()) && (errorRows == null || errorRows.isEmpty());
				}).log("No data rows (valid or error) found in file: ${header.CamelFileName} — nothing to upload").end()

				.process(exchange -> {
					List<String> processedFiles = exchange.getProperty("PROCESSED_FILES", List.class);
					log.info("DIAGNOSTIC archive: processedFiles={}", processedFiles);
					exchange.setProperty("archiveFileList", processedFiles);
				}).split(exchangeProperty("archiveFileList")).process(exchange -> {
					String fileName = exchange.getIn().getBody(String.class);
					String sourceKey = s3Config.getOutFolder() + fileName;
					String destKey = s3Config.getArchiveFolder() + fileName;
					exchange.setProperty("archiveFileName", fileName);
					exchange.setProperty("archiveSourceKey", sourceKey);
					exchange.setProperty("archiveDestKey", destKey);
				}).process(exchange -> {
					exchange.getIn().setHeader("CamelAwsS3BucketName", s3Config.getBucketName());
					exchange.getIn().setHeader("CamelAwsS3BucketDestinationName", s3Config.getBucketName());
					exchange.getIn().setHeader("CamelAwsS3Key", exchange.getProperty("archiveSourceKey", String.class));
					exchange.getIn().setHeader("CamelAwsS3DestinationKey",
							exchange.getProperty("archiveDestKey", String.class));
					exchange.getIn().setHeader("CamelAwsS3Operation", "copyObject");
				}).to(s3Config.getWriteUri()).process(exchange -> {
					exchange.getIn().removeHeader("CamelAwsS3DestinationKey");
					exchange.getIn().removeHeader("CamelAwsS3BucketDestinationName");
					exchange.getIn().setHeader("CamelAwsS3BucketName", s3Config.getBucketName());
					exchange.getIn().setHeader("CamelAwsS3Key", exchange.getProperty("archiveSourceKey", String.class));
					exchange.getIn().setHeader("CamelAwsS3Operation", "deleteObject");
				}).to(s3Config.getWriteUri())
				.log("Archived S3 file: ${exchangeProperty.archiveFileName} (${exchangeProperty.archiveSourceKey} -> ${exchangeProperty.archiveDestKey})")
				.end();
	}
}