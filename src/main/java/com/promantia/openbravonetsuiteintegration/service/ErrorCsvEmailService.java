package com.promantia.openbravonetsuiteintegration.service;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.promantia.openbravonetsuiteintegration.config.EmailNotificationConfig;

import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class ErrorCsvEmailService {


	private static final DateTimeFormatter FILE_TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");


	private final JavaMailSender mailSender;
	private final EmailNotificationConfig properties;


	@Value("${spring.mail.username}")
	private String username;


	public ErrorCsvEmailService(JavaMailSender mailSender, EmailNotificationConfig properties) {
		this.mailSender = mailSender;
		this.properties = properties;
	}


	public void sendZip(List<ErrorCsvFile> files) {


		List<String> recipients = properties.getRecipients().stream().filter(Objects::nonNull).map(String::trim)
				.filter(recipient -> !recipient.isBlank()).toList();


		if (files == null || files.isEmpty() || recipients.isEmpty()) {
			return;
		}


		log.info("Preparing SFTP error notification for {}", recipients);
		String zipName = "sftp-errors-" + LocalDateTime.now().format(FILE_TIMESTAMP) + ".zip";
		byte[] zipContents = createZip(files);
		log.info("ZIP created: {}, size: {} bytes", zipName, zipContents.length);


		try {
			MimeMessage mimeMessage = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
			helper.setFrom(username);
			helper.setTo(recipients.toArray(new String[0]));
			helper.setSubject(properties.getSubject());
			helper.setText("Attached are " + files.size() + " CSV file(s) from the SFTP error folders.");
			helper.addAttachment(zipName, new ByteArrayResource(zipContents), "application/zip");


			log.info("Starting SMTP send operation...");
			mailSender.send(mimeMessage);
			log.info("Email sent successfully to {}", recipients);


		} catch (Exception e) {


			log.error("Failed to send SFTP error notification email", e);
			throw new IllegalStateException("Failed to send SFTP error notification email", e);
		}
	}


	private byte[] createZip(List<ErrorCsvFile> files) {
		try (ByteArrayOutputStream bytes = new ByteArrayOutputStream();
				ZipOutputStream zip = new ZipOutputStream(bytes, StandardCharsets.UTF_8)) {
			for (ErrorCsvFile file : files) {
				String entryName = safeName(file.source()) + "/" + safeName(file.fileName());
				zip.putNextEntry(new ZipEntry(entryName));
				zip.write(file.contents());
				zip.closeEntry();
			}
			zip.finish();
			return bytes.toByteArray();
		} catch (IOException e) {
			throw new IllegalStateException("Failed to create in-memory ZIP for SFTP error files", e);
		}
	}


	private String safeName(String value) {
		if (value == null || value.isBlank()) {
			return "unknown";
		}
		String normalized = value.replace('\\', '/');
		return normalized.substring(normalized.lastIndexOf('/') + 1);
	}
}