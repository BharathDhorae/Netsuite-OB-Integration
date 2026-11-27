package com.promantia.openbravonetsuiteintegration.schedulars;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.promantia.openbravonetsuiteintegration.repository.ApplicationLogRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DataCleanupScheduler {

	private ApplicationLogRepository repository;

	public DataCleanupScheduler(ApplicationLogRepository repository) {
		this.repository = repository;
	}

	@Value("${log.cleanup.retention-days}")
	private int retentionDays;

	@Scheduled(cron = "${log.cleanup.cron}")
	public void cleanupOldLogs() {

		int deletedCount = repository.deleteOldLogs(retentionDays);

		log.info("Deleted {} application logs older than {} days", deletedCount, retentionDays);
	}
}