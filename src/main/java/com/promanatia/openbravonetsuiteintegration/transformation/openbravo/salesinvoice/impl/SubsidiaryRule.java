package com.promanatia.openbravonetsuiteintegration.transformation.openbravo.salesinvoice.impl;

import org.springframework.stereotype.Component;

import com.promanatia.openbravonetsuiteintegration.dto.FieldMappingDTO;
import com.promanatia.openbravonetsuiteintegration.service.LookupService;
import com.promanatia.openbravonetsuiteintegration.transformation.TransformationRule;
import com.promanatia.openbravonetsuiteintegration.utility.RowContext;

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