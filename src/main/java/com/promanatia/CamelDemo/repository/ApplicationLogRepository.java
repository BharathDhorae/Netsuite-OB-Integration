package com.promanatia.CamelDemo.repository;

import com.promanatia.CamelDemo.DTO.ApplicationLogEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ApplicationLogRepository {

    private final JdbcTemplate jdbcTemplate;

    public ApplicationLogRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(ApplicationLogEntity log) {

        String sql = """
                INSERT INTO application_logs
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

        jdbcTemplate.update(
                sql,
                log.getProduct(),
                log.getFlowType(),
                log.getDocumentId(),
                log.getLogLevel(),
                log.getMessage(),
                log.getErrorMessage()
        );
    }
}