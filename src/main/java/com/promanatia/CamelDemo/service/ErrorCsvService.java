package com.promanatia.CamelDemo.service;

import com.promanatia.CamelDemo.DTO.FlowType;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class ErrorCsvService {

    /**
     * Generate error CSV for failed records
     */
    public void generateErrorCsv(Exchange exchange) {

        Set<String> failedOrders =
                exchange.getProperty("failedOrders", Set.class);

        FlowType flowType =
                exchange.getProperty("FLOW_TYPE", FlowType.class);

        if (failedOrders == null || failedOrders.isEmpty()) {
            exchange.setProperty("errorCsv", "");
            exchange.setProperty("hasFailedOrders", false);
            return;
        }

        StringBuilder errorCsv = new StringBuilder();

        // Header
        errorCsv.append("documentno").append("\n");

        // Rows
        for (String id : failedOrders) {

            if (id != null && !id.trim().isEmpty()) {
                errorCsv.append(id.trim()).append("\n");
            }
        }

        exchange.setProperty("errorCsv", errorCsv.toString());
        exchange.setProperty("hasFailedOrders", true);
        exchange.setProperty("ERROR_FILE_NAME",
                buildErrorFileName(flowType));
    }

    /**
     * Build error file name per flow
     */
    private String buildErrorFileName(FlowType flowType) {

        String timestamp =
                String.valueOf(System.currentTimeMillis());

        return flowType.getOutputFileName()
                + "_ERROR_"
                + timestamp
                + ".csv";
    }
}