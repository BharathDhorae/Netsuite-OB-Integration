package com.promanatia.CamelDemo.service;

import com.promanatia.CamelDemo.DTO.FlowType;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CsvProcessingService {

    private static final int DOCUMENT_NO_INDEX = 0;
    private static final int PRODUCT_INDEX = 7;

    private final CsvValidator csvValidator;
    private final CsvMappingService csvMappingService;

    public CsvProcessingService(
            CsvValidator csvValidator,
            CsvMappingService csvMappingService) {

        this.csvValidator = csvValidator;
        this.csvMappingService = csvMappingService;
    }

    public void processCsvFile(Exchange exchange) {

        String fileContent =
                exchange.getIn().getBody(String.class);

        String[] rows =
                fileContent.split("\\r?\\n");

        // Validate file
        csvValidator.validateFile(rows);

        String[] headers =
                rows[0].split(",", -1);

        List<String> validRows = new ArrayList<>();
        Set<String> failedOrders = new HashSet<>();

        FlowType flowType =
                exchange.getProperty("FLOW_TYPE", FlowType.class);

        for (int i = 1; i < rows.length; i++) {

            String row = rows[i];

            if (csvValidator.isEmpty(row)) {
                continue;
            }

            String[] cols =
                    row.split(",", -1);

            String orderId =
                    csvValidator.getColumnValue(cols, DOCUMENT_NO_INDEX);

            String productId =
                    csvValidator.getColumnValue(cols, PRODUCT_INDEX);

            try {

                csvValidator.validateRow(cols, headers, i, row);

                validRows.add(row);

            } catch (Exception e) {

                failedOrders.add(orderId);

                // ⚠️ Removed hardcoded SO
                // You can later plug logging service here per flow

            }
        }

        // -----------------------------
        // Mapping (FIXED CALL HERE)
        // -----------------------------
        String mappedCsv =
                validRows.isEmpty()
                        ? ""
                        : csvMappingService.generateMappedCsv(
                        flowType,
                        headers,
                        validRows);

        // -----------------------------
        // Error CSV
        // -----------------------------
        String errorCsv =
                generateErrorCsv(failedOrders);

        // -----------------------------
        // Exchange properties
        // -----------------------------
        exchange.setProperty("mappedCsv", mappedCsv);
        exchange.setProperty("errorCsv", errorCsv);

        exchange.setProperty("hasValidRows", !validRows.isEmpty());
        exchange.setProperty("hasFailedOrders", !failedOrders.isEmpty());

        exchange.setProperty("failedOrders", failedOrders);

        exchange.getIn().setBody(mappedCsv);
    }

    /**
     * Generate error CSV for failed records
     */
    private String generateErrorCsv(Set<String> failedOrders) {

        StringBuilder sb = new StringBuilder();
        sb.append("documentno\n");

        for (String id : failedOrders) {

            if (id != null && !id.trim().isEmpty()) {
                sb.append(id.trim()).append("\n");
            }
        }

        return sb.toString();
    }
}