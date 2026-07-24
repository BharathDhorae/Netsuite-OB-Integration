package com.promanatia.CamelDemo.transformation.impl;

import org.springframework.stereotype.Component;

import com.promanatia.CamelDemo.DTO.FieldMappingEntity;
import com.promanatia.CamelDemo.transformation.TransformationRule;
import com.promanatia.CamelDemo.utility.RowContext;

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
	public String transform(RowContext row, FieldMappingEntity mapping) {
		String invoice = row.get("documentno");
		String subsidiary = subsidiaryRule.transform(row, mapping);
		return invoice + "-" + subsidiary;
	}

}