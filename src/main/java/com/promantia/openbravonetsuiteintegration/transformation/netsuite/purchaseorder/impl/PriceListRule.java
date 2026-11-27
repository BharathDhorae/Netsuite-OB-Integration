package com.promantia.openbravonetsuiteintegration.transformation.netsuite.purchaseorder.impl;

import org.springframework.stereotype.Component;

import com.promantia.openbravonetsuiteintegration.dto.FieldMappingDTO;
import com.promantia.openbravonetsuiteintegration.service.LookupService;
import com.promantia.openbravonetsuiteintegration.transformation.TransformationRule;
import com.promantia.openbravonetsuiteintegration.transformation.openbravo.salesinvoice.impl.BaseLookupRule;
import com.promantia.openbravonetsuiteintegration.utility.RowContext;

@Component
public class PriceListRule extends BaseLookupRule implements TransformationRule {

	public PriceListRule(LookupService lookupService) {
		super(lookupService);
	}

	@Override
	public String getRuleCode() {
		return "PRICELIST";
	}

	@Override
	public String transform(RowContext row, FieldMappingDTO mapping) {
		var dto = orgWarehouse(row);
		return dto == null ? ""
				: "PurchaseOrder".equalsIgnoreCase(mapping.getEntityName()) ? dto.getPurchaseOrderPriceList()
						: dto.getSalesOrderPriceList();

	}

}
