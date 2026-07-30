package com.promanatia.CamelDemo.transformation.netsuite.product.impl;

import org.springframework.stereotype.Component;

import com.promanatia.CamelDemo.DTO.FieldMappingDTO;
import com.promanatia.CamelDemo.service.LookupService;
import com.promanatia.CamelDemo.transformation.TransformationRule;
import com.promanatia.CamelDemo.utility.RowContext;

@Component
public class OrgNameBySubsidoryRule implements TransformationRule {

	private final LookupService lookupService;

	public OrgNameBySubsidoryRule(LookupService lookupService) {
		this.lookupService = lookupService;
	}

	@Override
	public String getRuleCode() {
		return "ORGNAMEBYSUBSIDARY";
	}

	@Override
	public String transform(RowContext row, FieldMappingDTO mapping) {
		String subsidary = row.get(mapping.getSourceColumn());
		if (subsidary != null && !subsidary.isEmpty()) {
			return lookupService.getOrganizationBySusidary(subsidary);
		}
		return "";
	}

}
