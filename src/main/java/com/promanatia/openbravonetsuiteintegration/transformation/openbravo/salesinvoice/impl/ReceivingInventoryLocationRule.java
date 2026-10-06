package com.promanatia.openbravonetsuiteintegration.transformation.openbravo.salesinvoice.impl;

import org.springframework.stereotype.Component;

import com.promanatia.openbravonetsuiteintegration.dto.FieldMappingDTO;
import com.promanatia.openbravonetsuiteintegration.service.LookupService;
import com.promanatia.openbravonetsuiteintegration.transformation.TransformationRule;
import com.promanatia.openbravonetsuiteintegration.utility.RowContext;

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

		if (!row.isInterCompany()) {
			return "";
		}

		if (businessPartnerExtId == null || businessPartnerExtId.isBlank()) {
			return "";
		}

		String externalInventoryLocation = lookupService.getExternalInventoryLocation(businessPartnerExtId);

		return externalInventoryLocation == null ? "" : externalInventoryLocation;
	}
}
