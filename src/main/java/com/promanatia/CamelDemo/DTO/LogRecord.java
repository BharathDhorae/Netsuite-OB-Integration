package com.promanatia.CamelDemo.DTO;

public class LogRecord {

    private String dateTime;
    private String orderNo;
    private String logLevel;
    private String message;
    private String payload;

    public LogRecord(String dateTime,
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

    public String getDateTime() {
        return dateTime;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public String getLogLevel() {
        return logLevel;
    }

    public String getMessage() {
        return message;
    }

    public String getPayload() {
        return payload;
    }
}