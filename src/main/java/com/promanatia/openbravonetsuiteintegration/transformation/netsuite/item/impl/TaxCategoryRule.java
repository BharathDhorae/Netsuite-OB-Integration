package com.promanatia.openbravonetsuiteintegration.transformation.netsuite.item.impl;

import org.springframework.stereotype.Component;

import com.promanatia.openbravonetsuiteintegration.dto.FieldMappingDTO;
import com.promanatia.openbravonetsuiteintegration.service.LookupService;
import com.promanatia.openbravonetsuiteintegration.transformation.TransformationRule;
import com.promanatia.openbravonetsuiteintegration.utility.RowContext;

@Component
public class TaxCategoryRule implements TransformationRule {

	private final LookupService lookupService;

	public TaxCategoryRule(LookupService lookupService) {
		this.lookupService = lookupService;
	}

	@Override
	public String getRuleCode() {
		return "TAXCATEGORY";
	}

	@Override
	public String transform(RowContext row, FieldMappingDTO mapping) {

		String taxRule = row.getTaxRule();

		if (taxRule == null || taxRule.isBlank()) {
			taxRule = lookupService.getTaxCategory(row.get("External ID for Sub Class"), row.get("Item Tax Group"));

			if (taxRule != null) {
				row.setTaxRule(taxRule);
			}
		}

		return taxRule == null ? "" : taxRule;
	}
}
