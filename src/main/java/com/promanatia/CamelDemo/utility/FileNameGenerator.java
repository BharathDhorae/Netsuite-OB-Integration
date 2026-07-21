package com.promanatia.CamelDemo.utility;

import org.springframework.stereotype.Component;

import com.promanatia.CamelDemo.DTO.EntityMasterDTO;

@Component
public class FileNameGenerator {

	public String buildSuccessFileName(EntityMasterDTO entity) {
		return entity.getEntityName() + "_" + System.currentTimeMillis() + ".csv";
	}

	public String buildErrorFileName(EntityMasterDTO entity) {
		return entity.getEntityName() + "_ERROR_" + System.currentTimeMillis() + ".csv";
	}
}
