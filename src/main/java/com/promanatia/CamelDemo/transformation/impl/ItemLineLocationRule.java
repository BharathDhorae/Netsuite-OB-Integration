package com.promanatia.CamelDemo.transformation.impl;

import org.springframework.stereotype.Component;

import com.promanatia.CamelDemo.DTO.FieldMappingDTO;
import com.promanatia.CamelDemo.DTO.OrgSubsidaryDto;
import com.promanatia.CamelDemo.service.LookupService;
import com.promanatia.CamelDemo.transformation.TransformationRule;
import com.promanatia.CamelDemo.utility.RowContext;

@Component
public class ItemLineLocationRule implements TransformationRule {

	private final LookupService lookupService;

	public ItemLineLocationRule(LookupService lookupService) {
		this.lookupService = lookupService;
	}

	@Override
	public String getRuleCode() {
		return "ITEM_LINE_LOCATION";
	}

	@Override
	public String transform(RowContext row, FieldMappingDTO mapping) {

		OrgSubsidaryDto dto = row.getOrganization();
		if (dto == null) {
			dto = lookupService.getOrganization(row.get("organization"));
			row.setOrganization(dto);
		}
		return dto == null ? "" : dto.getItemLineLocation();
	}

}