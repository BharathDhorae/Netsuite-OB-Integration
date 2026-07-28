package com.promanatia.CamelDemo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Config {

	@Value("${aws.bucket.name}")
	private String bucketName;

	@Value("${aws.access.key}")
	private String accessKey;

	@Value("${aws.secret.key}")
	private String secretKey;

	@Value("${aws.region}")
	private String region;

	@Value("${s3.in.folder}")
	private String inFolder;

	@Value("${s3.out.folder}")
	private String outFolder;

	@Value("${s3.error.folder}")
	private String errorFolder;

	@Value("${s3.archive.folder}")
	private String archiveFolder;

	@Value("${s3.poll.delay}")
	private long pollDelay;

	public String getReadUri() {

		// amazonS3Client references the "s3Client" bean registered by
		// S3ClientConfig, so no credentials appear in this URI (and therefore
		// never get written to Camel's endpoint-URI log lines).
		return String.format("aws2-s3://%s" + "?amazonS3Client=#s3Client" + "&prefix=%s" + "&deleteAfterRead=false"
				+ "&includeBody=true" + "&delay=%d", bucketName, outFolder, pollDelay);
	}

	public String getWriteUri() {

		return String.format("aws2-s3://%s" + "?amazonS3Client=#s3Client", bucketName);
	}

	public String getErrorFolder() {
		return errorFolder;
	}

	public String getArchiveFolder() {
		return archiveFolder;
	}

	public String getInFolder() {
		return inFolder;
	}

	public String getOutFolder() {
		return outFolder;
	}

	public String getBucketName() {
		return bucketName;
	}
}