package com.promanatia.CamelDemo.service;

import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class CsvProcessingService {

    @Autowired
    private CsvValidator csvValidator;

    @Autowired
    private CsvMappingService csvMappingService;

    public void processCsvFile(Exchange exchange) {

        String fileContent = exchange.getIn().getBody(String.class);
        String[] rows = fileContent.split("\\r?\\n");

        csvValidator.validateFile(rows);

        String[] headers = rows[0].split(",", -1);

        List<String> validRows = new ArrayList<>();
        List<String> failedRows = new ArrayList<>();
        Set<String> failedOrders = new HashSet<>();

        for (int rowNum = 1; rowNum < rows.length; rowNum++) {

            String row = rows[rowNum];

            if (csvValidator.isEmpty(row)) {
                continue;
            }

            String[] columns = row.split(",", -1);
            String orderId = csvValidator.getDocumentNo(columns);

            if (failedOrders.contains(orderId)) {
                continue;
            }

            try {
                csvValidator.validateRow(columns, headers, rowNum, row);
                validRows.add(row);

            } catch (RuntimeException ex) {
                failedOrders.add(orderId);
                failedRows.add(row);
            }
        }

        String generatedCsv =
                csvMappingService.generateMappedCsv(headers, validRows);

        String errorCsv =
                generateErrorCsv(failedOrders);

        exchange.setProperty("validRows", validRows);
        exchange.setProperty("failedRows", failedRows);
        exchange.setProperty("failedOrders", failedOrders);

        exchange.setProperty("errorCsv", errorCsv);

        exchange.setProperty("hasFailedOrders",
                !failedOrders.isEmpty());

        if (validRows.isEmpty()) {
            exchange.setProperty("hasValidRows", false);
            exchange.getIn().setBody("");
        } else {
            exchange.setProperty("hasValidRows", true);
            exchange.getIn().setBody(generatedCsv);
        }
    }

    private String generateErrorCsv(Set<String> failedOrders) {

        StringBuilder errorCsv = new StringBuilder();

        errorCsv.append("documentno").append("\n");

        for (String documentNo : failedOrders) {
            errorCsv.append(documentNo).append("\n");
        }

        return errorCsv.toString();
    }
}