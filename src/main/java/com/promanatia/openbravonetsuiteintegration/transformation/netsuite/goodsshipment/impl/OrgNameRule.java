package com.promanatia.openbravonetsuiteintegration.transformation.netsuite.goodsshipment.impl;

import org.springframework.stereotype.Component;

import com.promanatia.openbravonetsuiteintegration.dto.FieldMappingDTO;
import com.promanatia.openbravonetsuiteintegration.service.LookupService;
import com.promanatia.openbravonetsuiteintegration.transformation.TransformationRule;
import com.promanatia.openbravonetsuiteintegration.transformation.openbravo.salesinvoice.impl.BaseLookupRule;
import com.promanatia.openbravonetsuiteintegration.utility.RowContext;

@Component
public class OrgNameRule extends BaseLookupRule implements TransformationRule {

	private OrgNameRule(LookupService lookupService) {
		super(lookupService);
	}

	@Override
	public String getRuleCode() {
		return "ORGNAMEBYFINANCIALLOCATION";
	}

	@Override
	public String transform(RowContext row, FieldMappingDTO mapping) {

		var dto = orgWarehouse(row);
		return dto == null ? "" : dto.getOrganization();
	}

}
