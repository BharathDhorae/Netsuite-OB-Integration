package com.promanatia.CamelDemo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "integration_mapping")
public class MappingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String flowType;

    private String externalInstance;

    private String openbravoTable;

    private String openbravoRecordId;

    private String externalEntity;

    private String externalRecordId;

    private String status;

    public MappingEntity() {
    }

    public Long getId() {
        return id;
    }

    public String getFlowType() {
        return flowType;
    }

    public void setFlowType(String flowType) {
        this.flowType = flowType;
    }

    public String getExternalInstance() {
        return externalInstance;
    }

    public void setExternalInstance(String externalInstance) {
        this.externalInstance = externalInstance;
    }

    public String getOpenbravoTable() {
        return openbravoTable;
    }

    public void setOpenbravoTable(String openbravoTable) {
        this.openbravoTable = openbravoTable;
    }

    public String getOpenbravoRecordId() {
        return openbravoRecordId;
    }

    public void setOpenbravoRecordId(String openbravoRecordId) {
        this.openbravoRecordId = openbravoRecordId;
    }

    public String getExternalEntity() {
        return externalEntity;
    }

    public void setExternalEntity(String externalEntity) {
        this.externalEntity = externalEntity;
    }

    public String getExternalRecordId() {
        return externalRecordId;
    }

    public void setExternalRecordId(String externalRecordId) {
        this.externalRecordId = externalRecordId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}