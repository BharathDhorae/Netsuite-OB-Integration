package com.promanatia.CamelDemo.DTO;

import lombok.Data;

@Data
public class FieldMappingDTO {

	private String sourceColumn;
	private String targetColumn;
	private String transformationRuleCode;
	private Integer sequenceNo;
	private String mandatory;
	private String defaultValue;

}