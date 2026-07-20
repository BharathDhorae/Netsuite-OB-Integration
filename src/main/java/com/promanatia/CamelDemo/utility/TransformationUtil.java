package com.promanatia.CamelDemo.utility;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Locale;

import org.springframework.stereotype.Component;

@Component
public class TransformationUtil {

	private static final DateTimeFormatter INPUT_DATE_FORMATTER = new DateTimeFormatterBuilder()
			.appendPattern("yyyy-MM-dd HH:mm:ss").appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true).toFormatter();

	private static final DateTimeFormatter OUTPUT_DATE_FORMATTER = DateTimeFormatter.ofPattern("MMddyyyy");
	private static final DateTimeFormatter OUTPUT_MONTH_YEAR_FORMATTER = DateTimeFormatter.ofPattern("MMM-yy",
			Locale.ENGLISH);

	public String applyTransformation(String value, String rule) {

		if (value == null) {
			return "";
		}

		if (rule == null || rule.isBlank()) {
			return value;
		}

		switch (rule.trim().toUpperCase()) {
		case "DIRECT":
			return value;
		case "DATE_MMDDYYYY":
			return convertDate(value);
		case "DATE_MMYYYY":
			return convertMonthYear(value);
		default:
			throw new RuntimeException("Unsupported transformation rule: " + rule);
		}
	}

	private String convertDate(String value) {

		if (value == null || value.isBlank()) {
			return "";
		}

		LocalDateTime dateTime = LocalDateTime.parse(value.trim(), INPUT_DATE_FORMATTER);
		return dateTime.format(OUTPUT_DATE_FORMATTER);
	}

	private String convertMonthYear(String value) {

		if (value == null || value.isBlank()) {
			return "";
		}

		LocalDateTime dateTime = LocalDateTime.parse(value.trim(), INPUT_DATE_FORMATTER);
		return dateTime.format(OUTPUT_MONTH_YEAR_FORMATTER);
	}

}
