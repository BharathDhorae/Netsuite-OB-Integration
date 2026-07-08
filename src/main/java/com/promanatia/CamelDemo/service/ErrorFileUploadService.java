package com.promanatia.CamelDemo.service;

import com.promanatia.CamelDemo.DTO.FlowType;
import com.promanatia.CamelDemo.config.SftpConfig;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Service;

@Service
public class ErrorFileUploadService {

	private final SftpConfig sftpConfig;

	public ErrorFileUploadService(SftpConfig sftpConfig) {
		this.sftpConfig = sftpConfig;
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
		exchange.setProperty("ERROR_SFTP_URI", buildErrorSftpUri());
	}
	
	 private String buildErrorSftpUri() {

	        return "sftp://"
	                + sftpConfig.getHost()
	                + ":"
	                + sftpConfig.getPort()
	                + sftpConfig.getErrorDirectory()
	                + "?username="
	                + sftpConfig.getUsername()
	                + "&password="
	                + sftpConfig.getPassword()
	                + "&binary=true";
	    }

	private String buildFallbackFileName(FlowType flowType) {
		String timestamp = String.valueOf(System.currentTimeMillis());
		return flowType.getOutputFileName() + "_ERROR_" + timestamp + ".csv";
	}
}