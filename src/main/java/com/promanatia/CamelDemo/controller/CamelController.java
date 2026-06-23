package com.promanatia.CamelDemo.controller;

import com.promanatia.CamelDemo.DTO.Order;
import org.apache.camel.ProducerTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class CamelController {
    private final ProducerTemplate producerTemplate;

    public CamelController(ProducerTemplate producerTemplate) {
        this.producerTemplate = producerTemplate;
    }

    @PostMapping("/process")
    public String processOrder(@RequestBody Order order) {
        producerTemplate.sendBody("direct:processOrder", order);
        return "Order submitted for processing";
    }
}
