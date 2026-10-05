package com.promanatia.openbravonetsuiteintegration.transformation.openbravo.salesinvoice.impl;

import org.springframework.stereotype.Component;

import com.promanatia.openbravonetsuiteintegration.DTO.FieldMappingDTO;
import com.promanatia.openbravonetsuiteintegration.DTO.OrgSubsidaryDto;
import com.promanatia.openbravonetsuiteintegration.service.LookupService;
import com.promanatia.openbravonetsuiteintegration.transformation.TransformationRule;
import com.promanatia.openbravonetsuiteintegration.utility.RowContext;

@Component
public class VendorExternalRule extends BaseLookupRule implements TransformationRule {

	private final SubsidiaryRule subsidiaryRule;

	public VendorExternalRule(LookupService lookupService, SubsidiaryRule subsidiaryRule) {
		super(lookupService);
		this.subsidiaryRule = subsidiaryRule;

	}

	@Override
	public String getRuleCode() {
		return "VENDOR_EXTERNAL_ID";
	}

	@Override
	public String transform(RowContext row, FieldMappingDTO mapping) {

		if (!row.isInterCompany()) {
			return "";
		}
		OrgSubsidaryDto dto = subsidiary(row, mapping);

		return dto == null ? "" : dto.getInternalVendor();
	}

	protected OrgSubsidaryDto subsidiary(RowContext row, FieldMappingDTO mapping) {

		if (row.getSubsidiaryDto() == null) {
			row.setSubsidiaryDto(lookupService.getBySubsidiary(subsidiaryRule.transform(row, mapping),
					businessPartner(row).getAksharpithSubsidary()));
		}

		return row.getSubsidiaryDto();
	}

}