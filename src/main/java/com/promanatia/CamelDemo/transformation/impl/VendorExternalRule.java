package com.promanatia.CamelDemo.transformation.impl;

import org.springframework.stereotype.Component;

import com.promanatia.CamelDemo.DTO.FieldMappingEntity;
import com.promanatia.CamelDemo.DTO.OrgSubsidaryDto;
import com.promanatia.CamelDemo.service.LookupService;
import com.promanatia.CamelDemo.transformation.TransformationRule;
import com.promanatia.CamelDemo.utility.RowContext;

@Component
public class VendorExternalRule extends BaseLookupRule implements TransformationRule {

	public VendorExternalRule(LookupService lookupService) {
		super(lookupService);
	}

	@Override
	public String getRuleCode() {
		return "VENDOR_EXTERNAL_ID";
	}

	@Override
	public String transform(RowContext row, FieldMappingEntity mapping) {

		if (!row.isInterCompany()) {
			return "";
		}

		OrgSubsidaryDto dto = businessPartner(row);
		return dto == null ? "" : dto.getInternalVendor();
	}

}