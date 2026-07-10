package com.promanatia.CamelDemo.schedulars;

import com.promanatia.CamelDemo.DTO.FlowType;
import com.promanatia.CamelDemo.config.SftpConfig;
import com.promanatia.CamelDemo.service.*;
import com.promanatia.CamelDemo.utility.ApplicationLoggerService;
import com.promanatia.CamelDemo.utility.CsvAggregationStrategy;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SftpSchedulerRouteImplementation extends RouteBuilder {

	private static final Logger logger = LoggerFactory.getLogger(SftpSchedulerRouteImplementation.class);

	private final SftpConfig sftpConfig;
	private final CsvAggregationStrategy csvAggregationStrategy;
	private final CsvValidator csvValidatorService;
	private final CsvMappingService csvMappingService;
	private final S3UploadService s3UploadService;
	private final ErrorCsvService errorCsvService;
	private final ErrorFileUploadService errorFileUploadService;
	private final ApplicationLoggerService loggerService;

	public SftpSchedulerRouteImplementation(SftpConfig sftpConfig, CsvAggregationStrategy csvAggregationStrategy,
			CsvValidator csvValidatorService, CsvMappingService csvMappingService, S3UploadService s3UploadService,
			ErrorCsvService errorCsvService, ErrorFileUploadService errorFileUploadService,
			ApplicationLoggerService loggerService) {

		this.sftpConfig = sftpConfig;
		this.csvAggregationStrategy = csvAggregationStrategy;
		this.csvValidatorService = csvValidatorService;
		this.csvMappingService = csvMappingService;
		this.s3UploadService = s3UploadService;
		this.errorCsvService = errorCsvService;
		this.errorFileUploadService = errorFileUploadService;
		this.loggerService = loggerService;
	}

	@Override
	public void configure() {

		onException(Exception.class).log("Error processing file: ${header.CamelFileName}").log("${exception.message}")
				.handled(false);

		from(sftpConfig.getSftpUri()).routeId("sftp-file-reader")

				.process(exchange -> {

					String fileName = exchange.getIn().getHeader("CamelFileName", String.class);
					FlowType flowType = FlowType.fromFileName(fileName);
					exchange.setProperty("FLOW_TYPE", flowType);
					logger.info("Started processing file : " + fileName);
				})

				.convertBodyTo(String.class).process(exchange -> {
					String body = exchange.getIn().getBody(String.class);
					int totalRows = body.split("\\r?\\n").length - 1;
					logger.info("CSV loaded successfully. Total data rows : " + totalRows);
				}).aggregate(exchangeProperty("FLOW_TYPE"), csvAggregationStrategy).completionSize(10)
				.completionTimeout(15000)

				.process(exchange -> {

					String fileContent = exchange.getIn().getBody(String.class);
					String[] rows = fileContent.split("\\r?\\n");
					csvValidatorService.validateFile(rows);
					String[] headers = rows[0].split(",", -1);
					csvValidatorService.validateHeader(headers);
					logger.info(exchange.getProperty("FLOW_TYPE", FlowType.class).name(),
							"Header validation successful.");
					exchange.setProperty("headers", headers);
					exchange.setProperty("rows", rows);
				}).process(exchange -> {

					String[] rows = exchange.getProperty("rows", String[].class);
					String[] headers = exchange.getProperty("headers", String[].class);
					java.util.List<String> validRows = new java.util.ArrayList<>();
					java.util.List<String> errorRows = new java.util.ArrayList<>();
					java.util.Set<String> failedOrders = new java.util.HashSet<>();

					FlowType flowType = exchange.getProperty("FLOW_TYPE", FlowType.class);

					for (int i = 1; i < rows.length; i++) {
						String row = rows[i];
						if (row == null || row.trim().isEmpty()) {
							continue;
						}

						String[] cols = row.split(",", -1);
						String documentNo = cols.length > 0 ? cols[0].replace("\"", "").trim() : "UNKNOWN";
						String productId = cols.length > 0 ? cols[4].replace("\"", "").trim() : "UNKNOWN";

						logger.info(productId, flowType.name(), documentNo, "Started processing CSV file.");

						try {
							csvValidatorService.validateRow(cols, headers, i, row);
							logger.info(productId, flowType.name(), documentNo,
									"Rows " + i + "validated successfully.");
						} catch (Exception e) {
							failedOrders.add(documentNo);
							logger.error("Validation failed Reason : " + e.getMessage());
							loggerService.error(productId, flowType.name(), documentNo,
									"Error processing CSV columnn file.", e.getMessage());

						}
					}

					for (int i = 1; i < rows.length; i++) {
						String row = rows[i];
						if (row == null || row.trim().isEmpty()) {
							continue;
						}

						String[] cols = row.split(",", -1);
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

					FlowType flowType = exchange.getProperty("FLOW_TYPE", FlowType.class);
					String[] headers = exchange.getProperty("headers", String[].class);
					java.util.List<String> validRows = exchange.getProperty("validRows", java.util.List.class);

					if (validRows != null && !validRows.isEmpty()) {
						String mappedCsv = csvMappingService.generateMappedCsv(flowType, headers, validRows);
						exchange.setProperty("mappedCsv", mappedCsv);
					}
				})

				.process(s3UploadService::uploadToS3)
				.toD("aws2-s3://{{aws.bucket.name}}" + "?accessKey=RAW({{aws.access.key}})"
						+ "&secretKey=RAW({{aws.secret.key}})" + "&region={{aws.region}}")

				.process(errorCsvService::generateErrorCsv).process(errorFileUploadService::uploadErrorFile);
	}
}