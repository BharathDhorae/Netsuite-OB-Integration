package com.promanatia.CamelDemo.utility;

import java.util.HashMap;
import java.util.Map;

import com.promanatia.CamelDemo.DTO.OrgSubsidaryDto;
import com.promanatia.CamelDemo.DTO.ProductMasterDTO;

public class RowContext {

	private static final String BP_FIRST_NAME = "BusinessPartnerFirstName";
	private static final String ORG_NAME = "Organization";

	private final Map<String, String> values;

	/**
	 * Cached values for this row only
	 */
	private String department;

	private OrgSubsidaryDto organization;

	private OrgSubsidaryDto businessPartner;

	private ProductMasterDTO product;

	private OrgSubsidaryDto subsidiaryDto;

	public RowContext(String[] headers, String[] columns) {

		values = new HashMap<>(headers.length);

		for (int i = 0; i < headers.length; i++) {

			String header = normalize(headers[i]);

			String value = i < columns.length && columns[i] != null ? columns[i].trim() : "";

			values.put(header, value);
		}
	}

	private String normalize(String value) {

		if (value == null) {
			return "";
		}

		return value.trim().replaceAll("\\s+", " ").toLowerCase();
	}

	/**
	 * Get value by column name.
	 */
	public String get(String key) {

		if (key == null) {
			return "";
		}

		return values.getOrDefault(normalize(key), "");
	}

	/**
	 * Check if value exists.
	 */
	public boolean hasValue(String columnName) {

		return !get(columnName).isBlank();
	}

	/**
	 * Department is calculated only once.
	 */
	public String getDepartment() {

		if (department != null) {
			return department;
		}

		String bp = get(BP_FIRST_NAME).toLowerCase();
		String orgName = get(ORG_NAME).toLowerCase();

		if (bp.contains("baps shayona")) {

			department = "3255";

		} else if (bp.contains("baps swaminarayan sanstha")) {

			department = "3215";

		} else if (bp.contains("swaminarayan aksharpith")) {

			department = "3115";

		} else if (orgName.contains("aksharpith online") || bp.contains("akshar.org online")) {

			department = "3330";

		} else {

			department = "3230";
		}

		return department;
	}

	public boolean isInterCompany() {

		String dept = getDepartment();

		return "3115".equals(dept) || "3255".equals(dept);
	}

	/*
	 * Cached DTOs
	 */

	public OrgSubsidaryDto getOrganization() {
		return organization;
	}

	public void setOrganization(OrgSubsidaryDto organization) {
		this.organization = organization;
	}

	public OrgSubsidaryDto getBusinessPartner() {
		return businessPartner;
	}

	public void setBusinessPartner(OrgSubsidaryDto businessPartner) {
		this.businessPartner = businessPartner;
	}

	public ProductMasterDTO getProduct() {
		return product;
	}

	public void setProduct(ProductMasterDTO product) {
		this.product = product;
	}

	public OrgSubsidaryDto getSubsidiaryDto() {
		return subsidiaryDto;
	}

	public void setSubsidiaryDto(OrgSubsidaryDto subsidiaryDto) {
		this.subsidiaryDto = subsidiaryDto;
	}

}