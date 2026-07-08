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

	@Value("${sftp.remote.archive}")
	private String archiveDirectory;

	public String getSftpUri() {
		return String.format(
				"sftp://%s:%d%s?username=%s&password=%s&include=%s&delay=%d&move=%s/${file:name}&moveFailed=%s/${file:name}&readLock=changed",
				host, port, remoteDirectory, username, password, include, delay, archiveDirectory, errorDirectory);
	}

	public String getErrorSftpUri() {
		return String.format("sftp://%s:%d%s?username=%s&password=%s&binary=true", host, port, errorDirectory, username,
				password);
	}

	public String getSftpSourceUri(String fileName) {
		return String.format("sftp://%s:%d%s/%s?username=%s&password=%s&binary=true", host, port, remoteDirectory,
				fileName, username, password);
	}

	public String getArchiveUri(String fileName) {
		return String.format("sftp://%s:%d%s/%s?username=%s&password=%s&binary=true", host, port, archiveDirectory,
				fileName, username, password);
	}

}