package com.promanatia.openbravonetsuiteintegration.transformation.openbravo.salesinvoice.impl;

import org.springframework.stereotype.Component;

import com.promanatia.openbravonetsuiteintegration.DTO.FieldMappingDTO;
import com.promanatia.openbravonetsuiteintegration.transformation.TransformationRule;
import com.promanatia.openbravonetsuiteintegration.utility.RowContext;

@Component
public class InterCompanyRule implements TransformationRule {

	@Override
	public String getRuleCode() {
		return "INTERCOMPANY";
	}

	@Override
	public String transform(RowContext row, FieldMappingDTO mapping) {
		return row.isInterCompany() ? "True" : "False";
	}

}