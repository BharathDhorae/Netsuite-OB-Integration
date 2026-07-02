package com.promanatia.CamelDemo.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class SftpConfig {

    @Value("${sftp.host}")
    private String host;

    @Value("${sftp.port}")
    private int port;

    @Value("${sftp.username}")
    private String username;

    @Value("${sftp.password}")
    private String password;

    @Value("${sftp.remote.directory}")
    private String remoteDirectory;

    @Value("${sftp.delete}")
    private boolean delete;

    @Value("${sftp.include}")
    private String include;

    @Value("${sftp.delay}")
    private long delay;

    @Value("${sftp.remote.error-directory}")
    private String errorDirectory;

    @Value("${sftp.remote.archive}")
    private String archiveDirectory;

}