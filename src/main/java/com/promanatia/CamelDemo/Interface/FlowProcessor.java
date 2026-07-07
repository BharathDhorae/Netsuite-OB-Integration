package com.promanatia.CamelDemo.Interface;

import com.promanatia.CamelDemo.DTO.FlowType;
import org.apache.camel.Exchange;

public interface FlowProcessor {

    FlowType getFlowType();

    void process(Exchange exchange) throws Exception;
}