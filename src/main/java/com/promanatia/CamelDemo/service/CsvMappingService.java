package com.promanatia.CamelDemo.service;

import com.promanatia.CamelDemo.DTO.FieldMappingEntity;
import com.promanatia.CamelDemo.utility.CsvParser;
import com.promanatia.CamelDemo.utility.TransformationUtil;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CsvMappingService {

	private final CsvParser csvParser;
	private final TransformationUtil transformationUtil;

	public CsvMappingService(CsvParser csvParser, TransformationUtil transformationUtil) {
		this.csvParser = csvParser;
		this.transformationUtil = transformationUtil;
	}

	public String generateMappedCsv(List<FieldMappingEntity> mappings, String[] headers, List<String> validRows) {

		StringBuilder outputCsv = new StringBuilder();

		for (int i = 0; i < mappings.size(); i++) {
			outputCsv.append(mappings.get(i).getTargetColumn());
			if (i < mappings.size() - 1) {
				outputCsv.append(",");
			}
		}

		outputCsv.append("\n");

		for (String row : validRows) {
			String[] columns = csvParser.parseCsvLine(row);
			for (int i = 0; i < mappings.size(); i++) {

				FieldMappingEntity mapping = mappings.get(i);
				String value = "";
				Integer index = mapping.getSequenceNo() - 1;

				if (index != null && index >= 0 && index < columns.length) {
					value = columns[index];
				}

				value = transformationUtil.applyTransformation(value, mapping.getTransformationRuleCode());
				outputCsv.append(value);

				if (i < mappings.size() - 1) {
					outputCsv.append(",");
				}
			}
			outputCsv.append("\n");
		}
		return outputCsv.toString();
	}

}