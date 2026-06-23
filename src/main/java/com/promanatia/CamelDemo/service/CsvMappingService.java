package com.promanatia.CamelDemo.service;

import com.promanatia.CamelDemo.DTO.FieldMapping;
import com.promanatia.CamelDemo.repository.FieldMappingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CsvMappingService {

    @Autowired
    private FieldMappingRepository fieldMappingRepository;

    public String generateMappedCsv(String[] headers, List<String> validRows) {

        List<FieldMapping> mappings =
                fieldMappingRepository.getMappings("c_order");

        Map<String, Integer> sourceHeaderMap = new HashMap<>();

        for (int i = 0; i < headers.length; i++) {
            sourceHeaderMap.put(headers[i].trim().toLowerCase(), i);
        }

        StringBuilder outputCsv = new StringBuilder();

        for (int i = 0; i < mappings.size(); i++) {
            outputCsv.append(mappings.get(i).getTargetColumn());

            if (i < mappings.size() - 1) {
                outputCsv.append(",");
            }
        }

        outputCsv.append("\n");

        for (String row : validRows) {

            String[] columns = row.split(",", -1);

            for (int i = 0; i < mappings.size(); i++) {

                FieldMapping mapping = mappings.get(i);

                Integer sourceIndex =
                        sourceHeaderMap.get(
                                mapping.getSourceColumn()
                                        .trim()
                                        .toLowerCase());

                String value = "";

                if (sourceIndex != null && sourceIndex < columns.length) {
                    value = columns[sourceIndex];
                }

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
