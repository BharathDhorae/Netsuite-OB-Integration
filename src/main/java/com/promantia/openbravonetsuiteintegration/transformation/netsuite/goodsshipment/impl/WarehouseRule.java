package com.promantia.openbravonetsuiteintegration.transformation.netsuite.goodsshipment.impl;

import org.springframework.stereotype.Component;

import com.promantia.openbravonetsuiteintegration.dto.FieldMappingDTO;
import com.promantia.openbravonetsuiteintegration.service.LookupService;
import com.promantia.openbravonetsuiteintegration.transformation.TransformationRule;
import com.promantia.openbravonetsuiteintegration.transformation.openbravo.salesinvoice.impl.BaseLookupRule;
import com.promantia.openbravonetsuiteintegration.utility.RowContext;

@Component
public class WarehouseRule extends BaseLookupRule implements TransformationRule {

	public WarehouseRule(LookupService lookupService) {
		super(lookupService);
	}

	@Override
	public String getRuleCode() {
		return "WAREHOUSE";
	}

	@Override
	public String transform(RowContext row, FieldMappingDTO mapping) {
		var dto = orgWarehouse(row);
		return dto == null ? "" : dto.getWarehouse();
	}
}
