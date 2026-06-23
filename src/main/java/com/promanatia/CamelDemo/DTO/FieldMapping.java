package com.promanatia.CamelDemo.DTO;

public class FieldMapping {

    private String sourceColumn;
    private String targetColumn;
    private String transformationRuleCode;
    private Integer sequenceNo;

    public String getSourceColumn() {
        return sourceColumn;
    }

    public void setSourceColumn(String sourceColumn) {
        this.sourceColumn = sourceColumn;
    }

    public String getTargetColumn() {
        return targetColumn;
    }

    public void setTargetColumn(String targetColumn) {
        this.targetColumn = targetColumn;
    }

    public String getTransformationRuleCode() {
        return transformationRuleCode;
    }

    public void setTransformationRuleCode(String transformationRuleCode) {
        this.transformationRuleCode = transformationRuleCode;
    }

    public Integer getSequenceNo() {
        return sequenceNo;
    }

    public void setSequenceNo(Integer sequenceNo) {
        this.sequenceNo = sequenceNo;
    }
}