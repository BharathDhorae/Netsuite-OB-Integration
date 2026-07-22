package com.promanatia.CamelDemo.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.promanatia.CamelDemo.DTO.OrgSubsidaryDto;

@Repository
public class OrgSubsidaryRepository {

	private final JdbcTemplate jdbcTemplate;

	public OrgSubsidaryRepository(@Qualifier("mainJdbcTemplate") JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public OrgSubsidaryDto findByEntityName(String orgName) {

		String sql = """
				SELECT ad_org_name,subsidiary,
				aksharpith_subsidiary,itemLine_location
				FROM ob_ns_org_v3
				WHERE ad_org_name=?
				AND isactive='Y'
				""";

		return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {

			OrgSubsidaryDto entity = new OrgSubsidaryDto();
			entity.setOrgSubsidaryName(rs.getString("ad_org_name"));
			entity.setSubsidary(rs.getString("subsidiary"));
			entity.setAksharpithSubsidary(rs.getString("aksharpith_subsidiary"));
			entity.setItemLineLocation(rs.getString("itemLine_location"));

			return entity;
		}, orgName);
	}

}
