//package com.Promanatia.CamelDemo.CamleInfo;
//
//import org.apache.camel.builder.RouteBuilder;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Component;
//
////@Component
//public class RoutesInfo extends RouteBuilder {
//
//    private static final Logger log = LoggerFactory.getLogger(RoutesInfo.class);
//
//    @Override
//    public void configure() throws Exception {
//
//        from("timer:myTimer?period=10000")
//                .setBody(simple("Hello from Timer ${header.CamelTimerCounter}"))
//                .log("Received message: ${body}")
//                .to("log:mylog");
//
//    }
//}