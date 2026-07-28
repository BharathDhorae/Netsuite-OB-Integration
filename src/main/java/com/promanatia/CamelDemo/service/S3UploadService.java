package com.promanatia.CamelDemo.service;

import com.promanatia.CamelDemo.DTO.EntityMasterDTO;
import com.promanatia.CamelDemo.config.S3Config;
import com.promanatia.CamelDemo.utility.FileNameGenerator;

import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class S3UploadService {

	@Value("${aws.bucket.name}")
	private String bucketName;

	private final S3Config s3Config;
	private final FileNameGenerator fileNameGenerator;

	public S3UploadService(S3Config s3Config, FileNameGenerator fileNameGenerator) {
		this.s3Config = s3Config;
		this.fileNameGenerator = fileNameGenerator;
	}

	public void uploadSuccessFile(Exchange exchange) {

		String csvContent = exchange.getProperty("mappedCsv", String.class);
		EntityMasterDTO entity = exchange.getProperty("entity", EntityMasterDTO.class);

		if (csvContent == null || csvContent.isBlank()) {
			throw new RuntimeException("Mapped CSV is empty, cannot upload to S3");
		}

		String fileName = fileNameGenerator.buildSuccessFileName(entity);
		prepareS3Request(exchange, csvContent);
		exchange.getIn().setHeader("CamelAwsS3Key", s3Config.getInFolder() + fileName);
		exchange.setProperty("S3_FILE_NAME", fileName);
	}

	public void uploadErrorFile(Exchange exchange) {

		String errorCsv = exchange.getProperty("errorCsv", String.class);
		Boolean hasFailed = exchange.getProperty("hasFailedOrders", Boolean.class);
		EntityMasterDTO entity = exchange.getProperty("entity", EntityMasterDTO.class);
		if (hasFailed == null || !hasFailed || errorCsv == null || errorCsv.isBlank()) {
			return;
		}

		String fileName = exchange.getProperty("ERROR_FILE_NAME", String.class);

		if (fileName == null) {
			fileName = fileNameGenerator.buildErrorFileName(entity);
		}
		prepareS3Request(exchange, errorCsv);
		exchange.getIn().setHeader("CamelAwsS3Key", s3Config.getErrorFolder() + fileName);
	}

	private void prepareS3Request(Exchange exchange, String body) {
		exchange.getIn().removeHeader("Content-Length");
		exchange.getIn().removeHeader("CamelAwsS3ContentLength");
		exchange.getIn().setBody(body);
		exchange.getIn().setHeader("CamelAwsS3BucketName", bucketName);
		exchange.getIn().setHeader("CamelAwsS3ContentType", "text/csv");
	}

}