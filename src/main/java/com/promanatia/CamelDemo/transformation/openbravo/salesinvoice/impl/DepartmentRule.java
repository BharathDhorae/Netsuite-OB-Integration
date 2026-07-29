package com.promanatia.CamelDemo.transformation.openbravo.salesinvoice.impl;

import org.springframework.stereotype.Component;

import com.promanatia.CamelDemo.DTO.FieldMappingDTO;
import com.promanatia.CamelDemo.transformation.TransformationRule;
import com.promanatia.CamelDemo.utility.RowContext;

@Component
public class DepartmentRule implements TransformationRule {

	@Override
	public String getRuleCode() {
		return "BUSINESSPARTNERNAME";
	}

	@Override
	public String transform(RowContext row, FieldMappingDTO mapping) {
		return row.getDepartment();
	}

}