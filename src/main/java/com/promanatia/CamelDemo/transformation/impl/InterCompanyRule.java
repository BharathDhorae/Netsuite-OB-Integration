package com.promanatia.CamelDemo.transformation.impl;

import org.springframework.stereotype.Component;

import com.promanatia.CamelDemo.DTO.FieldMappingEntity;
import com.promanatia.CamelDemo.transformation.TransformationRule;
import com.promanatia.CamelDemo.utility.RowContext;

@Component
public class InterCompanyRule implements TransformationRule {

	@Override
	public String getRuleCode() {
		return "INTERCOMPANY";
	}

	@Override
	public String transform(RowContext row, FieldMappingEntity mapping) {
		return row.isInterCompany() ? "True" : "False";
	}

}