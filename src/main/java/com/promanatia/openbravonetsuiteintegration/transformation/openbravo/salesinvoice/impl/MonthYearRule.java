package com.promanatia.openbravonetsuiteintegration.transformation.openbravo.salesinvoice.impl;

import org.springframework.stereotype.Component;

import com.promanatia.openbravonetsuiteintegration.DTO.FieldMappingDTO;
import com.promanatia.openbravonetsuiteintegration.transformation.TransformationRule;
import com.promanatia.openbravonetsuiteintegration.utility.DateUtil;
import com.promanatia.openbravonetsuiteintegration.utility.RowContext;

@Component
public class MonthYearRule implements TransformationRule {

	@Override
	public String getRuleCode() {
		return "DATE_MMYYYY";
	}

	@Override
	public String transform(RowContext row, FieldMappingDTO mapping) {
		return DateUtil.toMonthYear(row.get("DateInvoiced"));
	}

}