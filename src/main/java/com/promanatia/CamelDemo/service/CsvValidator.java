package com.promanatia.CamelDemo.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.promanatia.CamelDemo.DTO.FieldMappingEntity;

@Service
public class CsvValidator {

	public void validateFile(String[] rows) {

		if (rows == null || rows.length == 0) {
			throw new RuntimeException("CSV file is empty");
		}

		if (rows.length <= 1) {
			throw new RuntimeException("CSV file is empty or contains only header.");
		}

		if (rows[0] == null || rows[0].trim().isEmpty()) {
			throw new RuntimeException("CSV header is missing");
		}
	}

	/**
	 * Validate each row based on headers
	 */
	public List<String> validateRow(String[] columns, String[] headers, int rowNum, String row,
			List<FieldMappingEntity> fieldMappings) {

		List<String> errors = new ArrayList<>();

		if (columns == null || columns.length == 0) {
			errors.add("Empty row found at Row " + (rowNum + 1));
			return errors;
		}

		for (int colNum = 0; colNum < headers.length; colNum++) {

			// Skip optional columns
			if ("N".equalsIgnoreCase(isOptionalColumn(colNum, fieldMappings))) {
				continue;
			}

			// Missing column
			if (colNum >= columns.length) {
				errors.add("Missing column '" + getColumnName(headers, colNum) + "' at Row " + (rowNum + 1));
				continue;
			}

			// Empty value
			if (isEmpty(columns[colNum])) {
				errors.add(
						"Empty value found in Column '" + getColumnName(headers, colNum) + "' at Row " + (rowNum + 1));
			}
		}

		return errors;
	}

	/**
	 * Optional column rules (same as your logic)
	 */
	private String isOptionalColumn(int colNum, List<FieldMappingEntity> fieldMappings) {
		return fieldMappings.get(colNum).getMandatory();
	}

	/**
	 * Get safe column name
	 */
	private String getColumnName(String[] headers, int colNum) {

		if (headers != null && colNum < headers.length) {
			return headers[colNum].trim();
		}

		return "Unknown Column";
	}

	/**
	 * Safe column value fetch
	 */
	public String getColumnValue(String[] columns, int columnIndex) {

		if (columns == null || columnIndex >= columns.length || columns[columnIndex] == null) {
			return "";
		}

		return columns[columnIndex].trim();
	}

	public boolean isEmpty(String value) {

		if (value == null) {
			return true;
		}

		value = value.trim();
		if (value.length() >= 2 && value.startsWith("\"") && value.endsWith("\"")) {
			value = value.substring(1, value.length() - 1).trim();
		}

		return value.isEmpty();
	}

	/**
	 * Validate CSV header row
	 */
	public void validateHeader(String[] headers, List<FieldMappingEntity> fieldMappings) {

		if (headers == null || headers.length == 0 || headers.length != fieldMappings.size()) {
			throw new RuntimeException("CSV header is missing or empty");
		}

		for (int i = 0; i < headers.length; i++) {

			if (headers[i] == null || headers[i].trim().isEmpty()) {
				throw new RuntimeException("Empty header found at column index: " + i);
			}
			if (!headers[i].equalsIgnoreCase(fieldMappings.get(i).getSourceColumn())) {
				throw new RuntimeException("Header column mismatch with table coulum : " + i);
			}
		}
	}
}