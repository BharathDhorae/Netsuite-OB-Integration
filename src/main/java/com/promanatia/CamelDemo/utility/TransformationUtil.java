package com.promanatia.CamelDemo.utility;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Locale;

import org.springframework.stereotype.Component;

@Component
public class TransformationUtil {

	private static final DateTimeFormatter DATE_TIME_FORMATTER = new DateTimeFormatterBuilder()
			.appendPattern("yyyy-MM-dd HH:mm:ss").appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true).toFormatter();

	private static final DateTimeFormatter DATE_FORMATTER1 = DateTimeFormatter.ofPattern("M/d/yyyy");

	private static final DateTimeFormatter DATE_FORMATTER2 = DateTimeFormatter.ofPattern("MM/dd/yyyy");

	private static final DateTimeFormatter DATE_FORMATTER3 = DateTimeFormatter.ofPattern("yyyy-MM-dd");

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

		return parseDate(value).format(OUTPUT_DATE_FORMATTER);
	}

	private String convertMonthYear(String value) {

		if (value == null || value.isBlank()) {
			return "";
		}

		return parseDate(value).format(OUTPUT_MONTH_YEAR_FORMATTER);
	}

	private LocalDate parseDate(String value) {

		value = value.trim();

		try {
			return LocalDateTime.parse(value, DATE_TIME_FORMATTER).toLocalDate();
		} catch (Exception ignored) {
		}

		try {
			return LocalDate.parse(value, DATE_FORMATTER1);
		} catch (Exception ignored) {
		}

		try {
			return LocalDate.parse(value, DATE_FORMATTER2);
		} catch (Exception ignored) {
		}

		try {
			return LocalDate.parse(value, DATE_FORMATTER3);
		} catch (Exception ignored) {
		}

		throw new RuntimeException("Unsupported date format : " + value);
	}

}
