package com.promanatia.CamelDemo.service;

import com.promanatia.CamelDemo.DTO.ApplicationLogEntity;
import com.promanatia.CamelDemo.repository.ApplicationLogRepository;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CsvProcessingService {

    private static final int DOCUMENT_NO_INDEX = 0;
    private static final int PRODUCT_INDEX = 7;

    private final CsvValidator csvValidator;
    private final CsvMappingService csvMappingService;
    private final ApplicationLogRepository applicationLogRepository;

    public CsvProcessingService(
            CsvValidator csvValidator,
            CsvMappingService csvMappingService,
            ApplicationLogRepository applicationLogRepository) {

        this.csvValidator = csvValidator;
        this.csvMappingService = csvMappingService;
        this.applicationLogRepository = applicationLogRepository;
    }

    public void processCsvFile(Exchange exchange) {

        String fileContent = exchange.getIn().getBody(String.class);
        String[] rows = fileContent.split("\\r?\\n");

        csvValidator.validateFile(rows);

        String[] headers = rows[0].split(",", -1);

        List<String> validRows = new ArrayList<>();
        Set<String> failedOrders = new HashSet<>();

        for (int i = 1; i < rows.length; i++) {

            String row = rows[i];

            if (csvValidator.isEmpty(row)) continue;

            String[] cols = row.split(",", -1);

            String orderId = csvValidator.getColumnValue(cols, DOCUMENT_NO_INDEX);

            try {
                csvValidator.validateRow(cols, headers, i, row);
                validRows.add(row);
            } catch (Exception e) {
                failedOrders.add(orderId);
            }
        }

        String mappedCsv =
                validRows.isEmpty()
                        ? ""
                        : csvMappingService.generateMappedCsv(headers, validRows);

        String errorCsv = generateErrorCsv(failedOrders);

        exchange.setProperty("mappedCsv", mappedCsv);
        exchange.setProperty("errorCsv", errorCsv);

        exchange.setProperty("hasValidRows", !validRows.isEmpty());
        exchange.setProperty("hasFailedOrders", !failedOrders.isEmpty());

        exchange.getIn().setBody(mappedCsv);
    }

    private String generateErrorCsv(Set<String> failedOrders) {

        StringBuilder sb = new StringBuilder();
        sb.append("documentno\n");

        for (String id : failedOrders) {
            sb.append(id).append("\n");
        }

        return sb.toString();
    }
}