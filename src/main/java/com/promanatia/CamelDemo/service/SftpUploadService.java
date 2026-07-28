package com.promanatia.CamelDemo.service;

import com.promanatia.CamelDemo.DTO.EntityMasterDTO;
import com.promanatia.CamelDemo.utility.FileNameGenerator;

import org.apache.camel.Exchange;
import org.springframework.stereotype.Service;

@Service
public class SftpUploadService {

	private final FileNameGenerator fileNameGenerator;

	public SftpUploadService(FileNameGenerator fileNameGenerator) {
		this.fileNameGenerator = fileNameGenerator;
	}

	public void uploadSuccessFile(Exchange exchange) {

		String csv = exchange.getProperty("mappedCsv", String.class);
		EntityMasterDTO entity = exchange.getProperty("entity", EntityMasterDTO.class);

		if (csv == null || csv.isBlank()) {
			throw new RuntimeException("Mapped CSV is empty");
		}

		String fileName = fileNameGenerator.buildSuccessFileName(entity);
		exchange.getIn().setBody(csv);
		exchange.getIn().setHeader("CamelFileName", fileName);
	}

	public void uploadErrorFile(Exchange exchange) {

		String errorCsv = exchange.getProperty("errorCsv", String.class);
		Boolean hasFailed = exchange.getProperty("hasFailedOrders", Boolean.class);
		EntityMasterDTO entity = exchange.getProperty("entity", EntityMasterDTO.class);
		if (hasFailed == null || !hasFailed || errorCsv == null || errorCsv.isBlank()) {
			return;
		}

		String fileName = exchange.getProperty("ERROR_FILE_NAME", String.class);
		if (fileName == null) {
			fileName = fileNameGenerator.buildErrorFileName(entity);
		}

		exchange.getIn().setBody(errorCsv);
		exchange.getIn().setHeader("CamelFileName", fileName);
	}
}