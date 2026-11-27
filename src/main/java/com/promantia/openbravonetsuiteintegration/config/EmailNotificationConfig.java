package com.promantia.openbravonetsuiteintegration.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "error-notification")
public class EmailNotificationConfig {

	private List<String> recipients = new ArrayList<>();
	private String subject = "SFTP error CSV notification";
	private long pollDelay = 60000;

	public List<String> getRecipients() {
		return recipients;
	}

	public void setRecipients(List<String> recipients) {
		this.recipients = recipients == null ? new ArrayList<>() : recipients;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public long getPollDelay() {
		return pollDelay;
	}

	public void setPollDelay(long pollDelay) {
		this.pollDelay = pollDelay;
	}
}