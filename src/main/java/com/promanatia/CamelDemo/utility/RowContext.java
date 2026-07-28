package com.promanatia.CamelDemo.utility;

import java.util.HashMap;
import java.util.Map;

import com.promanatia.CamelDemo.DTO.OrgSubsidaryDto;
import com.promanatia.CamelDemo.DTO.ProductMasterDTO;

public class RowContext {

	private static final String BP_FIRST_NAME = "BusinessPartnerFirstName";

	private final Map<String, String> values;

	/**
	 * Cached values for this row only
	 */
	private String department;

	private OrgSubsidaryDto organization;

	private OrgSubsidaryDto businessPartner;

	private ProductMasterDTO product;

	public RowContext(String[] headers, String[] columns) {

		values = new HashMap<>(headers.length);

		for (int i = 0; i < headers.length; i++) {

			values.put(headers[i].toLowerCase(), i < columns.length && columns[i] != null ? columns[i].trim() : "");
		}
	}

	/**
	 * Get value by column name.
	 */
	public String get(String columnName) {

		if (columnName == null) {
			return "";
		}

		return values.getOrDefault(columnName.toLowerCase(), "");
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

		if (bp.contains("baps shayona")) {

			department = "3255";

		} else if (bp.contains("baps swaminarayan sanstha")) {

			department = "3215";

		} else if (bp.contains("swaminarayan aksharpith")) {

			department = "3115";

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

}