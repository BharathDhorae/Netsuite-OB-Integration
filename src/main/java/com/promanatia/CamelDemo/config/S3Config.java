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

	@Value("${s3.out.folder}")
	private String outFolder;

	@Value("${s3.error.folder}")
	private String errorFolder;

	@Value("${s3.archive.folder}")
	private String archiveFolder;

	@Value("${s3.poll.delay}")
	private long pollDelay;

	public String getReadUri() {

		return String.format(
				"aws2-s3://%s" + "?accessKey=RAW(%s)" + "&secretKey=RAW(%s)" + "&region=%s" + "&prefix=%s"
						+ "&deleteAfterRead=false" + "&includeBody=true" + "&delay=%d",
				bucketName, accessKey, secretKey, region, outFolder, pollDelay);
	}

	public String getWriteUri() {

		return String.format("aws2-s3://%s" + "?accessKey=RAW(%s)" + "&secretKey=RAW(%s)" + "&region=%s", bucketName,
				accessKey, secretKey, region);
	}

	public String getErrorFolder() {
		return errorFolder;
	}

	public String getArchiveFolder() {
		return archiveFolder;
	}
}