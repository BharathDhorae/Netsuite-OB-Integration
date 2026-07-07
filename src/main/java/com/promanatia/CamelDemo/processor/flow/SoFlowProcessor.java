package com.promanatia.CamelDemo.processor.flow;

import com.promanatia.CamelDemo.DTO.FlowType;
import com.promanatia.CamelDemo.Interface.FlowProcessor;
import com.promanatia.CamelDemo.service.CsvProcessingService;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

@Component
public class SoFlowProcessor implements FlowProcessor {

    private final CsvProcessingService csvProcessingService;

    public SoFlowProcessor(CsvProcessingService csvProcessingService) {
        this.csvProcessingService = csvProcessingService;
    }

    @Override
    public FlowType getFlowType() {
        return FlowType.SalesOrder;
    }

    @Override
    public void process(Exchange exchange) throws Exception {

        csvProcessingService.processCsvFile(exchange);

    }

}