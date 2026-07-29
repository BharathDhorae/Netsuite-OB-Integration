package com.promanatia.CamelDemo.transformation.openbravo.salesinvoice.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.promanatia.CamelDemo.DTO.FieldMappingDTO;
import com.promanatia.CamelDemo.transformation.TransformationRule;
import com.promanatia.CamelDemo.utility.RowContext;

@Component
public class CustomerEmailAddressRule implements TransformationRule {

	@Override
	public String getRuleCode() {
		return "CUSTOMER_EMAIL_ADDRESS";
	}

	@Override
	public String transform(RowContext row, FieldMappingDTO mapping) {

		String value = row.get(mapping.getSourceColumn());

		if (value == null || value.isBlank()) {
			return mapping.getDefaultValue();
		}

		List<String> validEmails = new ArrayList<>();
		String[] emails = value.split(",");

		for (String email : emails) {
			email = email.trim();
			if (isValidEmail(email)) {
				validEmails.add(email);
			}
		}

		if (validEmails.isEmpty()) {
			return mapping.getDefaultValue();
		}
		return String.join(",", validEmails);
	}

	private boolean isValidEmail(String email) {

		email = email == null ? "" : email.trim();

		// Basic checks
		if (email.isEmpty())
			return false;
		if (email.contains(" "))
			return false;
		if (email.indexOf('@') == -1)
			return false;
		if (email.indexOf('@') != email.lastIndexOf('@'))
			return false;

		char first = email.charAt(0);
		char last = email.charAt(email.length() - 1);

		if (first == '-' || first == '.' || first == '+')
			return false;
		if (last == '-' || last == '.' || last == '+')
			return false;

		int atIndex = email.indexOf('@');
		int dotAfterAt = email.indexOf('.', atIndex);

		if (dotAfterAt == -1 || dotAfterAt == atIndex + 1) {
			return false;
		}

		// User part
		String userPart = email.substring(0, atIndex);

		char[] invalidChars = { '!', '"', '#', '$', '%', '^', '&', '*', '(', ')', '=', '+', '{', '}', '[', ']', ':',
				';', '\'', '<', '>', '|', '/', '\\', ',' };

		for (char ch : invalidChars) {
			if (userPart.indexOf(ch) >= 0) {
				return false;
			}
		}

		return true;
	}

}
