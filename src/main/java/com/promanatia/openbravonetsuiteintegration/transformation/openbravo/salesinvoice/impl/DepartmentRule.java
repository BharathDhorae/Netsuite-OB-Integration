package com.promanatia.openbravonetsuiteintegration.transformation.openbravo.salesinvoice.impl;

import org.springframework.stereotype.Component;

import com.promanatia.openbravonetsuiteintegration.dto.FieldMappingDTO;
import com.promanatia.openbravonetsuiteintegration.transformation.TransformationRule;
import com.promanatia.openbravonetsuiteintegration.utility.RowContext;

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