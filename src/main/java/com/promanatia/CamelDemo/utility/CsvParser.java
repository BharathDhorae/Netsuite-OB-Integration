package com.promanatia.CamelDemo.utility;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

@Component
public class CsvParser {

	private static final CSVFormat CSV_FORMAT = CSVFormat.DEFAULT.builder().setIgnoreSurroundingSpaces(false)
			.setIgnoreEmptyLines(false).build();

	/**
	 * Parse one CSV line/record.
	 *
	 * Use this only when the supplied string represents a complete CSV record.
	 */
	public String[] parseCsvLine(String line) {

		if (line == null || line.isEmpty()) {
			return new String[0];
		}

		try (CSVParser parser = CSVParser.parse(line, CSV_FORMAT)) {

			for (CSVRecord record : parser) {

				String[] values = new String[record.size()];

				for (int i = 0; i < record.size(); i++) {
					values[i] = record.get(i);
				}

				return values;
			}

			return new String[0];

		} catch (IOException e) {
			throw new RuntimeException("Failed to parse CSV record", e);
		}
	}

	/**
	 * Parse the complete CSV file.
	 *
	 * IMPORTANT: Do not split the CSV using \\r?\\n.
	 *
	 * Apache Commons CSV correctly handles fields containing embedded newlines.
	 */
	public List<String[]> parseCsv(String content) {

		List<String[]> rows = new ArrayList<>();

		if (content == null || content.isEmpty()) {
			return rows;
		}

		try (CSVParser parser = CSVParser.parse(new StringReader(content), CSV_FORMAT)) {

			for (CSVRecord record : parser) {

				String[] values = new String[record.size()];

				for (int i = 0; i < record.size(); i++) {
					values[i] = record.get(i);
				}

				rows.add(values);
			}

			return rows;

		} catch (IOException e) {
			throw new RuntimeException("Failed to parse CSV content", e);
		}
	}

	/**
	 * Converts a CSV record back into a valid CSV record string.
	 *
	 * This is important for multiline fields because the newline inside a quoted
	 * field must remain inside the quotes.
	 */
	public String toCsvRecord(String[] values) {

		try {
			StringWriter writer = new StringWriter();

			try (CSVPrinter printer = new CSVPrinter(writer, CSV_FORMAT)) {
				printer.printRecord((Object[]) values);
			}

			String result = writer.toString();

			if (result.endsWith("\r\n")) {
				result = result.substring(0, result.length() - 2);
			} else if (result.endsWith("\n")) {
				result = result.substring(0, result.length() - 1);
			}

			return result;

		} catch (IOException e) {
			throw new RuntimeException("Failed to convert CSV record", e);
		}
	}

	/**
	 * Parse complete CSV content and return records as valid CSV strings.
	 *
	 * Header is included.
	 */
	public List<String> parseCsvRecords(String content) {

		List<String> records = new ArrayList<>();

		if (content == null || content.isEmpty()) {
			return records;
		}

		try (CSVParser parser = CSVParser.parse(new StringReader(content), CSV_FORMAT)) {

			for (CSVRecord record : parser) {

				String[] values = new String[record.size()];

				for (int i = 0; i < record.size(); i++) {
					values[i] = record.get(i);
				}

				records.add(toCsvRecord(values));
			}

			return records;

		} catch (IOException e) {
			throw new RuntimeException("Failed to parse CSV records", e);
		}
	}
}