package com.promanatia.CamelDemo.processor;

import com.promanatia.CamelDemo.DTO.FlowType;
import com.promanatia.CamelDemo.Interface.FlowProcessor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class FlowProcessorFactory {

    private final Map<FlowType, FlowProcessor> processorMap = new HashMap<>();

    public FlowProcessorFactory(List<FlowProcessor> processors) {

        for (FlowProcessor processor : processors) {
            processorMap.put(processor.getFlowType(), processor);
        }

    }

    public FlowProcessor getProcessor(FlowType flowType) {

        FlowProcessor processor = processorMap.get(flowType);

        if (processor == null) {
            throw new RuntimeException("No processor found for " + flowType);
        }

        return processor;

    }

}