package com.promanatia.openbravonetsuiteintegration.schedulars;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.promanatia.openbravonetsuiteintegration.repository.ApplicationLogRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DataCleanupScheduler {

	@Autowired
	private ApplicationLogRepository repository;

	@Value("${log.cleanup.retention-days}")
	private int retentionDays;

	@Scheduled(cron = "${log.cleanup.cron}")
	public void cleanupOldLogs() {

		int deletedCount = repository.deleteOldLogs(retentionDays);

		log.info("Deleted {} application logs older than {} days", deletedCount, retentionDays);
	}
}