package com.promanatia.CamelDemo.repository;

import com.promanatia.CamelDemo.DTO.FieldMappingEntity;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FieldMappingRepository {

	private final JdbcTemplate jdbcTemplate;

	public FieldMappingRepository(@Qualifier("mainJdbcTemplate") JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public List<FieldMappingEntity> getMappings(String sourceTable) {

		String sql = """
				SELECT
				    source_column,
				    target_column,
				    transformation_rule_code,
				    sequence_no
				FROM int_m_field_mapping
				WHERE source_table = ?
				ORDER BY sequence_no
				""";

		return jdbcTemplate.query(sql, (rs, rowNum) -> {
			FieldMappingEntity mapping = new FieldMappingEntity();
			mapping.setSourceColumn(rs.getString("source_column"));
			mapping.setTargetColumn(rs.getString("target_column"));
			mapping.setTransformationRuleCode(rs.getString("transformation_rule_code"));
			mapping.setSequenceNo(rs.getInt("sequence_no"));
			return mapping;
		}, sourceTable);
	}
}