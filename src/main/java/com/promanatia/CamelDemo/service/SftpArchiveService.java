package com.promanatia.CamelDemo.service;

import com.promanatia.CamelDemo.DTO.FlowType;
import com.promanatia.CamelDemo.config.SftpConfig;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SftpArchiveService {

	private final SftpConfig sftpConfig;

	public SftpArchiveService(SftpConfig sftpConfig) {
		this.sftpConfig = sftpConfig;
	}

	public void archiveProcessedFiles(Exchange exchange) {

		@SuppressWarnings("unchecked")
		List<String> processedFiles = exchange.getProperty("PROCESSED_FILES", List.class);

		FlowType flowType = exchange.getProperty("FLOW_TYPE", FlowType.class);
		if (processedFiles == null || processedFiles.isEmpty()) {
			return;
		}
		for (String fileName : processedFiles) {
			if (fileName == null || fileName.isBlank()) {
				continue;
			}
			moveFileToArchive(fileName);
		}
	}

	private void moveFileToArchive(String fileName) {

		String sourceUri = sftpConfig.getSftpSourceUri(fileName);
		String targetUri = sftpConfig.getArchiveUri(fileName);

	}
}