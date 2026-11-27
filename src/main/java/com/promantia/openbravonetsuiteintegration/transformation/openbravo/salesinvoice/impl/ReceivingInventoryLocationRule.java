package com.promantia.openbravonetsuiteintegration.transformation.openbravo.salesinvoice.impl;

import org.springframework.stereotype.Component;

import com.promantia.openbravonetsuiteintegration.dto.FieldMappingDTO;
import com.promantia.openbravonetsuiteintegration.service.LookupService;
import com.promantia.openbravonetsuiteintegration.transformation.TransformationRule;
import com.promantia.openbravonetsuiteintegration.utility.RowContext;

@Component
public class ReceivingInventoryLocationRule implements TransformationRule {

	private final LookupService lookupService;

	public ReceivingInventoryLocationRule(LookupService lookupService) {
		this.lookupService = lookupService;
	}

	@Override
	public String getRuleCode() {
		return "RECEIVING_INVENTORY_LOCATION";
	}

	@Override
	public String transform(RowContext row, FieldMappingDTO mapping) {

		String businessPartnerExtId = row.get("BusinessPartnerExtID");

		if (!row.isInterCompany() || businessPartnerExtId == null || businessPartnerExtId.isEmpty()) {
			return "";
		}

		String externalInventoryLocation = lookupService.getExternalInventoryLocation(businessPartnerExtId);
		return externalInventoryLocation == null ? "" : externalInventoryLocation;
	}
}
