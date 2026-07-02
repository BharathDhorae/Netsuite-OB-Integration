package com.promanatia.CamelDemo.service;

import com.promanatia.CamelDemo.DTO.FieldMappingEntity;
import com.promanatia.CamelDemo.repository.FieldMappingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CsvMappingService {

    @Autowired
    private FieldMappingRepository fieldMappingRepository;

    public String generateMappedCsv(String[] headers, List<String> validRows) {

        List<FieldMappingEntity> mappings =
                fieldMappingRepository.getMappings("c_order");

        Map<String, Integer> sourceHeaderMap = new HashMap<>();

        // Store source header -> column index
        for (int i = 0; i < headers.length; i++) {
            sourceHeaderMap.put(headers[i].trim().toLowerCase(), i);
        }

        StringBuilder outputCsv = new StringBuilder();

        // Generate  Headers
        for (int i = 0; i < mappings.size(); i++) {{

            outputCsv.append(mappings.get(i).getTargetColumn());

            if (i < mappings.size() - 1) {
                outputCsv.append(",");
            }
        }
        }

        outputCsv.append("\n");

        // Process each row
        for (String row : validRows) {

            String[] columns = row.split(",", -1);

            for (int i = 0; i < mappings.size(); i++) {

                FieldMappingEntity mapping = mappings.get(i);

                Integer sourceIndex = sourceHeaderMap.get(
                        mapping.getSourceColumn()
                                .trim()
                                .toLowerCase());

                String value = "";

                // Read value from source CSV
                if (sourceIndex != null && sourceIndex < columns.length) {
                    value = columns[sourceIndex];
                }

                // Apply transformation
                value = applyTransformation(
                        value,
                        mapping.getTransformationRuleCode());

                outputCsv.append(value);

                if (i < mappings.size() - 1) {
                    outputCsv.append(",");
                }
            }

            outputCsv.append("\n");
        }

        return outputCsv.toString();
    }

    private String applyTransformation(String value,
                                       String transformationRuleCode) {

        if (value == null) {
            value = "";
        }

        if (transformationRuleCode == null ||
                transformationRuleCode.isBlank()) {
            return value;
        }

        switch (transformationRuleCode.trim().toUpperCase()) {

            case "DIRECT":
                return value;

            case "DATE_FORMAT":
                return convertDate(value);

            default:
                throw new IllegalArgumentException(
                        "Unsupported Transformation Rule : "
                                + transformationRuleCode);
        }
    }

    private String convertDate(String value) {

        if (value == null || value.isBlank()) {
            return "";
        }

        try {
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MMddyyyy");

            LocalDateTime dateTime = LocalDateTime.parse(value.trim(), inputFormatter);

            return dateTime.format(outputFormatter);

        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid date format: " + value, e);
        }
    }

}
