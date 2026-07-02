package com.promanatia.CamelDemo.service;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.promanatia.CamelDemo.config.SftpConfig;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SftpArchiveService {

    private static final Logger logger =
            LoggerFactory.getLogger(SftpArchiveService.class);

    private final SftpConfig sftpConfig;

    public SftpArchiveService(SftpConfig sftpConfig) {
        this.sftpConfig = sftpConfig;
    }

    public void archiveProcessedFiles(Exchange exchange) {

        List<String> files =
                exchange.getProperty("processedFiles", List.class);

        if (files == null || files.isEmpty()) {
            logger.warn("No files to archive");
            return;
        }

        Session session = null;
        ChannelSftp channel = null;

        try {

            JSch jsch = new JSch();

            session = jsch.getSession(
                    sftpConfig.getUsername(),
                    sftpConfig.getHost(),
                    sftpConfig.getPort()
            );

            session.setPassword(sftpConfig.getPassword());
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            channel = (ChannelSftp) session.openChannel("sftp");
            channel.connect();

            for (String file : files) {

                String src = "/home/openbravoetluser"+sftpConfig.getRemoteDirectory() + "/" + file;
                String dest = sftpConfig.getArchiveDirectory() + "/" + file;

                try {
                    logger.info("SOwdjsakhdicnsiriewirce {}",src);
                    logger.info("ipoewudbsfde987f98e7r98798r79 {}",dest);
                    channel.rename(src, dest);
                    logger.info("Archived: {}", file);
                } catch (Exception e) {
                    logger.error("Failed to archive: {}", file, e);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (channel != null) channel.disconnect();
            if (session != null) session.disconnect();
        }
    }
}