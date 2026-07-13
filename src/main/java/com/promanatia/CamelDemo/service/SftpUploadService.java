package com.promanatia.CamelDemo.service;

import com.promanatia.CamelDemo.DTO.FlowType;
import com.promanatia.CamelDemo.config.SftpConfig;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Service;

@Service
public class SftpUploadService {

	private final SftpConfig sftpConfig;

	public SftpUploadService(SftpConfig sftpConfig) {
		this.sftpConfig = sftpConfig;
	}

	public void uploadSuccessFile(Exchange exchange) {

		String csv = exchange.getProperty("mappedCsv", String.class);
		FlowType flowType = exchange.getProperty("FLOW_TYPE", FlowType.class);

		if (csv == null || csv.isBlank()) {
			throw new RuntimeException("Mapped CSV is empty");
		}

		String fileName = flowType.getOutputFileName() + "_" + System.currentTimeMillis() + ".csv";
		exchange.getIn().setBody(csv);
		exchange.getIn().setHeader("CamelFileName", fileName);
	}

	public void uploadErrorFile(Exchange exchange) {

		String errorCsv = exchange.getProperty("errorCsv", String.class);
		Boolean hasFailed = exchange.getProperty("hasFailedOrders", Boolean.class);
		FlowType flowType = exchange.getProperty("FLOW_TYPE", FlowType.class);
		if (hasFailed == null || !hasFailed || errorCsv == null || errorCsv.isBlank()) {
			return;
		}

		String fileName = exchange.getProperty("ERROR_FILE_NAME", String.class);
		if (fileName == null) {
			fileName = buildFallbackFileName(flowType);
		}

		exchange.getIn().setBody(errorCsv);
		exchange.getIn().setHeader("CamelFileName", fileName);
	}

	private String buildFallbackFileName(FlowType flowType) {
		String timestamp = String.valueOf(System.currentTimeMillis());
		return flowType.getOutputFileName() + "_ERROR_" + timestamp + ".csv";
	}

}