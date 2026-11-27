package com.promantia.openbravonetsuiteintegration.transformation.openbravo.salesinvoice.impl;

import org.springframework.stereotype.Component;

import com.promantia.openbravonetsuiteintegration.dto.FieldMappingDTO;
import com.promantia.openbravonetsuiteintegration.dto.OrgSubsidaryDto;
import com.promantia.openbravonetsuiteintegration.service.LookupService;
import com.promantia.openbravonetsuiteintegration.transformation.TransformationRule;
import com.promantia.openbravonetsuiteintegration.utility.RowContext;

@Component
public class InternalCustomerRule extends BaseLookupRule implements TransformationRule {

	public InternalCustomerRule(LookupService lookupService) {
		super(lookupService);
	}

	@Override
	public String getRuleCode() {
		return "INTERNAL_CUSTOMER";
	}

	@Override
	public String transform(RowContext row, FieldMappingDTO mapping) {

		if (!row.isInterCompany()) {
			return "";
		}

		OrgSubsidaryDto dto = businessPartner(row);
		return dto == null ? "" : dto.getInternalCustomer();
	}

}