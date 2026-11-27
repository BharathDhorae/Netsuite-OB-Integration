package com.promantia.openbravonetsuiteintegration.repository;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.promantia.openbravonetsuiteintegration.dto.ApplicationLogDTO;

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
				    flow,
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

		jdbcTemplate.update(sql, log.getFlow(), log.getFlowType(), log.getDocumentId(), log.getLogLevel(),
				log.getMessage(), log.getErrorMessage());
	}

	public int deleteOldLogs(int retentionDays) {

		String sql = """
				DELETE FROM apache_application_logs
				WHERE log_time < CURRENT_TIMESTAMP - (? * INTERVAL '1 day')
				""";

		return jdbcTemplate.update(sql, retentionDays);
	}

	public List<ApplicationLogDTO> findLogs(int page, int size, String flow, String flowType, String logLevel,
			String logDate) {

		int offset = (page - 1) * size;

		StringBuilder sql = new StringBuilder("""
				SELECT
				    id,
				    log_time,
				    flow,
				    flow_type,
				    log_level,
				    message,
				    error_message
				FROM apache_application_logs
				WHERE 1=1
				""");

		List<Object> params = new java.util.ArrayList<>();

		// Flow Filter
		if (flow != null && !flow.isBlank()) {
			sql.append(" AND flow = ?");
			params.add(flow);
		}

		// Flow Type Filter
		if (flowType != null && !flowType.isBlank()) {
			sql.append(" AND flow_type = ?");
			params.add(flowType);
		}

		// Log Level Filter
		if (logLevel != null && !logLevel.isBlank()) {
			sql.append(" AND log_level = ?");
			params.add(logLevel);
		}

		// Log Date Filter
		if (logDate != null && !logDate.isBlank()) {

			LocalDate date = LocalDate.parse(logDate);

			sql.append(" AND log_time >= ? AND log_time < ?");

			params.add(Timestamp.valueOf(date.atStartOfDay()));
			params.add(Timestamp.valueOf(date.plusDays(1).atStartOfDay()));
		}

		sql.append(" ORDER BY log_time DESC");

		// Apply pagination only when size is greater than 0
		if (size > 0) {
			sql.append(" LIMIT ? OFFSET ?");
			params.add(size);
			params.add(offset);
		}

		return jdbcTemplate.query(sql.toString(),
				(rs, rowNum) -> ApplicationLogDTO.builder().id(rs.getLong("id")).logTime(rs.getTimestamp("log_time"))
						.flow(rs.getString("flow")).flowType(rs.getString("flow_type"))
						.logLevel(rs.getString("log_level")).message(rs.getString("message"))
						.errorMessage(rs.getString("error_message")).build(),
				params.toArray());
	}

	// Get distinct flow types for dropdown
	public List<String> getFlowTypes() {

		String sql = """
				SELECT DISTINCT flow_type
				FROM apache_application_logs
				WHERE flow_type IS NOT NULL
				ORDER BY flow_type
				""";

		return jdbcTemplate.queryForList(sql, String.class);
	}

}