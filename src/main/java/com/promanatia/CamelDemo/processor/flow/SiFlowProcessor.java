package com.promanatia.CamelDemo.processor.flow;

import com.promanatia.CamelDemo.DTO.FlowType;
import com.promanatia.CamelDemo.Interface.FlowProcessor;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

@Component
public class SiFlowProcessor implements FlowProcessor {

    @Override
    public FlowType getFlowType() {
        return FlowType.SalesInvoice;
    }

    @Override
    public void process(Exchange exchange) throws Exception {

        // SI logic goes here

    }

}
