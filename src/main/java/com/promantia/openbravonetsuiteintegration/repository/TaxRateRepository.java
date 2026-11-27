package com.promantia.openbravonetsuiteintegration.repository;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class TaxRateRepository {

	private final JdbcTemplate jdbcTemplate;

	public TaxRateRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public String getTaxCategory(String externalId, String taxGroup) {

		String sql = """
				SELECT ob_tax_category
				FROM int_m_tax_rate
				WHERE external_id = ?
				  AND  tax_grop = ?
				  AND active = 'Y'
				  LIMIT 1
				""";

		try {
			return jdbcTemplate.queryForObject(sql, String.class, externalId, taxGroup);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
}
