package com.promanatia.CamelDemo.utility;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

@Component
public class CsvParser {

	private static final CSVFormat CSV_FORMAT = CSVFormat.DEFAULT.builder().setIgnoreSurroundingSpaces(false).build();

	public String[] parseCsvLine(String line) {

		if (line == null || line.isEmpty()) {
			return new String[0];
		}

		try (CSVParser parser = new CSVParser(new StringReader(line), CSV_FORMAT)) {

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
		} catch (IOException e) {
			throw new RuntimeException("Failed to parse CSV line : " + line, e);
		}
	}
}