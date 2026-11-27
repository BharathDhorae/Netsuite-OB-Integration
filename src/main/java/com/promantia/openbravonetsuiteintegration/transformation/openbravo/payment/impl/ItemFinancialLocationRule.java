package com.promantia.openbravonetsuiteintegration.transformation.openbravo.payment.impl;

import org.springframework.stereotype.Component;

import com.promantia.openbravonetsuiteintegration.dto.FieldMappingDTO;
import com.promantia.openbravonetsuiteintegration.dto.OrgSubsidaryDto;
import com.promantia.openbravonetsuiteintegration.service.LookupService;
import com.promantia.openbravonetsuiteintegration.transformation.TransformationRule;
import com.promantia.openbravonetsuiteintegration.transformation.openbravo.salesinvoice.impl.BaseLookupRule;
import com.promantia.openbravonetsuiteintegration.utility.RowContext;

@Component
public class ItemFinancialLocationRule extends BaseLookupRule implements TransformationRule {

	public ItemFinancialLocationRule(LookupService lookupService) {
		super(lookupService);
	}

	@Override
	public String getRuleCode() {
		return "ITEM_FINANCIAL_LOCATION";
	}

	@Override
	public String transform(RowContext row, FieldMappingDTO mapping) {

		var dto = organization(row);
		return dto == null ? "" : dto.getFinancialLocation();
	}

}