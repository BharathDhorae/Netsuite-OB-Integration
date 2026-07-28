package com.promanatia.CamelDemo.service;

import com.promanatia.CamelDemo.DTO.EntityMasterDTO;
import com.promanatia.CamelDemo.utility.FileNameGenerator;

import org.apache.camel.Exchange;
import org.springframework.stereotype.Service;

@Service
public class ErrorCsvService {

	private final FileNameGenerator fileNameGenerator;

	public ErrorCsvService(FileNameGenerator fileNameGenerator) {
		this.fileNameGenerator = fileNameGenerator;
	}

	public void generateErrorCsv(Exchange exchange) {

		java.util.List<String> errorRows = exchange.getProperty("errorRows", java.util.List.class);
		String[] headers = exchange.getProperty("headers", String[].class);
		EntityMasterDTO entity = exchange.getProperty("entity", EntityMasterDTO.class);
		if (errorRows == null || errorRows.isEmpty()) {

			exchange.setProperty("errorCsv", "");
			exchange.setProperty("hasFailedOrders", false);
			return;
		}

		StringBuilder errorCsv = new StringBuilder();

		errorCsv.append(String.join(",", headers));
		errorCsv.append("\n");

		for (String row : errorRows) {

			errorCsv.append(row);
			errorCsv.append("\n");
		}

		exchange.setProperty("errorCsv", errorCsv.toString());
		exchange.setProperty("hasFailedOrders", true);
		exchange.setProperty("ERROR_FILE_NAME", fileNameGenerator.buildErrorFileName(entity));
	}
}