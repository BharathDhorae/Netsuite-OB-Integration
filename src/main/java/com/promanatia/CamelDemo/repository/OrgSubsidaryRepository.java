package com.promanatia.CamelDemo.repository;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.promanatia.CamelDemo.DTO.OrgSubsidaryDto;

@Repository
public class OrgSubsidaryRepository {

	private final JdbcTemplate jdbcTemplate;

	public OrgSubsidaryRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public OrgSubsidaryDto findByOrganizationName(String orgName) {

		String sql = """
				SELECT ad_org_name,
				       subsidiary,
				       aksharpith_subsidiary,
				       itemline_location,
				       internal_vendor,
				       financial_location,
				       internal_customer
				FROM ob_ns_org_v3
				WHERE ad_org_name=?
				AND isactive='Y'
				""";
		try {
			return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {

				OrgSubsidaryDto entity = new OrgSubsidaryDto();
				entity.setOrgSubsidaryName(rs.getString("ad_org_name"));
				entity.setSubsidary(rs.getString("subsidiary"));
				entity.setAksharpithSubsidary(rs.getString("aksharpith_subsidiary"));
				entity.setItemLineLocation(rs.getString("itemline_location"));
				entity.setInternalVendor(rs.getString("internal_vendor"));
				entity.setFinancialLocation(rs.getString("financial_location"));
				entity.setInternalCustomer(rs.getString("internal_customer"));

				return entity;
			}, orgName);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public OrgSubsidaryDto findByBusinessPartner(String businessPartner) {

		String sql = """
				SELECT ad_org_name,
				       subsidiary,
				       aksharpith_subsidiary,
				       itemline_location,
				       internal_vendor,
				       financial_location,
				       internal_customer
				FROM ob_ns_org_v3
				WHERE LOWER(ob_business_partner) LIKE LOWER(? || '%')
				  AND isactive = 'Y'
				LIMIT 1
				""";

		try {
			return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {

				OrgSubsidaryDto entity = new OrgSubsidaryDto();
				entity.setOrgSubsidaryName(rs.getString("ad_org_name"));
				entity.setSubsidary(rs.getString("subsidiary"));
				entity.setAksharpithSubsidary(rs.getString("aksharpith_subsidiary"));
				entity.setItemLineLocation(rs.getString("itemline_location"));
				entity.setInternalVendor(rs.getString("internal_vendor"));
				entity.setFinancialLocation(rs.getString("financial_location"));
				entity.setInternalCustomer(rs.getString("internal_customer"));

				return entity;
			}, businessPartner);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public String findBySusidary(String subsidary) {

		String sql = """
				SELECT ad_org_name
				FROM ob_ns_org_v3
				WHERE subsidiary=?
				AND isactive='Y'
				Limit 1
				""";

		try {
			return jdbcTemplate.queryForObject(sql, String.class, subsidary);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

}
