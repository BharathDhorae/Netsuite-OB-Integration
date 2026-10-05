package com.promanatia.openbravonetsuiteintegration.transformation.openbravo.salesinvoice.impl;

import org.springframework.stereotype.Component;

import com.promanatia.openbravonetsuiteintegration.DTO.FieldMappingDTO;
import com.promanatia.openbravonetsuiteintegration.DTO.OrgSubsidaryDto;
import com.promanatia.openbravonetsuiteintegration.service.LookupService;
import com.promanatia.openbravonetsuiteintegration.transformation.TransformationRule;
import com.promanatia.openbravonetsuiteintegration.utility.RowContext;

@Component
public class SubsidiaryRule implements TransformationRule {

	private final LookupService lookupService;

	public SubsidiaryRule(LookupService lookupService) {
		this.lookupService = lookupService;
	}

	@Override
	public String getRuleCode() {
		return "SUBSIDIARY";
	}

	@Override
	public String transform(RowContext row, FieldMappingDTO mapping) {

		OrgSubsidaryDto dto = row.getOrganization();
		if (dto == null) {
			dto = lookupService.getOrganization(row.get("organization"));
			row.setOrganization(dto);
		}

		if (dto == null) {
			return "";
		}

		String documentNo = row.get("documentno");
		if (documentNo.endsWith("A")) {
			return dto.getAksharpithSubsidary();
		}

		return dto.getSubsidary();
	}

}