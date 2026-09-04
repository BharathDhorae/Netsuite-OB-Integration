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

	public String findBySubsdiaryForProduct(String subsidary) {

		String sql = """
				SELECT org_name
				FROM int_m_lookup_org
				WHERE subsdiary_id=?
				AND isactive='Y'
				""";

		try {
			return jdbcTemplate.queryForObject(sql, String.class, subsidary);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public OrgSubsidaryDto findBySubsidiary(String subsidiary, String vendorSubsidiary) {

		String sql = """
				SELECT
				    ad_org_name,
				    subsidiary,
				    aksharpith_subsidiary,
				    itemLine_location,
				    internal_vendor,
				    financial_location,
				    internal_customer
				FROM ob_ns_org_v3
				WHERE
				    (subsidiary = ?
				     OR aksharpith_subsidiary = ?)
				    AND internal_vendor IS NOT NULL
				    AND isactive='Y'
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
			}, subsidiary, vendorSubsidiary);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
}
