package com.promantia.openbravonetsuiteintegration.transformation.openbravo.salesinvoice.impl;

import org.springframework.stereotype.Component;

import com.promantia.openbravonetsuiteintegration.dto.FieldMappingDTO;
import com.promantia.openbravonetsuiteintegration.transformation.TransformationRule;
import com.promantia.openbravonetsuiteintegration.utility.DateUtil;
import com.promantia.openbravonetsuiteintegration.utility.RowContext;

@Component
public class MonthYearRule implements TransformationRule {

	@Override
	public String getRuleCode() {
		return "DATE_MMYYYY";
	}

	@Override
	public String transform(RowContext row, FieldMappingDTO mapping) {
		String value = row.get("DateInvoiced");

		if (value == null || value.isBlank()) {
			value = row.get("MovementDate");
		}

		return DateUtil.toMonthYear(value);
	}

}