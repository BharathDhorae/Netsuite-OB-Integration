package com.promantia.openbravonetsuiteintegration.transformation.openbravo.payment.impl;

import org.springframework.stereotype.Component;

import com.promantia.openbravonetsuiteintegration.dto.FieldMappingDTO;
import com.promantia.openbravonetsuiteintegration.transformation.TransformationRule;
import com.promantia.openbravonetsuiteintegration.utility.DateUtil;
import com.promantia.openbravonetsuiteintegration.utility.RowContext;

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
