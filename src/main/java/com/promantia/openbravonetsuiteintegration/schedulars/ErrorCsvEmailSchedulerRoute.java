package com.promantia.openbravonetsuiteintegration.schedulars;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.promantia.openbravonetsuiteintegration.config.EmailNotificationConfig;
import com.promantia.openbravonetsuiteintegration.config.SftpConfig;
import com.promantia.openbravonetsuiteintegration.service.ErrorCsvEmailService;
import com.promantia.openbravonetsuiteintegration.service.ErrorCsvFile;

@Component
public class ErrorCsvEmailSchedulerRoute extends RouteBuilder {

	private static final Logger logger = LoggerFactory.getLogger(ErrorCsvEmailSchedulerRoute.class);

	private final SftpConfig sftpConfig;
	private final EmailNotificationConfig notificationProperties;
	private final ErrorCsvEmailService emailService;

	public ErrorCsvEmailSchedulerRoute(SftpConfig sftpConfig, EmailNotificationConfig notificationProperties,
			ErrorCsvEmailService emailService) {
		this.sftpConfig = sftpConfig;
		this.notificationProperties = notificationProperties;
		this.emailService = emailService;
	}

	@Override
	public void configure() {
		if (notificationProperties.getRecipients().stream()
				.noneMatch(recipient -> recipient != null && !recipient.isBlank())) {
			logger.warn("SFTP error email routes are disabled because error-notification.recipients is empty");
			return;
		}

		configureErrorFolder("ob-to-netsuite-error-email", "Openbravo-to-NetSuite",
				sftpConfig.getObToNetsuiteErrorNotificationEndpoint());
		configureErrorFolder("netsuite-to-ob-error-email", "NetSuite-to-Openbravo",
				sftpConfig.getNetsuiteToObErrorNotificationEndpoint());
	}

	private void configureErrorFolder(String routeId, String source,
			org.apache.camel.builder.EndpointConsumerBuilder endpoint) {
		from(endpoint).routeId(routeId).convertBodyTo(String.class).process(exchange -> {
			String fileName = exchange.getIn().getHeader("CamelFileNameOnly", String.class);
			String contents = exchange.getIn().getBody(String.class);
			exchange.getIn().setBody(new ErrorCsvFile(source, fileName, contents.getBytes(StandardCharsets.UTF_8)));
		}).aggregate(org.apache.camel.builder.Builder.constant(true), this::aggregateFiles)
				.completionFromBatchConsumer()
				.process(exchange -> emailService.sendZip(exchange.getIn().getBody(List.class)));
	}

	@SuppressWarnings("unchecked")
	private Exchange aggregateFiles(Exchange oldExchange, Exchange newExchange) {
		ErrorCsvFile file = newExchange.getIn().getBody(ErrorCsvFile.class);
		if (oldExchange == null) {
			newExchange.getIn().setBody(new ArrayList<>(List.of(file)));
			return newExchange;
		}

		oldExchange.getIn().getBody(List.class).add(file);
		return oldExchange;
	}
}