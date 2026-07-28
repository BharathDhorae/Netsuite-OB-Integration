package com.promanatia.CamelDemo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.promanatia.CamelDemo.DTO.FieldMappingDTO;
import com.promanatia.CamelDemo.utility.CsvParser;
import com.promanatia.CamelDemo.utility.RowContext;
import com.promanatia.CamelDemo.utility.TransformationUtil;

@Service
public class CsvMappingService {

	private final CsvParser csvParser;
	private final TransformationUtil transformationUtil;

	public CsvMappingService(CsvParser csvParser, TransformationUtil transformationUtil) {
		this.csvParser = csvParser;
		this.transformationUtil = transformationUtil;
	}

	public String generateMappedCsv(List<FieldMappingDTO> mappings, String[] headers, List<String> validRows) {

		StringBuilder outputCsv = new StringBuilder();

		// Header
		for (int i = 0; i < mappings.size(); i++) {
			outputCsv.append(escapeCsv(mappings.get(i).getTargetColumn()));

			if (i < mappings.size() - 1) {
				outputCsv.append(",");
			}
		}

		outputCsv.append("\n");

		// Data
		for (String row : validRows) {

			String[] columns = csvParser.parseCsvLine(row);
			RowContext context = new RowContext(headers, columns);
			for (int i = 0; i < mappings.size(); i++) {
				outputCsv.append(escapeCsv(transformationUtil.applyTransformation(context, mappings.get(i))));
				if (i < mappings.size() - 1) {
					outputCsv.append(",");
				}
			}

			outputCsv.append("\n");
		}

		return outputCsv.toString();
	}

	private String escapeCsv(String value) {

		if (value == null) {
			return "";
		}

		if (value.contains("\"")) {
			value = value.replace("\"", "\"\"");
		}

		if (value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r")) {
			return "\"" + value + "\"";
		}

		return value;
	}
}