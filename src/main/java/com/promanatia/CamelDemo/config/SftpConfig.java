package com.promanatia.CamelDemo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
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

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRemoteDirectory() {
        return remoteDirectory;
    }

    public String getInclude() {
        return include;
    }

    public boolean isDelete() {
        return delete;
    }

    public long getDelay() {
        return delay;
    }

    public String getErrorDirectory() {
        return errorDirectory;
    }

}