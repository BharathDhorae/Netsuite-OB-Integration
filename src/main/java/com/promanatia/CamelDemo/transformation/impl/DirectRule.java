package com.promanatia.CamelDemo.transformation.impl;

import org.springframework.stereotype.Component;

import com.promanatia.CamelDemo.DTO.FieldMappingEntity;
import com.promanatia.CamelDemo.transformation.TransformationRule;
import com.promanatia.CamelDemo.utility.RowContext;

@Component
public class DirectRule implements TransformationRule {

	@Override
	public String getRuleCode() {
		return "DIRECT";
	}

	@Override
	public String transform(RowContext row, FieldMappingEntity mapping) {

		if (mapping.getDefaultValue() != null && !mapping.getDefaultValue().isBlank()) {
			return mapping.getDefaultValue();
		}
		return row.get(mapping.getSourceColumn());
	}

}