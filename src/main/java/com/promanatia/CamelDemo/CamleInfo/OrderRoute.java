package com.promanatia.CamelDemo.CamleInfo;
import com.promanatia.CamelDemo.DTO.OrderEntity;
import com.promanatia.CamelDemo.processor.DeadLetterProcessor;
import com.promanatia.CamelDemo.processor.OrderValidationProcessor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class OrderRoute extends RouteBuilder {
    private final OrderValidationProcessor validationProcessor;
    private final DeadLetterProcessor deadLetterProcessor;

    public OrderRoute(OrderValidationProcessor validationProcessor, DeadLetterProcessor deadLetterProcessor) {
        this.validationProcessor = validationProcessor;
        this.deadLetterProcessor = deadLetterProcessor;
    }

    @Override
    public void configure() {
        onException(Exception.class).
                handled(true)
                .setHeader("LOG_LEVEL", constant("ERROR"))
                .setHeader("LOG_MESSAGE", simple("${exception.message}"))
                .process(deadLetterProcessor);

        from("direct:processOrder")
                .marshal().json() .setHeader("LOG_LEVEL", constant("INFO"))
                .setHeader("LOG_MESSAGE", constant("Order Received"))
                .process(deadLetterProcessor)
                .unmarshal().json(OrderEntity.class)
                .process(validationProcessor)
                .setHeader("LOG_LEVEL", constant("INFO"))
                .setHeader("LOG_MESSAGE", constant("Validation Successful"))
                .process(deadLetterProcessor) .log("Processing Order : ${body.orderNo}")
                .setHeader("LOG_LEVEL", constant("INFO"))
                .setHeader("LOG_MESSAGE", constant("Order Processed Successfully"))
                .process(deadLetterProcessor);
    }
}