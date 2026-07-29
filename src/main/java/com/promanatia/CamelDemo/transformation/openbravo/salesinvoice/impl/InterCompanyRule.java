package com.promanatia.CamelDemo.transformation.openbravo.salesinvoice.impl;

import org.springframework.stereotype.Component;

import com.promanatia.CamelDemo.DTO.FieldMappingDTO;
import com.promanatia.CamelDemo.transformation.TransformationRule;
import com.promanatia.CamelDemo.utility.RowContext;

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