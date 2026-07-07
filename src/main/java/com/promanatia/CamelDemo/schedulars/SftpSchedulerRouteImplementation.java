package com.promanatia.CamelDemo.schedulars;

import com.promanatia.CamelDemo.DTO.FlowType;
import com.promanatia.CamelDemo.config.SftpConfig;
import com.promanatia.CamelDemo.service.*;
import com.promanatia.CamelDemo.utility.CsvAggregationStrategy;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class SftpSchedulerRouteImplementation extends RouteBuilder {

    private final SftpConfig sftpConfig;

    private final CsvAggregationStrategy csvAggregationStrategy;
    private final CsvValidator csvValidatorService;
    private final CsvMappingService csvMappingService;
    private final S3UploadService s3UploadService;
    private final ErrorCsvService errorCsvService;
    private final ErrorFileUploadService errorFileUploadService;
    private final SftpArchiveService sftpArchiveService;

    public SftpSchedulerRouteImplementation(
            SftpConfig sftpConfig,
            CsvAggregationStrategy csvAggregationStrategy,
            CsvValidator csvValidatorService,
            CsvMappingService csvMappingService,
            S3UploadService s3UploadService,
            ErrorCsvService errorCsvService,
            ErrorFileUploadService errorFileUploadService,
            SftpArchiveService sftpArchiveService) {

        this.sftpConfig = sftpConfig;
        this.csvAggregationStrategy = csvAggregationStrategy;
        this.csvValidatorService = csvValidatorService;
        this.csvMappingService = csvMappingService;
        this.s3UploadService = s3UploadService;
        this.errorCsvService = errorCsvService;
        this.errorFileUploadService = errorFileUploadService;
        this.sftpArchiveService = sftpArchiveService;
    }

    @Override
    public void configure() {

        onException(Exception.class)
                .log("Error processing file: ${header.CamelFileName}")
                .log("${exception.message}")
                .handled(true);

        from(buildSftpUri())
                .routeId("sftp-file-reader")

                .process(exchange -> {

                    String fileName =
                            exchange.getIn().getHeader("CamelFileName", String.class);

                    FlowType flowType =
                            FlowType.fromFileName(fileName);

                    exchange.setProperty("FLOW_TYPE", flowType);
                })

                .convertBodyTo(String.class)

                .aggregate(exchangeProperty("FLOW_TYPE"), csvAggregationStrategy)
                .completionSize(10)
                .completionTimeout(15000)

                .process(exchange -> {

                    String fileContent =
                            exchange.getIn().getBody(String.class);

                    String[] rows =
                            fileContent.split("\\r?\\n");

                    csvValidatorService.validateFile(rows);

                    String[] headers =
                            rows[0].split(",", -1);

                    csvValidatorService.validateHeader(headers);

                    exchange.setProperty("headers", headers);
                    exchange.setProperty("rows", rows);
                })
                .process(exchange -> {

                    String[] rows =
                            exchange.getProperty("rows", String[].class);

                    String[] headers =
                            exchange.getProperty("headers", String[].class);

                    java.util.List<String> validRows =
                            new java.util.ArrayList<>();

                    java.util.List<String> errorRows =
                            new java.util.ArrayList<>();

                    java.util.Set<String> failedOrders =
                            new java.util.HashSet<>();

                    for (int i = 1; i < rows.length; i++) {

                        String row = rows[i];

                        if (row == null || row.trim().isEmpty()) {
                            continue;
                        }

                        String[] cols = row.split(",", -1);

                        String documentNo =
                                cols.length > 0
                                        ? cols[0].replace("\"", "").trim()
                                        : "UNKNOWN";

                        try {

                            csvValidatorService.validateRow(
                                    cols,
                                    headers,
                                    i,
                                    row);

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

                        String documentNo =
                                cols.length > 0
                                        ? cols[0].replace("\"", "").trim()
                                        : "UNKNOWN";

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

                    FlowType flowType =
                            exchange.getProperty("FLOW_TYPE", FlowType.class);

                    String[] headers =
                            exchange.getProperty("headers", String[].class);

                    java.util.List<String> validRows =
                            exchange.getProperty("validRows", java.util.List.class);

                    if (validRows != null && !validRows.isEmpty()) {

                        String mappedCsv =
                                csvMappingService.generateMappedCsv(
                                        flowType,
                                        headers,
                                        validRows);

                        exchange.setProperty("mappedCsv", mappedCsv);
                    }
                })

                .process(s3UploadService::uploadToS3)

                .toD("aws2-s3://{{aws.bucket.name}}"
                        + "?accessKey=RAW({{aws.access.key}})"
                        + "&secretKey=RAW({{aws.secret.key}})"
                        + "&region={{aws.region}}")

                .process(errorCsvService::generateErrorCsv)

                .process(errorFileUploadService::uploadErrorFile)

                .toD("${exchangeProperty.ERROR_SFTP_URI}")

                .process(sftpArchiveService::archiveProcessedFiles);
    }

    private String buildSftpUri() {

        return "sftp://"
                + sftpConfig.getHost()
                + ":"
                + sftpConfig.getPort()
                + sftpConfig.getRemoteDirectory()

                + "?username=" + sftpConfig.getUsername()
                + "&password=" + sftpConfig.getPassword()
                + "&include=" + sftpConfig.getInclude()
                + "&delay=" + sftpConfig.getDelay()

                + "&move=" + sftpConfig.getArchiveDirectory()
                + "/${file:name}"

                + "&moveFailed=" + sftpConfig.getErrorDirectory()
                + "/${file:name}"

                + "&readLock=changed";
    }
}