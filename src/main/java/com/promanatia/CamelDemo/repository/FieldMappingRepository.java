package com.promanatia.CamelDemo.repository;

import com.promanatia.CamelDemo.DTO.FieldMappingDTO;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FieldMappingRepository {

	private final JdbcTemplate jdbcTemplate;

	public FieldMappingRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public List<FieldMappingDTO> getMappings(String sourceTable) {

		String sql = """
				SELECT
				    source_column,
				    target_column,
				    transformation_rule_code,
				    sequence_no,
				    mandatory,
				    default_value
				FROM int_m_field_mapping
				WHERE source_table = ? and active = 'Y'
				ORDER BY sequence_no
				""";

		return jdbcTemplate.query(sql, (rs, rowNum) -> {

			FieldMappingDTO mapping = new FieldMappingDTO();

			mapping.setSourceColumn(rs.getString("source_column"));
			mapping.setTargetColumn(rs.getString("target_column"));
			mapping.setTransformationRuleCode(rs.getString("transformation_rule_code"));
			mapping.setSequenceNo(rs.getInt("sequence_no"));
			mapping.setMandatory(rs.getString("mandatory"));
			mapping.setDefaultValue(rs.getString("default_value"));

			return mapping;

		}, sourceTable);
	}

	public String getIdentificationColumn(String sourceTable) {

		String sql = """
				SELECT source_column
				FROM int_m_field_mapping
				WHERE source_table = ?
				  AND identification_primary_column = 'Y'
				  AND active = 'Y'
				ORDER BY sequence_no
				""";

		try {
			return jdbcTemplate.queryForObject(sql, String.class, sourceTable);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
}