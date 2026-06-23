package com.promanatia.CamelDemo.schedulars;

import com.promanatia.CamelDemo.config.SftpConfig;
import com.promanatia.CamelDemo.service.CsvProcessingService;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SftpSchedulerRouteImplementation extends RouteBuilder {

    @Autowired
    private SftpConfig sftpConfig;

    @Autowired
    private CsvProcessingService csvProcessingService;

    @Override
    public void configure() {

        from(buildSftpUri())
                .routeId("sftp-file-reader")
                .log("Processing File : ${header.CamelFileName}")

                .convertBodyTo(String.class)
                .process(csvProcessingService::processCsvFile)

                // Upload valid CSV to S3
                .choice()
                .when(exchangeProperty("hasValidRows").isEqualTo(true))
                .setHeader("CamelAwsS3Key",
                        simple("success/SORio_${date:now:yyyyMMddHHmmss}.csv"))
                .to("aws2-s3://{{aws.bucket.name}}"
                        + "?accessKey=RAW({{aws.access.key}})"
                        + "&secretKey=RAW({{aws.secret.key}})"
                        + "&region={{aws.region}}")
                .log("Valid CSV uploaded successfully to S3")
                .otherwise()
                .log("No valid rows found. Skipping S3 upload.")
                .end()

                // Write error CSV to SFTP error folder
                .choice()
                .when(exchangeProperty("hasFailedOrders").isEqualTo(true))

                .process(exchange -> {
                    String errorCsv =
                            exchange.getProperty("errorCsv", String.class);

                    exchange.getIn().setBody(errorCsv);
                })

                .log("Error CSV Body : ${body}")

                .setHeader("CamelFileName",
                        simple("SOR_ERROR_${date:now:yyyyMMddHHmmss}.csv"))

                .to(buildSftpErrorUri())

                .log("Error CSV written to SFTP error folder")

                .otherwise()
                .log("No failed orders found. Skipping error file creation.")
                .end();
    }

    private String buildSftpUri() {
        return "sftp://" + sftpConfig.getHost()
                + ":" + sftpConfig.getPort()
                + sftpConfig.getRemoteDirectory()
                + "?username=" + sftpConfig.getUsername()
                + "&password=" + sftpConfig.getPassword()
                + "&delete=" + sftpConfig.isDelete()
                + "&include=" + sftpConfig.getInclude()
                + "&delay=" + sftpConfig.getDelay();
    }

    private String buildSftpErrorUri() {
        return "sftp://" + sftpConfig.getHost()
                + ":" + sftpConfig.getPort()
                + sftpConfig.getErrorDirectory()
                + "?username=" + sftpConfig.getUsername()
                + "&password=" + sftpConfig.getPassword();
    }
}