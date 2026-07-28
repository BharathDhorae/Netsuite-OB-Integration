package com.promanatia.CamelDemo.repository;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.promanatia.CamelDemo.DTO.EntityMasterDTO;

@Repository
public class EntityMasterRepository {

	private final JdbcTemplate jdbcTemplate;

	public EntityMasterRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public EntityMasterDTO findByEntityName(String entityName, String sourceSystem, String targetSystem) {

		String sql = """
				SELECT *
				FROM int_m_entity_master
				WHERE upper(entity_name)=upper(?)
				AND upper(source_system)=upper(?)
				AND upper(target_system)=upper(?)
				AND active='Y'
				""";
		try {
			return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {

				EntityMasterDTO entity = new EntityMasterDTO();

				entity.setEntityId(rs.getLong("entity_id"));
				entity.setEntityName(rs.getString("entity_name"));
				entity.setSourceTableName(rs.getString("source_table_name"));
				entity.setTargetObjectName(rs.getString("target_object_name"));
				entity.setSourceSystem(rs.getString("source_system"));
				entity.setTargetSystem(rs.getString("target_system"));

				return entity;
			}, entityName, sourceSystem, targetSystem);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
}