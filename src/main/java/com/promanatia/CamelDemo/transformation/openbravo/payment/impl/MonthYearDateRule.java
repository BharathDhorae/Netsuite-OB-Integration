package com.promanatia.CamelDemo.transformation.openbravo.payment.impl;

import org.springframework.stereotype.Component;

import com.promanatia.CamelDemo.DTO.FieldMappingDTO;
import com.promanatia.CamelDemo.transformation.TransformationRule;
import com.promanatia.CamelDemo.utility.DateUtil;
import com.promanatia.CamelDemo.utility.RowContext;

@Component
public class MonthYearDateRule implements TransformationRule {

	@Override
	public String getRuleCode() {
		return "DATE_YYYYMM";
	}

	@Override
	public String transform(RowContext row, FieldMappingDTO mapping) {
		return DateUtil.toDayMonth(row.get("DateInvoiced"));
	}

}
