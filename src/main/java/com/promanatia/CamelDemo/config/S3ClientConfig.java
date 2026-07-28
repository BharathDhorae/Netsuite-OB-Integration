package com.promanatia.CamelDemo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

/**
 * Builds a real S3Client and registers it in the Spring context so the Camel
 * aws2-s3 endpoint URI can reference it by bean name instead of having the
 * access key / secret key embedded directly in the URI string. Camel logs
 * endpoint URIs (route start-up, toD dynamic URIs, DEBUG logging), so
 * credentials baked into the URI - even wrapped in RAW() - can end up in log
 * files. Referencing a registry bean avoids that entirely.
 */
@Configuration
public class S3ClientConfig {

	@Value("${aws.access.key}")
	private String accessKey;

	@Value("${aws.secret.key}")
	private String secretKey;

	@Value("${aws.region}")
	private String region;

	@Bean(name = "s3Client")
	public S3Client s3Client() {
		return S3Client.builder()
				.credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
				.region(Region.of(region)).build();
	}
}