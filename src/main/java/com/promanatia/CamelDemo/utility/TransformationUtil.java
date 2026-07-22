package com.promanatia.CamelDemo.utility;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.promanatia.CamelDemo.DTO.FieldMappingEntity;
import com.promanatia.CamelDemo.DTO.OrgSubsidaryDto;
import com.promanatia.CamelDemo.repository.OrgSubsidaryRepository;

@Component
public class TransformationUtil {

	private final OrgSubsidaryRepository orgSubsidaryRepository;

	// Cache to avoid repeated DB calls
	private final Map<String, OrgSubsidaryDto> orgCache = new ConcurrentHashMap<>();

	public TransformationUtil(OrgSubsidaryRepository orgSubsidaryRepository) {
		this.orgSubsidaryRepository = orgSubsidaryRepository;
	}

	private static final DateTimeFormatter DATE_TIME_FORMATTER = new DateTimeFormatterBuilder()
			.appendPattern("yyyy-MM-dd HH:mm:ss").appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true).toFormatter();

	private static final DateTimeFormatter DATE_FORMATTER1 = DateTimeFormatter.ofPattern("M/d/yyyy");

	private static final DateTimeFormatter DATE_FORMATTER2 = DateTimeFormatter.ofPattern("MM/dd/yyyy");

	private static final DateTimeFormatter DATE_FORMATTER3 = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	private static final DateTimeFormatter OUTPUT_MONTH_YEAR_FORMATTER = DateTimeFormatter.ofPattern("MMM-yy",
			Locale.ENGLISH);

	public String applyTransformation(String[] columns, String[] headers, FieldMappingEntity mapping) {

		String value = "";

		int index = mapping.getSequenceNo() - 1;

		if (index >= 0 && index < columns.length) {
			value = columns[index];
		}

		String ruleCode = mapping.getTransformationRuleCode();

		if (ruleCode == null || ruleCode.isBlank()) {
			return value;
		}

		return switch (ruleCode.trim().toUpperCase()) {

		case "DIRECT" -> getRuleCodeValue(value, ruleCode, mapping.getDefaultValue());

		case "DATE_MMDDYYYY" -> convertDate(value);

		case "DATE_MMYYYY" -> convertMonthYear(value);

		case "SUBSIDIARY" -> getSubsidiary(columns, headers);

		case "ITEM_LINE_LOCATION" -> getItemLineLocation(columns, headers);

		case "CUSTBODY_OB_INVOICE_NO" -> getCustbodyObInvoiceNo(columns, headers);

		default -> throw new RuntimeException("Unsupported transformation rule : " + ruleCode);
		};
	}

	/**
	 * Returns OrgSubsidaryDto from cache/database
	 */
	private OrgSubsidaryDto getOrgSubsidary(String[] columns, String[] headers) {

		String organization = getColumnValue(headers, columns, "organization");

		if (organization == null || organization.isBlank()) {
			return null;
		}

		return orgCache.computeIfAbsent(organization.trim(), orgSubsidaryRepository::findByEntityName);
	}

	/**
	 * Returns Subsidiary based on Document No
	 */
	private String getSubsidiary(String[] columns, String[] headers) {

		OrgSubsidaryDto dto = getOrgSubsidary(columns, headers);

		if (dto == null) {
			return "";
		}

		String documentNo = getColumnValue(headers, columns, "documentno");

		if (documentNo != null && documentNo.trim().endsWith("A")) {
			return dto.getAksharpithSubsidary();
		}

		return dto.getSubsidary();
	}

	/**
	 * Returns Item Line Location
	 */
	private String getItemLineLocation(String[] columns, String[] headers) {

		OrgSubsidaryDto dto = getOrgSubsidary(columns, headers);

		return dto == null ? "" : dto.getItemLineLocation();
	}

	/**
	 * Returns Invoice No + Subsidiary
	 */
	private String getCustbodyObInvoiceNo(String[] columns, String[] headers) {

		String documentNo = getColumnValue(headers, columns, "documentno");

		return (documentNo == null ? "" : documentNo.trim()) + "-" + getSubsidiary(columns, headers);
	}

	/**
	 * Returns column value by column name
	 */
	private String getColumnValue(String[] headers, String[] columns, String columnName) {

		for (int i = 0; i < headers.length; i++) {

			if (headers[i].equalsIgnoreCase(columnName)) {

				return i < columns.length ? columns[i].trim() : "";
			}
		}

		return "";
	}

	/**
	 * Direct transformation
	 */
	private String getRuleCodeValue(String value, String ruleCode, String defaultValue) {

		if (defaultValue != null && !defaultValue.isBlank()) {
			return defaultValue;
		}

		return value == null ? "" : value;
	}

	/**
	 * Converts to MMddyyyy
	 */
	private String convertDate(String value) {

		if (value == null || value.isBlank()) {
			return "";
		}

		return parseDate(value).format(DATE_FORMATTER2);
	}

	/**
	 * Converts to MMM-yy
	 */
	private String convertMonthYear(String value) {

		if (value == null || value.isBlank()) {
			return "";
		}

		return parseDate(value).format(OUTPUT_MONTH_YEAR_FORMATTER);
	}

	/**
	 * Parses supported date formats
	 */
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