package com.promanatia.openbravonetsuiteintegration.utility;

import org.springframework.stereotype.Component;

import com.promanatia.openbravonetsuiteintegration.DTO.EntityMasterDTO;

@Component
public class FileNameGenerator {

	public String buildSuccessFileName(EntityMasterDTO entity) {
		return entity.getEntityName() + "_" + System.currentTimeMillis() + ".csv";
	}

	public String buildErrorFileName(EntityMasterDTO entity) {
		return entity.getEntityName() + "_ERROR_" + System.currentTimeMillis() + ".csv";
	}
}
