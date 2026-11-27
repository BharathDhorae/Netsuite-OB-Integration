package com.promantia.openbravonetsuiteintegration.transformation.openbravo.salesinvoice.impl;

import org.springframework.stereotype.Component;

import com.promantia.openbravonetsuiteintegration.dto.FieldMappingDTO;
import com.promantia.openbravonetsuiteintegration.dto.OrgSubsidaryDto;
import com.promantia.openbravonetsuiteintegration.service.LookupService;
import com.promantia.openbravonetsuiteintegration.transformation.TransformationRule;
import com.promantia.openbravonetsuiteintegration.utility.RowContext;

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

			OrgSubsidaryDto bp = businessPartner(row);
			if (bp == null)
				return null;

			row.setSubsidiaryDto(
					lookupService.getBySubsidiary(subsidiaryRule.transform(row, mapping), bp.getAksharpithSubsidary()));
		}

		return row.getSubsidiaryDto();
	}

}