package com.promanatia.CamelDemo.service;

import com.promanatia.CamelDemo.DTO.FieldMappingEntity;
import com.promanatia.CamelDemo.DTO.FlowType;
import com.promanatia.CamelDemo.repository.FieldMappingRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CsvMappingService {

    private final FieldMappingRepository fieldMappingRepository;

    public CsvMappingService(FieldMappingRepository fieldMappingRepository) {
        this.fieldMappingRepository = fieldMappingRepository;
    }

    /**
     * Main method: converts validated CSV into mapped CSV
     */
    public String generateMappedCsv(FlowType flowType,
                                    String[] headers,
                                    List<String> validRows) {

        List<FieldMappingEntity> mappings =
                fieldMappingRepository.getMappings(
                        flowType.getSourceTable());

        if (mappings == null || mappings.isEmpty()) {
            throw new RuntimeException(
                    "No field mappings found for flow: " + flowType);
        }

        Map<String, Integer> sourceHeaderMap = buildHeaderIndex(headers);

        StringBuilder outputCsv = new StringBuilder();

        // ---------------------------
        // Build Target Headers
        // ---------------------------
        for (int i = 0; i < mappings.size(); i++) {

            outputCsv.append(mappings.get(i).getTargetColumn());

            if (i < mappings.size() - 1) {
                outputCsv.append(",");
            }
        }

        outputCsv.append("\n");

        // ---------------------------
        // Process Rows
        // ---------------------------
        for (String row : validRows) {

            String[] columns = row.split(",", -1);

            for (int i = 0; i < mappings.size(); i++) {

                FieldMappingEntity mapping = mappings.get(i);

                String value = "";

                Integer sourceIndex =
                        sourceHeaderMap.get(
                                mapping.getSourceColumn()
                                        .trim()
                                        .toLowerCase());

                if (sourceIndex != null
                        && sourceIndex < columns.length) {
                    value = columns[sourceIndex];
                }

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

    /**
     * Build header → index map
     */
    private Map<String, Integer> buildHeaderIndex(String[] headers) {

        Map<String, Integer> map = new HashMap<>();

        if (headers == null) {
            return map;
        }

        for (int i = 0; i < headers.length; i++) {

            if (headers[i] != null) {
                map.put(headers[i].trim().toLowerCase(), i);
            }
        }

        return map;
    }

    /**
     * Apply transformation rules
     */
    private String applyTransformation(String value,
                                       String rule) {

        if (value == null) {
            return "";
        }

        if (rule == null || rule.isBlank()) {
            return value;
        }

        switch (rule.trim().toUpperCase()) {

            case "DIRECT":
                return value;

            case "UPPERCASE":
                return value.toUpperCase();

            case "LOWERCASE":
                return value.toLowerCase();

            case "DATE_FORMAT":
                return convertDate(value);

            default:
                throw new RuntimeException(
                        "Unsupported transformation rule: " + rule);
        }
    }

    /**
     * Date conversion utility
     */
    private String convertDate(String value) {

        if (value == null || value.isBlank()) {
            return "";
        }

        try {

            DateTimeFormatter inputFormatter =
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

            DateTimeFormatter outputFormatter =
                    DateTimeFormatter.ofPattern("MMddyyyy");

            LocalDateTime dateTime =
                    LocalDateTime.parse(value.trim(), inputFormatter);

            return dateTime.format(outputFormatter);

        } catch (Exception e) {
            throw new RuntimeException(
                    "Invalid date format: " + value, e);
        }
    }
}