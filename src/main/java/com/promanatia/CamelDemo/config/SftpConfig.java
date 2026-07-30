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

	@Value("${sftp.include}")
	private String include;

	@Value("${sftp.ob.netsuite.delay}")
	private long obToNetsuiteDelay;
	
	@Value("${sftp.netsuite.ob.delay}")
	private long netsuiteToOBDelay;

	@Value("${sftp.ob.netsuite.outdirectory}")
	private String outOBtoNetsuiteDirectory;

	@Value("${sftp.ob.netsuite.indirectory}")
	private String inOBtoNetsuiteDirectory;

	@Value("${sftp.ob.netsuite.errordirectory}")
	private String errorOBtoNetsuiteDirectory;

	@Value("${sftp.ob.netsuite.archivedirectory}")
	private String archiveOBtoNetsuiteDirectory;

	@Value("${sftp.netsuite.ob.outdirectory}")
	private String outNetsuiteToOBDirectory;

	@Value("${sftp.netsuite.ob.errordirectory}")
	private String errorNetsuiteToOBDirectory;

	@Value("${sftp.netsuite.ob.archivedirectory}")
	private String archiveNetsuiteToOBDirectory;

	@Value("${sftp.netsuite.ob.indirectory}")
	private String inNetsuiteToOBDirectory;

	public EndpointConsumerBuilder getObToNetsuiteSftpEndpoint() {
		return sftp(host + ":" + port + outOBtoNetsuiteDirectory).username(username).password(password).include(include)
				.delay(obToNetsuiteDelay).move(archiveOBtoNetsuiteDirectory + "/${file:name}")
				.moveFailed(errorOBtoNetsuiteDirectory + "/${file:name}").readLock("changed");
	}

	public EndpointConsumerBuilder getNetsuiteToObSftpEndpoint() {
		return sftp(host + ":" + port + outNetsuiteToOBDirectory).username(username).password(password).include(include)
				.delay(netsuiteToOBDelay).move(archiveNetsuiteToOBDirectory + "/${file:name}")
				.moveFailed(errorNetsuiteToOBDirectory + "/${file:name}").readLock("changed");
	}

	public EndpointProducerBuilder getObToNetsuiteErrorSftpEndpoint() {
		return sftp(host + ":" + port + errorOBtoNetsuiteDirectory).username(username).password(password).binary(true);
	}

	public EndpointProducerBuilder getNetsuiteToObErrorSftpEndpoint() {
		return sftp(host + ":" + port + errorNetsuiteToOBDirectory).username(username).password(password).binary(true);
	}

	public EndpointProducerBuilder getObToNetsuiteInSftpEndpoint() {
		return sftp(host + ":" + port + inOBtoNetsuiteDirectory).username(username).password(password).binary(true);
	}

	public EndpointProducerBuilder getNetsuiteToObInSftpEndpoint() {
		return sftp(host + ":" + port + inNetsuiteToOBDirectory).username(username).password(password).binary(true);
	}
}