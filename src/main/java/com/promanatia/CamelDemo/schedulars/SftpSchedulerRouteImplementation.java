package com.promanatia.CamelDemo.schedulars;

import com.promanatia.CamelDemo.config.SftpConfig;
import com.promanatia.CamelDemo.service.CsvProcessingService;
import com.promanatia.CamelDemo.service.SftpArchiveService;
import com.promanatia.CamelDemo.utility.CsvAggregationStrategy;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class SftpSchedulerRouteImplementation extends RouteBuilder {

    private final SftpConfig sftpConfig;
    private final CsvProcessingService csvProcessingService;
    private final CsvAggregationStrategy csvAggregationStrategy;
    private final SftpArchiveService sftpArchiveService;

    public SftpSchedulerRouteImplementation(
            SftpConfig sftpConfig,
            CsvProcessingService csvProcessingService,
            CsvAggregationStrategy csvAggregationStrategy,
            SftpArchiveService sftpArchiveService) {

        this.sftpConfig = sftpConfig;
        this.csvProcessingService = csvProcessingService;
        this.csvAggregationStrategy = csvAggregationStrategy;
        this.sftpArchiveService = sftpArchiveService;
    }

    @Override
    public void configure() {

        from(buildSftpUri())
                .routeId("sftp-file-reader")

                .convertBodyTo(String.class)

                .aggregate(constant(true), csvAggregationStrategy)
                .completionSize(10)
                .completionTimeout(15000)

                // Process CSV
                .process(csvProcessingService::processCsvFile)

                /*
                 * Upload valid rows to S3
                 */
                .choice()
                .when(exchangeProperty("hasValidRows").isEqualTo(true))

                .log("Uploading valid rows to S3...")

                .setBody(exchangeProperty("mappedCsv"))

                .setHeader("CamelAwsS3Key",
                        simple("Test/SalesOrder_${date:now:yyyyMMddHHmmss}.csv"))

                .to("aws2-s3://{{aws.bucket.name}}"
                        + "?accessKey=RAW({{aws.access.key}})"
                        + "&secretKey=RAW({{aws.secret.key}})"
                        + "&region={{aws.region}}")

                .log("S3 upload completed successfully.")
                .end()

                /*
                 * Upload invalid rows as Error CSV
                 */
                .choice()
                .when(exchangeProperty("hasFailedOrders").isEqualTo(true))

                .log("Creating Error CSV...")

                .process(exchange -> {
                    String errorCsv =
                            exchange.getProperty("errorCsv", String.class);

                    exchange.getIn().setBody(errorCsv == null ? "" : errorCsv);
                })

                .setHeader("CamelFileName",
                        simple("ERROR_${date:now:yyyyMMddHHmmss}.csv"))

                .to(buildSftpErrorUri())

                .log("Error CSV uploaded successfully.")
                .end()

                /*
                 * Always archive original files
                 */
                .process(sftpArchiveService::archiveProcessedFiles)

                .log("Original source files archived successfully.");
    }

    private String buildSftpUri() {

        return "sftp://"
                + sftpConfig.getHost()
                + ":"
                + sftpConfig.getPort()
                + sftpConfig.getRemoteDirectory()
                + "?username="
                + sftpConfig.getUsername()
                + "&password="
                + sftpConfig.getPassword()
                + "&include="
                + sftpConfig.getInclude()
                + "&delay="
                + sftpConfig.getDelay();
    }

    private String buildSftpErrorUri() {

        return "sftp://"
                + sftpConfig.getHost()
                + ":"
                + sftpConfig.getPort()
                + sftpConfig.getErrorDirectory()
                + "?username="
                + sftpConfig.getUsername()
                + "&password="
                + sftpConfig.getPassword();
    }
}
