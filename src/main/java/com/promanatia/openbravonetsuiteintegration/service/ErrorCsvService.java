package com.promanatia.openbravonetsuiteintegration.service;

import com.promanatia.openbravonetsuiteintegration.dto.EntityMasterDTO;
import com.promanatia.openbravonetsuiteintegration.utility.FileNameGenerator;

import java.util.List;

import org.apache.camel.Exchange;
import org.springframework.stereotype.Service;

@Service
public class ErrorCsvService {

	private static final String CSV_LINE_SEPARATOR = "\r\n";

	private final FileNameGenerator fileNameGenerator;

	public ErrorCsvService(FileNameGenerator fileNameGenerator) {
		this.fileNameGenerator = fileNameGenerator;
	}

	public void generateErrorCsv(Exchange exchange) {

		List<String> errorRows = exchange.getProperty("errorRows", List.class);
		String[] headers = exchange.getProperty("headers", String[].class);
		EntityMasterDTO entity = exchange.getProperty("entity", EntityMasterDTO.class);
		if (errorRows == null || errorRows.isEmpty()) {

			exchange.setProperty("errorCsv", "");
			exchange.setProperty("hasFailedOrders", false);
			return;
		}

		StringBuilder errorCsv = new StringBuilder();

		errorCsv.append(String.join(",", headers));
		errorCsv.append(CSV_LINE_SEPARATOR);

		for (String row : errorRows) {

			errorCsv.append(row);
			errorCsv.append(CSV_LINE_SEPARATOR);
		}

		exchange.setProperty("errorCsv", errorCsv.toString());
		exchange.setProperty("hasFailedOrders", true);
		exchange.setProperty("ERROR_FILE_NAME", fileNameGenerator.buildErrorFileName(entity));
	}
}