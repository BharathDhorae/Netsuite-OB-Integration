package com.promanatia.CamelDemo.transformation.openbravo.salesinvoice.impl;

import com.promanatia.CamelDemo.DTO.OrgSubsidaryDto;
import com.promanatia.CamelDemo.DTO.ProductMasterDTO;
import com.promanatia.CamelDemo.service.LookupService;
import com.promanatia.CamelDemo.utility.RowContext;

public abstract class BaseLookupRule {

	protected final LookupService lookupService;

	protected BaseLookupRule(LookupService lookupService) {
		this.lookupService = lookupService;
	}

	protected OrgSubsidaryDto organization(RowContext row) {

		if (row.getOrganization() == null) {
			row.setOrganization(lookupService.getOrganization(row.get("organization")));
		}

		return row.getOrganization();
	}

	protected OrgSubsidaryDto businessPartner(RowContext row) {

		if (row.getBusinessPartner() == null) {
			row.setBusinessPartner(lookupService.getBusinessPartner(row.get("BusinessPartnerFirstName")));
		}

		return row.getBusinessPartner();
	}

	protected ProductMasterDTO product(RowContext row) {

		if (row.getProduct() == null) {
			row.setProduct(lookupService.getProduct(row.get("ProductSearchKey")));
		}

		return row.getProduct();
	}

}