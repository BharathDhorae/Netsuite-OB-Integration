package com.promanatia.CamelDemo.transformation.openbravo.salesinvoice.impl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;

import com.promanatia.CamelDemo.DTO.FieldMappingDTO;
import com.promanatia.CamelDemo.transformation.TransformationRule;
import com.promanatia.CamelDemo.utility.DateUtil;
import com.promanatia.CamelDemo.utility.RowContext;

@Component
public class DueDateRule implements TransformationRule {

	@Override
	public String getRuleCode() {
		return "DUE_DATE";
	}

	@Override
	public String transform(RowContext row, FieldMappingDTO mapping) {

		String invoiceDate = row.get("DateInvoiced");

		if (invoiceDate.isBlank()) {
			return "";
		}

		LocalDate dueDate = DateUtil.parse(invoiceDate);

		String paymentTerm = row.get("PaymentTerms");
		if (!paymentTerm.isBlank()) {
			String days = paymentTerm.replaceAll("[^0-9]", "");

			if (!days.isBlank()) {
				dueDate = dueDate.plusDays(Integer.parseInt(days));
			}
		}
		return dueDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
	}

}