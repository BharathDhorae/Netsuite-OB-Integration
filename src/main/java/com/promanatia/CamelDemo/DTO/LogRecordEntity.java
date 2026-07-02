package com.promanatia.CamelDemo.DTO;

import lombok.Data;

@Data
public class LogRecordEntity {

    private String dateTime;
    private String orderNo;
    private String logLevel;
    private String message;
    private String payload;

    public LogRecordEntity(String dateTime,
                     String orderNo,
                     String logLevel,
                     String message,
                     String payload) {
        this.dateTime = dateTime;
        this.orderNo = orderNo;
        this.logLevel = logLevel;
        this.message = message;
        this.payload = payload;
    }

}