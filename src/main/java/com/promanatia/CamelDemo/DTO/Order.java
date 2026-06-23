package com.promanatia.CamelDemo.DTO;

public class Order {
    private String documentId;
    private String orderNo;
    private Double amount;

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "{ " +
                "\"documentId\":\"" + documentId + "\"," +
                "\"orderNo\":\"" + orderNo + "\"," +
                "\"amount\":" + amount +
                " }";
    }
}
