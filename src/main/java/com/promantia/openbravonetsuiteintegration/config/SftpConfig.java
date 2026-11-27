
package com.promantia.openbravonetsuiteintegration.config;

import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.sftp;

import org.apache.camel.builder.EndpointConsumerBuilder;
import org.apache.camel.builder.EndpointProducerBuilder;
import org.apache.camel.builder.endpoint.dsl.SftpEndpointBuilderFactory.SftpEndpointBuilder;
import org.apache.camel.component.file.GenericFileExist;
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

	@Value("${sftp.known-hosts-file}")
	private String knownHostsPath;

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

	@Value("${error-notification.poll-delay:60000}")
	private long errorNotificationDelay;

	// Common SFTP configuration with SSH host-key verification
	private SftpEndpointBuilder base(String directory) {
		return sftp(host + ":" + port + directory).username(username).password(password).strictHostKeyChecking("yes")
				.knownHostsFile(knownHostsPath);
	}

	// OB to NetSuite consumer
	public EndpointConsumerBuilder getObToNetsuiteSftpEndpoint() {
		return base(outOBtoNetsuiteDirectory).include(include).delay(obToNetsuiteDelay)
				.move(archiveOBtoNetsuiteDirectory + "/${file:name}")
				.moveFailed(errorOBtoNetsuiteDirectory + "/${file:name}").readLock("changed");
	}

	// NetSuite to OB consumer
	public EndpointConsumerBuilder getNetsuiteToObSftpEndpoint() {
		return base(outNetsuiteToOBDirectory).include(include).delay(netsuiteToOBDelay)
				.move(archiveNetsuiteToOBDirectory + "/${file:name}")
				.moveFailed(errorNetsuiteToOBDirectory + "/${file:name}").readLock("changed");
	}

	// OB error notification consumer
	public EndpointConsumerBuilder getObToNetsuiteErrorNotificationEndpoint() {
		return base(errorOBtoNetsuiteDirectory).include("(?i).*\\.csv").delay(errorNotificationDelay)
				.readLock("changed");
	}

	// NetSuite error notification consumer
	public EndpointConsumerBuilder getNetsuiteToObErrorNotificationEndpoint() {
		return base(errorNetsuiteToOBDirectory).include("(?i).*\\.csv").delay(errorNotificationDelay)
				.readLock("changed");
	}

	// OB error producer
	public EndpointProducerBuilder getObToNetsuiteErrorSftpEndpoint() {
		return base(errorOBtoNetsuiteDirectory).binary(true).fileExist(GenericFileExist.Fail);
	}

	// NetSuite error producer
	public EndpointProducerBuilder getNetsuiteToObErrorSftpEndpoint() {
		return base(errorNetsuiteToOBDirectory).binary(true).fileExist(GenericFileExist.Fail);
	}

	// OB input producer
	public EndpointProducerBuilder getObToNetsuiteInSftpEndpoint() {
		return base(inOBtoNetsuiteDirectory).binary(true).fileExist(GenericFileExist.Fail);
	}

	// NetSuite input producer
	public EndpointProducerBuilder getNetsuiteToObInSftpEndpoint() {
		return base(inNetsuiteToOBDirectory).binary(true).fileExist(GenericFileExist.Fail);
	}

}
