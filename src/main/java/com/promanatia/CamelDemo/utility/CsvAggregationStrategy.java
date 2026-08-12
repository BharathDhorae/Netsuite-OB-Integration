package com.promanatia.CamelDemo.utility;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

@Component
public class CsvAggregationStrategy implements AggregationStrategy {

	private static final String PROCESSED_FILES = "PROCESSED_FILES";

	private final CsvParser csvParser;

	public CsvAggregationStrategy(CsvParser csvParser) {
		this.csvParser = csvParser;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {

		try {

			String fileName = newExchange.getIn().getHeader("CamelFileName", String.class);

			String body = newExchange.getIn().getBody(String.class);

			if (body == null || body.isBlank()) {
				return oldExchange != null ? oldExchange : newExchange;
			}

			/*
			 * Parse the COMPLETE CSV.
			 *
			 * DO NOT use:
			 *
			 * body.split("\\r?\\n")
			 *
			 * because a quoted CSV field can contain a newline.
			 */
			List<String> records = csvParser.parseCsvRecords(body);

			if (records.isEmpty()) {
				return oldExchange != null ? oldExchange : newExchange;
			}

			/*
			 * First record is the header.
			 */
			String header = records.get(0);

			/*
			 * Data records only.
			 */
			List<String> dataRecords = new ArrayList<>();

			for (int i = 1; i < records.size(); i++) {

				String record = records.get(i);

				if (record != null && !record.isBlank()) {
					dataRecords.add(record);
				}
			}

			/*
			 * First exchange of the aggregation.
			 */
			if (oldExchange == null) {

				List<String> files = new ArrayList<>();

				if (fileName != null) {
					files.add(fileName);
				}

				newExchange.setProperty(PROCESSED_FILES, files);

				/*
				 * Keep complete CSV records.
				 */
				StringBuilder merged = new StringBuilder();

				merged.append(header);

				for (String record : dataRecords) {
					merged.append("\n");
					merged.append(record);
				}

				newExchange.getIn().setBody(merged.toString());

				return newExchange;
			}

			/*
			 * Existing aggregation.
			 */
			String oldBody = oldExchange.getIn().getBody(String.class);

			StringBuilder merged = new StringBuilder(oldBody);

			/*
			 * Only append DATA records.
			 *
			 * Header is not appended again.
			 */
			for (String record : dataRecords) {

				if (record == null || record.isBlank()) {
					continue;
				}

				merged.append("\n");
				merged.append(record);
			}

			oldExchange.getIn().setBody(merged.toString());

			List<String> files = oldExchange.getProperty(PROCESSED_FILES, List.class);

			if (files == null) {
				files = new ArrayList<>();
			}

			if (fileName != null && !files.contains(fileName)) {
				files.add(fileName);
			}

			oldExchange.setProperty(PROCESSED_FILES, files);

			return oldExchange;

		} catch (Exception e) {

			throw new RuntimeException("Error while aggregating CSV files", e);
		}
	}
}