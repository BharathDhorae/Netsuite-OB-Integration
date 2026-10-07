package com.promanatia.openbravonetsuiteintegration.utility;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;

import com.promanatia.openbravonetsuiteintegration.dto.EntityMasterDTO;

@Component
public class FileNameGenerator {

	private static final DateTimeFormatter FILE_NAME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmm");

	public String buildSuccessFileName(EntityMasterDTO entity) {
		String dateTime = LocalDateTime.now().format(FILE_NAME_FORMATTER);
		return dateTime + "_" + entity.getEntityName() + ".csv";
	}

	public String buildErrorFileName(EntityMasterDTO entity) {
		String dateTime = LocalDateTime.now().format(FILE_NAME_FORMATTER);
		return dateTime + "_" + entity.getEntityName() + "_ERROR.csv";
	}
}