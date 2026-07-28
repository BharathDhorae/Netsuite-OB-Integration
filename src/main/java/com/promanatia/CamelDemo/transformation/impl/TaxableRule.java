package com.promanatia.CamelDemo.transformation.impl;

import org.springframework.stereotype.Component;

import com.promanatia.CamelDemo.DTO.FieldMappingDTO;
import com.promanatia.CamelDemo.transformation.TransformationRule;
import com.promanatia.CamelDemo.utility.RowContext;

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
