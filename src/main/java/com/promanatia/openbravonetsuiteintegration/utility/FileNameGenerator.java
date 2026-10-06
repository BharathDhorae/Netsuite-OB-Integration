package com.promanatia.openbravonetsuiteintegration.utility;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;

import com.promanatia.openbravonetsuiteintegration.dto.EntityMasterDTO;

@Component
public class FileNameGenerator {

	String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

	public String buildSuccessFileName(EntityMasterDTO entity) {
		return date + "_" + entity.getEntityName() + ".csv";
	}

	public String buildErrorFileName(EntityMasterDTO entity) {
		return date + "_" + "_ERROR_" + entity.getEntityName() + ".csv";
	}
}
