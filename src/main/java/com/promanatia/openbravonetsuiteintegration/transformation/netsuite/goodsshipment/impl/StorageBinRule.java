package com.promanatia.openbravonetsuiteintegration.transformation.netsuite.goodsshipment.impl;

import org.springframework.stereotype.Component;

import com.promanatia.openbravonetsuiteintegration.dto.FieldMappingDTO;
import com.promanatia.openbravonetsuiteintegration.dto.OrgWarehouseBinAccountDTO;
import com.promanatia.openbravonetsuiteintegration.service.LookupService;
import com.promanatia.openbravonetsuiteintegration.transformation.TransformationRule;
import com.promanatia.openbravonetsuiteintegration.utility.RowContext;

@Component
public class StorageBinRule implements TransformationRule {

	private final LookupService lookupService;

	public StorageBinRule(LookupService lookupService) {
		this.lookupService = lookupService;
	}

	@Override
	public String getRuleCode() {
		return "STORAGEBIN";
	}

	@Override
	public String transform(RowContext row, FieldMappingDTO mapping) {
		OrgWarehouseBinAccountDTO orgWarehouseBinAccountDTO = row.getOrgwarehouseAccountDto();

		if (orgWarehouseBinAccountDTO == null) {
			orgWarehouseBinAccountDTO = lookupService.getBankAccountBySubsidiary(row.get("Subsidiary"),
					row.get("Financial location"));
			row.setOrgwarehouseAccountDto(orgWarehouseBinAccountDTO);
		}
		return orgWarehouseBinAccountDTO == null ? "" : orgWarehouseBinAccountDTO.getStorageBin();

	}

}
