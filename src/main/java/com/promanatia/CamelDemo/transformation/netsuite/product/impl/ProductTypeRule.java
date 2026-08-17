package com.promanatia.CamelDemo.transformation.netsuite.product.impl;

import org.springframework.stereotype.Component;

import com.promanatia.CamelDemo.DTO.FieldMappingDTO;
import com.promanatia.CamelDemo.service.LookupService;
import com.promanatia.CamelDemo.transformation.TransformationRule;
import com.promanatia.CamelDemo.utility.RowContext;

@Component
public class ProductTypeRule implements TransformationRule {

	private final LookupService lookupService;

	public ProductTypeRule(LookupService lookupService) {
			this.lookupService = lookupService;
		}

	@Override
	public String getRuleCode() {
		return "PRODUCTTYPE";
	}

	@Override
	public String transform(RowContext row, FieldMappingDTO mapping) {
		String internalId = row.get(mapping.getSourceColumn());
		if (internalId != null && !internalId.isEmpty()) {
			return lookupService.getProductTypeByInternalId(internalId);
		}
		return "";
	}

}
