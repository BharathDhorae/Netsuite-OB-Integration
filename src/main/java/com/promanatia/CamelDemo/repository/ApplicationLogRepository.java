package com.promanatia.CamelDemo.repository;

import com.promanatia.CamelDemo.DTO.ApplicationLogDTO;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ApplicationLogRepository {

	private final JdbcTemplate jdbcTemplate;

	public ApplicationLogRepository(JdbcTemplate jdbcTemplate) {

		this.jdbcTemplate = jdbcTemplate;
	}

	public void save(ApplicationLogDTO log) {

		String sql = """
				INSERT INTO apache_application_logs
				(
				    log_time,
				    product,
				    flow_type,
				    document_id,
				    log_level,
				    message,
				    error_message
				)
				VALUES
				(
				    CURRENT_TIMESTAMP,
				    ?, ?, ?, ?, ?, ?
				)
				""";

		jdbcTemplate.update(sql, log.getProduct(), log.getFlowType(), log.getDocumentId(), log.getLogLevel(),
				log.getMessage(), log.getErrorMessage());
	}

	public int deleteOldLogs(int retentionDays) {

		String sql = """
				DELETE FROM apache_application_logs
				WHERE log_time < CURRENT_TIMESTAMP - (? * INTERVAL '1 day')
				""";

		return jdbcTemplate.update(sql, retentionDays);
	}
}