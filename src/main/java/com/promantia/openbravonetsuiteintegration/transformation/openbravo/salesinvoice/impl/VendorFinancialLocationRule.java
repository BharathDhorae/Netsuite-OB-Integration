package com.promantia.openbravonetsuiteintegration.transformation.openbravo.salesinvoice.impl;

import org.springframework.stereotype.Component;

import com.promantia.openbravonetsuiteintegration.dto.FieldMappingDTO;
import com.promantia.openbravonetsuiteintegration.dto.OrgSubsidaryDto;
import com.promantia.openbravonetsuiteintegration.service.LookupService;
import com.promantia.openbravonetsuiteintegration.transformation.TransformationRule;
import com.promantia.openbravonetsuiteintegration.utility.RowContext;

@Component
public class VendorFinancialLocationRule extends BaseLookupRule implements TransformationRule {

	public VendorFinancialLocationRule(LookupService lookupService) {
		super(lookupService);
	}

	@Override
	public String getRuleCode() {
		return "VENDOR_FINANCIAL_LOCATION_EXTERNAL_ID";
	}

	@Override
	public String transform(RowContext row, FieldMappingDTO mapping) {
		OrgSubsidaryDto dto = businessPartner(row);
		return dto == null ? "" : dto.getFinancialLocation();
	}

}