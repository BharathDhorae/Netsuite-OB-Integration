package com.promanatia.CamelDemo.transformation.openbravo.salesinvoice.impl;

import org.springframework.stereotype.Component;

import com.promanatia.CamelDemo.DTO.FieldMappingDTO;
import com.promanatia.CamelDemo.service.LookupService;
import com.promanatia.CamelDemo.transformation.TransformationRule;
import com.promanatia.CamelDemo.utility.RowContext;

@Component
public class SellingInventoryLocationRule implements TransformationRule {

	private final LookupService lookupService;

	public SellingInventoryLocationRule(LookupService lookupService) {
		this.lookupService = lookupService;
	}

	@Override
	public String getRuleCode() {
		return "SELLING_INVENTORY_LOCATION";
	}

	@Override
	public String transform(RowContext row, FieldMappingDTO mapping) {

		String organization = row.get("organization");

		if (organization == null || organization.isBlank()) {
			return "";
		}

		String externalInventoryLocation = lookupService.getExternalInventoryLocation(organization);

		return externalInventoryLocation == null ? "" : externalInventoryLocation;
	}

}
