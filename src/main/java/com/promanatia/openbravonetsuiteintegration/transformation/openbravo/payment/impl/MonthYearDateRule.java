package com.promanatia.openbravonetsuiteintegration.transformation.openbravo.payment.impl;

import org.springframework.stereotype.Component;

import com.promanatia.openbravonetsuiteintegration.dto.FieldMappingDTO;
import com.promanatia.openbravonetsuiteintegration.transformation.TransformationRule;
import com.promanatia.openbravonetsuiteintegration.utility.DateUtil;
import com.promanatia.openbravonetsuiteintegration.utility.RowContext;

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
