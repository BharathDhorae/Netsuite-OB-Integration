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

    /**
     * Moves processed files to archive folder
     */
    public void archiveProcessedFiles(Exchange exchange) {

        @SuppressWarnings("unchecked")
        List<String> processedFiles =
                exchange.getProperty("PROCESSED_FILES", List.class);

        FlowType flowType =
                exchange.getProperty("FLOW_TYPE", FlowType.class);

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

    /**
     * Move single file to archive folder (SFTP)
     */
    private void moveFileToArchive(String fileName) {

        // Source file path
        String sourceUri =
                buildSftpSourceUri(fileName);

        // Destination archive path
        String targetUri =
                buildArchiveUri(fileName);

        // NOTE:
        // In Camel route we will use these URIs with:
        // toD(sourceUri) + toD(targetUri)

        // For now we only prepare logic
    }

    /**
     * Build source SFTP URI
     */
    private String buildSftpSourceUri(String fileName) {

        return "sftp://"
                + sftpConfig.getHost()
                + ":"
                + sftpConfig.getPort()
                + sftpConfig.getRemoteDirectory()
                + "/"
                + fileName
                + "?username="
                + sftpConfig.getUsername()
                + "&password="
                + sftpConfig.getPassword()
                + "&binary=true";
    }

    /**
     * Build archive SFTP URI
     */
    private String buildArchiveUri(String fileName) {

        return "sftp://"
                + sftpConfig.getHost()
                + ":"
                + sftpConfig.getPort()
                + sftpConfig.getArchiveDirectory()
                + "/"
                + fileName
                + "?username="
                + sftpConfig.getUsername()
                + "&password="
                + sftpConfig.getPassword()
                + "&binary=true";
    }
}