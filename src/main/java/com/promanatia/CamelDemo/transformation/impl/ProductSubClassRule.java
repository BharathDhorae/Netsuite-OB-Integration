package com.promanatia.CamelDemo.transformation.impl;

import org.springframework.stereotype.Component;

import com.promanatia.CamelDemo.DTO.FieldMappingEntity;
import com.promanatia.CamelDemo.DTO.ProductMasterDTO;
import com.promanatia.CamelDemo.service.LookupService;
import com.promanatia.CamelDemo.transformation.TransformationRule;
import com.promanatia.CamelDemo.utility.RowContext;

@Component
public class ProductSubClassRule extends BaseLookupRule implements TransformationRule {

	public ProductSubClassRule(LookupService lookupService) {
		super(lookupService);
	}

	@Override
	public String getRuleCode() {
		return "TABLEREF_PRODUCT_SUBCLASS";
	}

	@Override
	public String transform(RowContext row, FieldMappingEntity mapping) {
		ProductMasterDTO dto = product(row);
		return dto == null ? "" : dto.getSubClassExternalId();
	}

}