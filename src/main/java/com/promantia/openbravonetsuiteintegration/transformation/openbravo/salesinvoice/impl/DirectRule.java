package com.promantia.openbravonetsuiteintegration.transformation.openbravo.salesinvoice.impl;

import org.springframework.stereotype.Component;

import com.promantia.openbravonetsuiteintegration.dto.FieldMappingDTO;
import com.promantia.openbravonetsuiteintegration.transformation.TransformationRule;
import com.promantia.openbravonetsuiteintegration.utility.RowContext;

@Component
public class DirectRule implements TransformationRule {

	@Override
	public String getRuleCode() {
		return "DIRECT";
	}

	@Override
	public String transform(RowContext row, FieldMappingDTO mapping) {

		if (mapping.getDefaultValue() != null && !mapping.getDefaultValue().isBlank()) {
			return mapping.getDefaultValue();
		}
		return row.get(mapping.getSourceColumn());
	}

}