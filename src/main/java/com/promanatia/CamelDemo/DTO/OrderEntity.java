package com.promanatia.CamelDemo.DTO;

import lombok.Data;

@Data
public class OrderEntity{
    private String documentId;
    private String orderNo;
    private Double amount;

    @Override
    public String toString() {
        return "{ " +
                "\"documentId\":\"" + documentId + "\"," +
                "\"orderNo\":\"" + orderNo + "\"," +
                "\"amount\":" + amount +
                " }";
    }
}
