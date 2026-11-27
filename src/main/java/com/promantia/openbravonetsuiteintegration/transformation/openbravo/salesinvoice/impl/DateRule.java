package com.promantia.openbravonetsuiteintegration.transformation.openbravo.salesinvoice.impl;

import org.springframework.stereotype.Component;

import com.promantia.openbravonetsuiteintegration.dto.FieldMappingDTO;
import com.promantia.openbravonetsuiteintegration.transformation.TransformationRule;
import com.promantia.openbravonetsuiteintegration.utility.DateUtil;
import com.promantia.openbravonetsuiteintegration.utility.RowContext;

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