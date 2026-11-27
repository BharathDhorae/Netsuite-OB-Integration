package com.promanatia.openbravonetsuiteintegration.transformation.netsuite.purchaseorder.impl;

import org.springframework.stereotype.Component;

import com.promanatia.openbravonetsuiteintegration.dto.FieldMappingDTO;
import com.promanatia.openbravonetsuiteintegration.service.LookupService;
import com.promanatia.openbravonetsuiteintegration.transformation.TransformationRule;
import com.promanatia.openbravonetsuiteintegration.transformation.openbravo.salesinvoice.impl.BaseLookupRule;
import com.promanatia.openbravonetsuiteintegration.utility.RowContext;

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
