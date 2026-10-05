package com.promanatia.openbravonetsuiteintegration.transformation.openbravo.payment.impl;

import org.springframework.stereotype.Component;

import com.promanatia.openbravonetsuiteintegration.DTO.FieldMappingDTO;
import com.promanatia.openbravonetsuiteintegration.DTO.OrgSubsidaryDto;
import com.promanatia.openbravonetsuiteintegration.service.LookupService;
import com.promanatia.openbravonetsuiteintegration.transformation.TransformationRule;
import com.promanatia.openbravonetsuiteintegration.utility.RowContext;

@Component
public class ItemFinancialLocationRule implements TransformationRule {

	private final LookupService lookupService;

	public ItemFinancialLocationRule(LookupService lookupService) {
		this.lookupService = lookupService;
	}

	@Override
	public String getRuleCode() {
		return "ITEM_FINANCIAL_LOCATION";
	}

	@Override
	public String transform(RowContext row, FieldMappingDTO mapping) {

		OrgSubsidaryDto dto = row.getOrganization();
		if (dto == null) {
			dto = lookupService.getOrganization(row.get("organization"));
			row.setOrganization(dto);
		}
		return dto == null ? "" : dto.getFinancialLocation();
	}

}