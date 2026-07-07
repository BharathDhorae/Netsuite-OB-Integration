package com.promanatia.CamelDemo.utility;

import com.promanatia.CamelDemo.DTO.FlowType;
import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CsvAggregationStrategy implements AggregationStrategy {

    private static final String PROCESSED_FILES = "PROCESSED_FILES";

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {

        try {

            String fileName =
                    newExchange.getIn().getHeader("CamelFileName", String.class);

            FlowType flowType =
                    newExchange.getProperty("FLOW_TYPE", FlowType.class);

            String body =
                    newExchange.getIn().getBody(String.class);

            if (body == null || body.trim().isEmpty()) {
                return oldExchange != null ? oldExchange : newExchange;
            }

            /*
             * FIRST FILE IN BATCH
             */
            if (oldExchange == null) {

                List<String> files = new ArrayList<>();
                files.add(fileName);

                newExchange.setProperty(PROCESSED_FILES, files);

                // Keep full CSV as starting point
                newExchange.getIn().setBody(body);

                return newExchange;
            }

            /*
             * MERGING LOGIC
             * We remove header from next files
             */
            String oldBody =
                    oldExchange.getIn().getBody(String.class);

            String[] newRows =
                    body.split("\\r?\\n");

            StringBuilder merged = new StringBuilder(oldBody);

            for (int i = 1; i < newRows.length; i++) {

                if (newRows[i] == null || newRows[i].trim().isEmpty()) {
                    continue;
                }

                merged.append("\n").append(newRows[i]);
            }

            oldExchange.getIn().setBody(merged.toString());

            /*
             * Track file names per flow
             */
            List<String> files =
                    oldExchange.getProperty(PROCESSED_FILES, List.class);

            if (files == null) {
                files = new ArrayList<>();
            }

            if (!files.contains(fileName)) {
                files.add(fileName);
            }

            oldExchange.setProperty(PROCESSED_FILES, files);

            return oldExchange;

        } catch (Exception e) {
            throw new RuntimeException(
                    "Error while aggregating CSV files", e);
        }
    }
}