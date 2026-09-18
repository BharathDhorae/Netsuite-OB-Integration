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

		String externalInventoryLocation = row.getExternalInventoryLocation();
		if (externalInventoryLocation == null) {
			externalInventoryLocation = lookupService.getExternalInventoryLocation(row.get("organization"));
			row.setExternalInventoryLocation(externalInventoryLocation);
		}
		return externalInventoryLocation == null ? "" : externalInventoryLocation;
	}

}
