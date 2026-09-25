package com.promanatia.CamelDemo.repository;

import java.util.ArrayList;
import java.util.List;

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
		return findByOrganizationName(orgName, null);
	}

	public OrgSubsidaryDto findByOrganizationName(String orgName, String itemLineLocation) {

		StringBuilder sql = new StringBuilder("""
				SELECT ad_org_name,
				       subsidiary,
				       aksharpith_subsidiary,
				       itemline_location,
				       internal_vendor,
				       financial_location,
				       internal_customer,
				       external_id_inventory_location
				FROM ob_ns_org_v3
				WHERE isactive = 'Y'
				""");

		List<Object> params = new ArrayList<>();

		if (orgName != null && !orgName.isBlank()) {
			sql.append(" AND LOWER(ad_org_name) = LOWER(?)");
			params.add(orgName);
		}

		if (itemLineLocation != null && !itemLineLocation.isBlank()) {
			sql.append(" LOWER(itemline_location) = LOWER(?)");
			params.add(itemLineLocation);
		}

		sql.append(" LIMIT 1");

		try {
			return jdbcTemplate.queryForObject(sql.toString(), (rs, rowNum) -> {

				OrgSubsidaryDto entity = new OrgSubsidaryDto();

				entity.setOrgSubsidaryName(rs.getString("ad_org_name"));
				entity.setSubsidary(rs.getString("subsidiary"));
				entity.setAksharpithSubsidary(rs.getString("aksharpith_subsidiary"));
				entity.setItemLineLocation(rs.getString("itemline_location"));
				entity.setInternalVendor(rs.getString("internal_vendor"));
				entity.setFinancialLocation(rs.getString("financial_location"));
				entity.setInternalCustomer(rs.getString("internal_customer"));
				entity.setExternalInventoryLocation(rs.getString("external_id_inventory_location"));

				return entity;
			}, params.toArray());

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

	public String getOrgNameByLocationColumn(String itemLineLocation) {

		String sql = """
				SELECT ad_org_nme
				FROM ob_ns_org_v3
				WHERE itemline_location = ?
				AND isactive='Y'
				""";

		try {
			return jdbcTemplate.queryForObject(sql, String.class, itemLineLocation);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public String findExternalInventoryLocation(String itemLineLocation) {

		String sql = """
				 SELECT external_id_inventory_location
				FROM ob_ns_org_v3
				WHERE LOWER(ad_org_name) LIKE LOWER('%' || ?)
				  AND isactive = 'Y'
				LIMIT 1
				""";

		try {
			return jdbcTemplate.queryForObject(sql, String.class, itemLineLocation);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
}
