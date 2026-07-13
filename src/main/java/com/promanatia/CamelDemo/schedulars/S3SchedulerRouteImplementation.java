package com.promanatia.CamelDemo.schedulars;

import com.promanatia.CamelDemo.DTO.FlowType;
import com.promanatia.CamelDemo.config.S3Config;
import com.promanatia.CamelDemo.service.*;
import com.promanatia.CamelDemo.utility.ApplicationLoggerService;
import com.promanatia.CamelDemo.utility.CsvAggregationStrategy;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import com.promanatia.CamelDemo.config.SftpConfig;

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

	public S3SchedulerRouteImplementation(S3Config s3Config, CsvAggregationStrategy csvAggregationStrategy,
			CsvValidator csvValidatorService, CsvMappingService csvMappingService, SftpUploadService sftpUploadService,
			ErrorCsvService errorCsvService, S3UploadService s3UploadService, ApplicationLoggerService loggerService,
			SftpConfig sftpConfig) {

		this.s3Config = s3Config;
		this.csvAggregationStrategy = csvAggregationStrategy;
		this.csvValidatorService = csvValidatorService;
		this.csvMappingService = csvMappingService;
		this.sftpUploadService = sftpUploadService;
		this.errorCsvService = errorCsvService;
		this.s3UploadService = s3UploadService;
		this.loggerService = loggerService;
		this.sftpConfig = sftpConfig;
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

					FlowType flowType = FlowType.fromFileName(fileName);
					exchange.setProperty("FLOW_TYPE", flowType);
				}).convertBodyTo(String.class).aggregate(exchangeProperty("FLOW_TYPE"), csvAggregationStrategy)
				.completionSize(10).completionTimeout(15000)

				.process(exchange -> {

					String fileContent = exchange.getIn().getBody(String.class);
					String[] rows = fileContent.split("\\r?\\n");
					csvValidatorService.validateFile(rows);
					String[] headers = rows[0].split(",", -1);
					csvValidatorService.validateHeader(headers);
					exchange.setProperty("headers", headers);
					exchange.setProperty("rows", rows);
				}).process(exchange -> {

					String[] rows = exchange.getProperty("rows", String[].class);
					String[] headers = exchange.getProperty("headers", String[].class);
					java.util.List<String> validRows = new java.util.ArrayList<>();
					java.util.List<String> errorRows = new java.util.ArrayList<>();
					java.util.Set<String> failedOrders = new java.util.HashSet<>();

					for (int i = 1; i < rows.length; i++) {
						String row = rows[i];
						if (row == null || row.trim().isEmpty()) {
							continue;
						}

						String[] cols = row.split(",", -1);
						String documentNo = cols.length > 0 ? cols[0].replace("\"", "").trim() : "UNKNOWN";
						try {
							csvValidatorService.validateRow(cols, headers, i, row);
						} catch (Exception e) {
							failedOrders.add(documentNo);
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

				.process(sftpUploadService::uploadSuccessFile).toD(sftpConfig.getInSftpEndpoint())
				.process(errorCsvService::generateErrorCsv).process(s3UploadService::uploadErrorFile)
				.toD(s3Config.getWriteUri());
	}
}