package com.promanatia.CamelDemo.transformation.netsuite.goodsshipment.impl;

import com.promanatia.CamelDemo.DTO.FieldMappingDTO;
import com.promanatia.CamelDemo.service.LookupService;
import com.promanatia.CamelDemo.transformation.TransformationRule;
import com.promanatia.CamelDemo.utility.RowContext;

public class OrgNameByLocationRule implements TransformationRule {

	private final LookupService lookupService;

	private OrgNameByLocationRule(LookupService lookupService) {
		this.lookupService = lookupService;
	}

	@Override
	public String getRuleCode() {
		return "ORGNAMEBYLOCATION";
	}

	@Override
	public String transform(RowContext row, FieldMappingDTO mapping) {

		if (row.getOrgNameLocation() == null) {
			row.setOrgNameLocation(lookupService.getOrgName(row.get("Inventory Location")));
		}
		return row.getOrgNameLocation();
	}

}
