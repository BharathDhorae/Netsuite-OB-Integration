package com.promanatia.openbravonetsuiteintegration.transformation.openbravo.salesinvoice.impl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.promanatia.openbravonetsuiteintegration.dto.FieldMappingDTO;
import com.promanatia.openbravonetsuiteintegration.transformation.TransformationRule;
import com.promanatia.openbravonetsuiteintegration.utility.DateUtil;
import com.promanatia.openbravonetsuiteintegration.utility.RowContext;

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
			Matcher m = Pattern.compile("\\d+").matcher(paymentTerm);
			if (m.find()) {
				dueDate = dueDate.plusDays(Integer.parseInt(m.group()));
			}
		}
		return dueDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
	}

}