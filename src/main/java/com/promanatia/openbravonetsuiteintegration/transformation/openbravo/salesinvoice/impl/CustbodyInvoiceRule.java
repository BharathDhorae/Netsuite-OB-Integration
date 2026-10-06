package com.promanatia.openbravonetsuiteintegration.transformation.openbravo.salesinvoice.impl;

import org.springframework.stereotype.Component;

import com.promanatia.openbravonetsuiteintegration.dto.FieldMappingDTO;
import com.promanatia.openbravonetsuiteintegration.transformation.TransformationRule;
import com.promanatia.openbravonetsuiteintegration.utility.RowContext;

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
		return invoice + "-" + subsidiary;
	}

}