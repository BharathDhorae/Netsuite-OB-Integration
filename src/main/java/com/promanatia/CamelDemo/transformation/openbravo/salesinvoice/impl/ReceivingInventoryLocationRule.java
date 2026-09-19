package com.promanatia.CamelDemo.transformation.openbravo.salesinvoice.impl;

import org.springframework.stereotype.Component;

import com.promanatia.CamelDemo.DTO.FieldMappingDTO;
import com.promanatia.CamelDemo.service.LookupService;
import com.promanatia.CamelDemo.transformation.TransformationRule;
import com.promanatia.CamelDemo.utility.RowContext;

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
