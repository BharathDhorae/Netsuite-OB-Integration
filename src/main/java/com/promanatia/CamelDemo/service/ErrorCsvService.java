package com.promanatia.CamelDemo.service;

import com.promanatia.CamelDemo.DTO.FlowType;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class ErrorCsvService {

    public void generateErrorCsv(Exchange exchange) {

        java.util.List<String> errorRows =
                exchange.getProperty("errorRows", java.util.List.class);

        String[] headers =
                exchange.getProperty("headers", String[].class);

        FlowType flowType =
                exchange.getProperty("FLOW_TYPE", FlowType.class);

        if (errorRows == null || errorRows.isEmpty()) {

            exchange.setProperty("errorCsv", "");
            exchange.setProperty("hasFailedOrders", false);
            return;
        }

        StringBuilder errorCsv = new StringBuilder();

        errorCsv.append(String.join(",", headers));
        errorCsv.append("\n");

        for (String row : errorRows) {

            errorCsv.append(row);
            errorCsv.append("\n");
        }

        exchange.setProperty("errorCsv", errorCsv.toString());
        exchange.setProperty("hasFailedOrders", true);
        exchange.setProperty(
                "ERROR_FILE_NAME",
                buildErrorFileName(flowType)
        );
    }

    private String buildErrorFileName(FlowType flowType) {

        String timestamp =
                String.valueOf(System.currentTimeMillis());

        return flowType.getOutputFileName()
                + "_ERROR_"
                + timestamp
                + ".csv";
    }
}