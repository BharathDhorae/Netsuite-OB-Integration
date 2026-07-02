package com.promanatia.CamelDemo.utility;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CsvAggregationStrategy implements AggregationStrategy {

    private static final Logger logger =
            LoggerFactory.getLogger(CsvAggregationStrategy.class);

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {

        String fileName =
                newExchange.getIn().getHeader("CamelFileName", String.class);

        String body =
                newExchange.getIn().getBody(String.class);

        if (body == null || body.trim().isEmpty()) {
            return oldExchange == null ? newExchange : oldExchange;
        }

        // FIRST FILE
        if (oldExchange == null) {

            List<String> files = new ArrayList<>();
            files.add(fileName);

            newExchange.setProperty("processedFiles", files);
            newExchange.getIn().setBody(body);

            return newExchange;
        }

        // MERGE FILES
        String oldBody = oldExchange.getIn().getBody(String.class);

        StringBuilder merged = new StringBuilder(oldBody);

        String[] rows = body.split("\\r?\\n");

        for (int i = 1; i < rows.length; i++) {
            merged.append("\n").append(rows[i]);
        }

        oldExchange.getIn().setBody(merged.toString());

        List<String> files =
                oldExchange.getProperty("processedFiles", List.class);

        if (files == null) files = new ArrayList<>();

        if (!files.contains(fileName)) {
            files.add(fileName);
        }

        oldExchange.setProperty("processedFiles", files);

        logger.info("Batch Files: {}", files);

        return oldExchange;
    }
}