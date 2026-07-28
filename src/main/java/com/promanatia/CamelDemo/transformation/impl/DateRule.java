package com.promanatia.CamelDemo.transformation.impl;

import org.springframework.stereotype.Component;

import com.promanatia.CamelDemo.DTO.FieldMappingDTO;
import com.promanatia.CamelDemo.transformation.TransformationRule;
import com.promanatia.CamelDemo.utility.DateUtil;
import com.promanatia.CamelDemo.utility.RowContext;

@Component
public class DateRule implements TransformationRule {

	@Override
	public String getRuleCode() {
		return "DATE_MMDDYYYY";
	}

	@Override
	public String transform(RowContext row, FieldMappingDTO mapping) {
		return DateUtil.toMMddyyyy(row.get(mapping.getSourceColumn()));
	}

}