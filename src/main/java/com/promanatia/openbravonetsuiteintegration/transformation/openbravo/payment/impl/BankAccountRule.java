package com.promanatia.openbravonetsuiteintegration.transformation.openbravo.payment.impl;

import org.springframework.stereotype.Component;

import com.promanatia.openbravonetsuiteintegration.dto.FieldMappingDTO;
import com.promanatia.openbravonetsuiteintegration.dto.OrgSubsidaryDto;
import com.promanatia.openbravonetsuiteintegration.service.LookupService;
import com.promanatia.openbravonetsuiteintegration.transformation.TransformationRule;
import com.promanatia.openbravonetsuiteintegration.utility.RowContext;

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
		String bankAccount = lookupService.getBankAccountBySubsidiary(subsidiary, financialLocation);
		return bankAccount != null ? bankAccount : "";
	}

}
