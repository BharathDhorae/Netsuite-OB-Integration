package com.promanatia.CamelDemo.transformation.openbravo.salesinvoice.impl;

import org.springframework.stereotype.Component;

import com.promanatia.CamelDemo.DTO.FieldMappingDTO;
import com.promanatia.CamelDemo.transformation.TransformationRule;
import com.promanatia.CamelDemo.utility.RowContext;

@Component
public class DirectRule implements TransformationRule {

	@Override
	public String getRuleCode() {
		return "DIRECT";
	}

	@Override
	public String transform(RowContext row, FieldMappingDTO mapping) {

		if (mapping.getDefaultValue() != null && !mapping.getDefaultValue().isBlank()) {
			return mapping.getDefaultValue();
		}
		return row.get(mapping.getSourceColumn());
	}

}