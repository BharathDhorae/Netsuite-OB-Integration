package com.promanatia.CamelDemo.schedulars;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.promanatia.CamelDemo.repository.ApplicationLogRepository;

@Component
public class DataCleanupScheduler {

	protected static final Logger logger = LoggerFactory.getLogger(DataCleanupScheduler.class);

	@Autowired
	private ApplicationLogRepository repository;

	@Value("${log.cleanup.retention-days}")
	private int retentionDays;

	@Scheduled(cron = "${log.cleanup.cron}")
	public void cleanupOldLogs() {

		int deletedCount = repository.deleteOldLogs(retentionDays);

		logger.info("Deleted {} application logs older than {} days", deletedCount, retentionDays);
	}
}