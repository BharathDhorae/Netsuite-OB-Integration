package com.promanatia.openbravonetsuiteintegration.transformation.openbravo.salesinvoice.impl;

import org.springframework.stereotype.Component;

import com.promanatia.openbravonetsuiteintegration.DTO.FieldMappingDTO;
import com.promanatia.openbravonetsuiteintegration.DTO.ProductMasterDTO;
import com.promanatia.openbravonetsuiteintegration.service.LookupService;
import com.promanatia.openbravonetsuiteintegration.transformation.TransformationRule;
import com.promanatia.openbravonetsuiteintegration.utility.RowContext;

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