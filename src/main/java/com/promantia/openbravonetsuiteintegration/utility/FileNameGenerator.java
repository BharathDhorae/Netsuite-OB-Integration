package com.promantia.openbravonetsuiteintegration.utility;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;

import com.promantia.openbravonetsuiteintegration.dto.EntityMasterDTO;

@Component
public class FileNameGenerator {

	private static final DateTimeFormatter FILE_NAME_FORMATTER = DateTimeFormatter.ofPattern("MMddyy-HHmmss");

	public String buildSuccessFileName(EntityMasterDTO entity) {
		String dateTime = LocalDateTime.now().format(FILE_NAME_FORMATTER);
		return entity.getTargetObjectName() + "_" + dateTime + ".csv";
	}

	public String buildErrorFileName(EntityMasterDTO entity) {
		String dateTime = LocalDateTime.now().format(FILE_NAME_FORMATTER);
		return entity.getTargetObjectName() + "_" + dateTime + "_ERROR.csv";
	}
}