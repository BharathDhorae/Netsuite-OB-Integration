package com.promanatia.CamelDemo.DTO;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationLogDTO {

	private Long id;
	private Timestamp logTime;
	private String product;
	private String flowType;
	private String documentId;
	private String logLevel;
	private String message;
	private String errorMessage;

}