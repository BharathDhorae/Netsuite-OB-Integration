package com.promanatia.CamelDemo.service;

import com.promanatia.CamelDemo.DTO.FlowType;
import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class S3UploadService {

	@Value("${aws.bucket.name}")
	private String bucketName;

	@Value("${aws.access.key}")
	private String accessKey;

	@Value("${aws.secret.key}")
	private String secretKey;

	@Value("${aws.region}")
	private String region;

	public void uploadToS3(Exchange exchange) {

		String csvContent = exchange.getProperty("mappedCsv", String.class);
		FlowType flowType = exchange.getProperty("FLOW_TYPE", FlowType.class);

		if (csvContent == null || csvContent.isBlank()) {
			throw new RuntimeException("Mapped CSV is empty, cannot upload to S3");
		}

		String fileName = buildFileName(flowType);
		exchange.getIn().setBody(csvContent);
		exchange.getIn().setHeader("CamelAwsS3Key", fileName);
		exchange.getIn().setHeader("CamelAwsS3BucketName", bucketName);
		exchange.getIn().setHeader("CamelAwsS3ContentType", "text/csv");
		exchange.setProperty("S3_FILE_NAME", fileName);
	}

	private String buildFileName(FlowType flowType) {
		String timestamp = String.valueOf(System.currentTimeMillis());
		return "test/" + flowType.getOutputFileName() + "_" + timestamp + ".csv";
	}
}
