package com.promanatia.CamelDemo.config;

import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.sftp;

import org.apache.camel.builder.EndpointConsumerBuilder;
import org.apache.camel.builder.EndpointProducerBuilder;
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

	@Value("${sftp.remote.incoming-directory}")
	private String inDirectory;

	public EndpointConsumerBuilder getSftpEndpoint() {
		return sftp(host + ":" + port + remoteDirectory).username(username).password(password).include(include)
				.delay(delay).move(archiveDirectory + "/${file:name}").moveFailed(errorDirectory + "/${file:name}")
				.readLock("changed");
	}

	public EndpointProducerBuilder getErrorSftpEndpoint() {
		return sftp(host + ":" + port + errorDirectory).username(username).password(password).binary(true);
	}

	public EndpointProducerBuilder getInSftpEndpoint() {
		return sftp(host + ":" + port + inDirectory).username(username).password(password).binary(true);
	}

}