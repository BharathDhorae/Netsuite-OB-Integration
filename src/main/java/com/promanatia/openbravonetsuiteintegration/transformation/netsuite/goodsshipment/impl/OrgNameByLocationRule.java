package com.promanatia.openbravonetsuiteintegration.transformation.netsuite.goodsshipment.impl;


import org.springframework.stereotype.Component;

import com.promanatia.openbravonetsuiteintegration.dto.FieldMappingDTO;
import com.promanatia.openbravonetsuiteintegration.service.LookupService;
import com.promanatia.openbravonetsuiteintegration.transformation.TransformationRule;
import com.promanatia.openbravonetsuiteintegration.utility.RowContext;

@Component
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
