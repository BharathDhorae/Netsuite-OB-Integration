package com.promanatia.CamelDemo.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.promanatia.CamelDemo.DTO.ProductMasterDTO;

@Repository
public class ProductMasterRepository {
	private final JdbcTemplate jdbcTemplate;

	public ProductMasterRepository(@Qualifier("mainJdbcTemplate") JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public ProductMasterDTO findByEntityName(String searchKey) {

		String sql = """
				SELECT classexternalid, productcategoryexternalid,subclassexternalid
				FROM int_m_product
				WHERE searchkey = ?
				  AND isactive = 'Y'
				  Limit 1
				""";

		try {
			return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
				ProductMasterDTO entity = new ProductMasterDTO();
				entity.setClassExternalId(rs.getString("classexternalid"));
				entity.setProductCategoryExternalId(rs.getString("productcategoryexternalid"));
				entity.setSubClassExternalId(rs.getString("subclassexternalid"));
				return entity;
			}, searchKey);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
}