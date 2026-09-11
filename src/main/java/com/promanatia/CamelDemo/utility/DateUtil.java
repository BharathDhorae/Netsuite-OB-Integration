package com.promanatia.CamelDemo.utility;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Locale;

public final class DateUtil {

	private DateUtil() {
	}

	private static final DateTimeFormatter DATE_TIME = new DateTimeFormatterBuilder()
			.appendPattern("yyyy-MM-dd HH:mm:ss").appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true).toFormatter();

	private static final DateTimeFormatter M_D_YYYY = DateTimeFormatter.ofPattern("M/d/yyyy");

	private static final DateTimeFormatter MM_DD_YYYY = DateTimeFormatter.ofPattern("MM/dd/yyyy");

	private static final DateTimeFormatter YYYY_MM_DD = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	private static final List<DateTimeFormatter> INPUTS = List.of(DATE_TIME, M_D_YYYY, MM_DD_YYYY, YYYY_MM_DD);

	public static LocalDate parse(String value) {

		if (value == null || value.isBlank()) {
			return null;
		}

		value = value.trim();

		for (DateTimeFormatter formatter : INPUTS) {

			try {

				if (formatter == DATE_TIME) {
					return LocalDateTime.parse(value, formatter).toLocalDate();
				}

				return LocalDate.parse(value, formatter);

			} catch (Exception ignored) {
			}
		}

		throw new RuntimeException("Unsupported Date : " + value);
	}

	public static String toMMddyyyy(String value) {

		LocalDate date = parse(value);

		if (date == null) {
			return "";
		}

		return date.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
	}

	public static String toMonthYear(String value) {

		LocalDate date = parse(value);

		if (date == null) {
			return "";
		}

		return date.format(DateTimeFormatter.ofPattern("MMM yyyy", Locale.ENGLISH));
	}

	public static String toDayMonth(String value) {
		LocalDate date = parse(value);

		if (date == null) {
			return "";
		}

		return date.format(DateTimeFormatter.ofPattern("dd-MMM", Locale.ENGLISH));
	}

}