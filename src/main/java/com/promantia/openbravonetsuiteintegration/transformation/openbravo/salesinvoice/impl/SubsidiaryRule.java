package com.promantia.openbravonetsuiteintegration.transformation.openbravo.salesinvoice.impl;

import org.springframework.stereotype.Component;

import com.promantia.openbravonetsuiteintegration.dto.FieldMappingDTO;
import com.promantia.openbravonetsuiteintegration.service.LookupService;
import com.promantia.openbravonetsuiteintegration.transformation.TransformationRule;
import com.promantia.openbravonetsuiteintegration.utility.RowContext;

@Component
public class SubsidiaryRule extends BaseLookupRule implements TransformationRule {

	public SubsidiaryRule(LookupService lookupService) {
		super(lookupService);
	}

	@Override
	public String getRuleCode() {
		return "SUBSIDIARY";
	}

	@Override
	public String transform(RowContext row, FieldMappingDTO mapping) {

		var dto = organization(row);

		String documentNo = row.get("documentno");
		if (documentNo.endsWith("A")) {
			return dto.getAksharpithSubsidary();
		}

		return dto.getSubsidary();
	}

}