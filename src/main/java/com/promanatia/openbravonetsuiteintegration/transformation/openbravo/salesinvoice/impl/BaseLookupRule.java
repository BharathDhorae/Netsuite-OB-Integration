package com.promanatia.openbravonetsuiteintegration.transformation.openbravo.salesinvoice.impl;

import com.promanatia.openbravonetsuiteintegration.dto.OrgSubsidaryDto;
import com.promanatia.openbravonetsuiteintegration.service.LookupService;
import com.promanatia.openbravonetsuiteintegration.utility.RowContext;

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

}