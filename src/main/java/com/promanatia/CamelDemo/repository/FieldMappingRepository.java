package com.promanatia.CamelDemo.repository;

import java.util.List;

import com.promanatia.CamelDemo.DTO.FieldMapping;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class FieldMappingRepository {

    private final JdbcTemplate jdbcTemplate;

    public FieldMappingRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<FieldMapping> getMappings(String sourceTable) {

        String sql = """
            SELECT source_column,
                   target_column,
                   transformation_rule_code,
                   sequence_no
            FROM int_m_field_mapping
            WHERE source_table = ?
            ORDER BY sequence_no
            """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> {

                    FieldMapping mapping = new FieldMapping();

                    mapping.setSourceColumn(
                            rs.getString("source_column"));

                    mapping.setTargetColumn(
                            rs.getString("target_column"));

                    mapping.setTransformationRuleCode(
                            rs.getString("transformation_rule_code"));

                    mapping.setSequenceNo(
                            rs.getInt("sequence_no"));

                    return mapping;
                },
                sourceTable);
    }
}