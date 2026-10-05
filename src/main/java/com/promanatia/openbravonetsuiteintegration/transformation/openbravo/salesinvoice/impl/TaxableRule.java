package com.promanatia.openbravonetsuiteintegration.transformation.openbravo.salesinvoice.impl;

import org.springframework.stereotype.Component;

import com.promanatia.openbravonetsuiteintegration.DTO.FieldMappingDTO;
import com.promanatia.openbravonetsuiteintegration.transformation.TransformationRule;
import com.promanatia.openbravonetsuiteintegration.utility.RowContext;

@Component
public class TaxableRule implements TransformationRule {

	@Override
	public String getRuleCode() {
		return "Taxable";
	}

	@Override
	public String transform(RowContext row, FieldMappingDTO mapping) {
		String value = row.get(mapping.getSourceColumn());
		return value != null && "Y".equalsIgnoreCase(value.trim()) ? "T" : "F";
	}

}
