package com.promantia.openbravonetsuiteintegration.transformation.openbravo.salesinvoice.impl;

import java.util.Objects;

import org.springframework.stereotype.Component;

import com.promantia.openbravonetsuiteintegration.dto.FieldMappingDTO;
import com.promantia.openbravonetsuiteintegration.transformation.TransformationRule;
import com.promantia.openbravonetsuiteintegration.utility.RowContext;

@Component
public class CustbodyInvoiceRule implements TransformationRule {

	private final SubsidiaryRule subsidiaryRule;

	public CustbodyInvoiceRule(SubsidiaryRule subsidiaryRule) {
		this.subsidiaryRule = subsidiaryRule;
	}

	@Override
	public String getRuleCode() {
		return "CUSTBODY_OB_INVOICE_NO";
	}

	@Override
	public String transform(RowContext row, FieldMappingDTO mapping) {
		String invoice = row.get("documentno");
		String subsidiary = subsidiaryRule.transform(row, mapping);
		return invoice + "-" + Objects.toString(subsidiary, "");
	}

}