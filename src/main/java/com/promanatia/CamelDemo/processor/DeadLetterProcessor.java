package com.promanatia.CamelDemo.processor;

import com.promanatia.CamelDemo.DTO.LogRecordEntity;
import com.promanatia.CamelDemo.service.DeadLetterService;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class DeadLetterProcessor implements Processor {

    private final DeadLetterService deadLetterService;

    public DeadLetterProcessor(DeadLetterService deadLetterService) {
        this.deadLetterService = deadLetterService;
    }

    @Override
    public void process(Exchange exchange) {

        String orderNo =
                exchange.getIn().getHeader("orderNo", String.class);

        if (orderNo == null) {
            orderNo = "N/A";
        }

        String level =
                exchange.getIn().getHeader("LOG_LEVEL", String.class);

        String message =
                exchange.getIn().getHeader("LOG_MESSAGE", String.class);

        String payload =
                exchange.getIn().getBody(String.class);

        String dateTime = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        LogRecordEntity record = new LogRecordEntity(
                dateTime,
                orderNo,
                level,
                message,
                payload
        );

        deadLetterService.save(record);
    }
}