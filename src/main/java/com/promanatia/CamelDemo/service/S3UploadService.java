package com.promanatia.CamelDemo.service;

import com.promanatia.CamelDemo.DTO.FlowType;
import com.promanatia.CamelDemo.config.S3Config;

import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class S3UploadService {

	@Value("${aws.bucket.name}")
	private String bucketName;
	
	private final S3Config s3Config;
	
	public S3UploadService(S3Config s3Config) {
		this.s3Config = s3Config;
	}


	public void uploadSuccessFile(Exchange exchange) {

		String csvContent = exchange.getProperty("mappedCsv", String.class);
		FlowType flowType = exchange.getProperty("FLOW_TYPE", FlowType.class);

		if (csvContent == null || csvContent.isBlank()) {
			throw new RuntimeException("Mapped CSV is empty, cannot upload to S3");
		}

		String fileName = buildFileName(flowType);
		exchange.getIn().setBody(csvContent);
		exchange.getIn().setHeader("CamelAwsS3Key", s3Config.getInFolder() + fileName);
		exchange.getIn().setHeader("CamelAwsS3BucketName", bucketName);
		exchange.getIn().setHeader("CamelAwsS3ContentType", "text/csv");
		exchange.setProperty("S3_FILE_NAME", fileName);
	}

	private String buildFileName(FlowType flowType) {
		String timestamp = String.valueOf(System.currentTimeMillis());
		return flowType.getOutputFileName() + "_" + timestamp + ".csv";
	}
	
	public void uploadErrorFile(Exchange exchange) {

		String errorCsv = exchange.getProperty("errorCsv", String.class);
		Boolean hasFailed = exchange.getProperty("hasFailedOrders", Boolean.class);
		if (hasFailed == null || !hasFailed || errorCsv == null) {
			return;
		}

		String fileName = exchange.getProperty("ERROR_FILE_NAME", String.class);
		exchange.getIn().setBody(errorCsv);
		exchange.getIn().setHeader("CamelAwsS3Key", s3Config.getErrorFolder() + fileName);
	}

}
