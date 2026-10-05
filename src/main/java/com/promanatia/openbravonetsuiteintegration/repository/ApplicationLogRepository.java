package com.promanatia.openbravonetsuiteintegration.repository;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.promanatia.openbravonetsuiteintegration.DTO.ApplicationLogDTO;

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

	public List<ApplicationLogDTO> findLogs(int page, int size) {

		int offset = (page - 1) * size;

		String sql = """
				SELECT
				    id,
				    log_time,
				    product,
				    flow_type,
				    document_id,
				    log_level,
				    message,
				    error_message
				FROM apache_application_logs
				ORDER BY log_time DESC
				LIMIT ? OFFSET ?
				""";

		return jdbcTemplate.query(sql,
				(rs, rowNum) -> ApplicationLogDTO.builder().id(rs.getLong("id")).logTime(rs.getTimestamp("log_time"))
						.product(rs.getString("product")).flowType(rs.getString("flow_type"))
						.documentId(rs.getString("document_id")).logLevel(rs.getString("log_level"))
						.message(rs.getString("message")).errorMessage(rs.getString("error_message")).build(),
				size, offset);
	}

}