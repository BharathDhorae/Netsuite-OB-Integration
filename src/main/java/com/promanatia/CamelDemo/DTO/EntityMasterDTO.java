package com.promanatia.CamelDemo.DTO;

import lombok.Data;

@Data
public class EntityMasterDTO {

	private Long entityId;
	private String entityName;
	private String sourceTableName;
	private String targetObjectName;
	private String sourceSystem;
	private String targetSystem;

}
