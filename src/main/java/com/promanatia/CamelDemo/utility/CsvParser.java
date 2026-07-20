package com.promanatia.CamelDemo.utility;

import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

@Component
public class CsvParser {

	/**
	 * Parses a single CSV line into its column values using Apache Commons CSV,
	 * correctly handling commas embedded inside quoted fields and escaped quotes
	 * (""). Note: parses one line at a time, so it does NOT handle a quoted field
	 * containing an embedded newline (that needs whole-document parsing instead).
	 */
	public String[] parseCsvLine(String line) {
		try (CSVParser parser = CSVParser.parse(line, CSVFormat.DEFAULT.withIgnoreSurroundingSpaces(false))) {
			List<CSVRecord> records = parser.getRecords();
			if (records.isEmpty()) {
				return new String[0];
			}
			CSVRecord record = records.get(0);
			String[] values = new String[record.size()];
			for (int i = 0; i < record.size(); i++) {
				values[i] = record.get(i);
			}
			return values;
		} catch (Exception e) {
			throw new RuntimeException("Failed to parse CSV line: " + line, e);
		}
	}

}
