package com.promanatia.CamelDemo.processor;

import com.promanatia.CamelDemo.DTO.OrderEntity;
import com.promanatia.CamelDemo.exception.DocumentIdMissingException;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component
public class OrderValidationProcessor implements Processor {

    @Override
    public void process(Exchange exchange) {

        OrderEntity order = exchange.getIn().getBody(OrderEntity.class);

        if (order.getDocumentId() == null ||
                order.getDocumentId().isBlank()) {

            throw new DocumentIdMissingException(
                    "Document ID is missing for order"
            );
        }

        exchange.getIn().setHeader(
                "orderNo",
                order.getOrderNo()
        );
    }
}