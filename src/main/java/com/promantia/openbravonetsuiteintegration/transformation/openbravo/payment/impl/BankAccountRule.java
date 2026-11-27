package com.promantia.openbravonetsuiteintegration.transformation.openbravo.payment.impl;

import org.springframework.stereotype.Component;

import com.promantia.openbravonetsuiteintegration.dto.FieldMappingDTO;
import com.promantia.openbravonetsuiteintegration.dto.OrgSubsidaryDto;
import com.promantia.openbravonetsuiteintegration.dto.OrgWarehouseBinAccountDTO;
import com.promantia.openbravonetsuiteintegration.service.LookupService;
import com.promantia.openbravonetsuiteintegration.transformation.TransformationRule;
import com.promantia.openbravonetsuiteintegration.utility.RowContext;

@Component
public class BankAccountRule implements TransformationRule {

	private final LookupService lookupService;

	public BankAccountRule(LookupService lookupService) {
		this.lookupService = lookupService;
	}

	@Override
	public String getRuleCode() {
		return "ACCOUNT";
	}

	@Override
	public String transform(RowContext row, FieldMappingDTO mapping) {

		OrgSubsidaryDto dto = row.getOrganization();

		if (dto == null) {
			dto = lookupService.getOrganization(row.get("organization"));
			row.setOrganization(dto);
		}

		if (dto == null) {
			return "";
		}

		String documentNo = row.get("documentno");

		// Determine the subsidiary
		String subsidiary;

		if (documentNo != null && documentNo.endsWith("A")) {
			subsidiary = dto.getAksharpithSubsidary();
		} else {
			subsidiary = dto.getSubsidary();
		}

		// Get the financial location
		String financialLocation = dto.getFinancialLocation();
		if (subsidiary == null || financialLocation == null) {
			return "";
		}

		// Find bank account using subsidiary and financial location
		OrgWarehouseBinAccountDTO bankAccount = lookupService.getBankAccountBySubsidiary(subsidiary, financialLocation);
		return bankAccount == null || bankAccount.getBanckAccount() != null ? bankAccount.getBanckAccount() : "";
	}

}
