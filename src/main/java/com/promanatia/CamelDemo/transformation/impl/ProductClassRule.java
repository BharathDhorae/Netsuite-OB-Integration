package com.promanatia.CamelDemo.transformation.impl;

import org.springframework.stereotype.Component;

import com.promanatia.CamelDemo.DTO.FieldMappingDTO;
import com.promanatia.CamelDemo.DTO.ProductMasterDTO;
import com.promanatia.CamelDemo.service.LookupService;
import com.promanatia.CamelDemo.transformation.TransformationRule;
import com.promanatia.CamelDemo.utility.RowContext;

@Component
public class ProductClassRule extends BaseLookupRule implements TransformationRule {

	public ProductClassRule(LookupService lookupService) {
		super(lookupService);
	}

	@Override
	public String getRuleCode() {
		return "TABLEREF_PRODUCT_CLASS";
	}

	@Override
	public String transform(RowContext row, FieldMappingDTO mapping) {
		ProductMasterDTO dto = product(row);
		return dto == null ? "" : dto.getClassExternalId();
	}

}