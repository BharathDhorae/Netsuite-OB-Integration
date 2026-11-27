package com.promanatia.openbravonetsuiteintegration.transformation.openbravo.payment.impl;

import org.springframework.stereotype.Component;

import com.promanatia.openbravonetsuiteintegration.dto.FieldMappingDTO;
import com.promanatia.openbravonetsuiteintegration.dto.OrgSubsidaryDto;
import com.promanatia.openbravonetsuiteintegration.service.LookupService;
import com.promanatia.openbravonetsuiteintegration.transformation.TransformationRule;
import com.promanatia.openbravonetsuiteintegration.transformation.openbravo.salesinvoice.impl.BaseLookupRule;
import com.promanatia.openbravonetsuiteintegration.utility.RowContext;

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