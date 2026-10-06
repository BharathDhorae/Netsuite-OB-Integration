package com.promanatia.openbravonetsuiteintegration.schedulars;

import com.promanatia.openbravonetsuiteintegration.config.SftpConfig;
import com.promanatia.openbravonetsuiteintegration.dto.FlowType;
import com.promanatia.openbravonetsuiteintegration.repository.EntityMasterRepository;
import com.promanatia.openbravonetsuiteintegration.repository.FieldMappingRepository;
import com.promanatia.openbravonetsuiteintegration.service.*;
import com.promanatia.openbravonetsuiteintegration.utility.CsvAggregationStrategy;
import com.promanatia.openbravonetsuiteintegration.utility.CsvParser;
import com.promanatia.openbravonetsuiteintegration.utility.CsvValidator;

import org.apache.camel.builder.EndpointConsumerBuilder;
import org.apache.camel.builder.EndpointProducerBuilder;
import org.springframework.stereotype.Component;

/**
 * Dedicated scheduler for the OpenBravo -> NetSuite flow. Reads from the
 * OB-to-NetSuite SFTP folder, writes successfully mapped CSVs into the NetSuite
 * "in" folder, and error CSVs into the OB-to-NetSuite error folder.
 *
 * All the actual parsing/validation/mapping logic lives in
 * {@link AbstractCsvSchedulerRoute} — this class only wires up direction
 * specific endpoints and identifiers.
 */
@Component
public class ObToNetsuiteSchedulerRoute extends AbstractCsvSchedulerRoute {

	private final SftpConfig sftpConfig;

	public ObToNetsuiteSchedulerRoute(SftpConfig sftpConfig, CsvAggregationStrategy csvAggregationStrategy,
			CsvValidator csvValidatorService, CsvMappingService csvMappingService, ErrorCsvService errorCsvService,
			ApplicationLoggerService loggerService, SftpUploadService sftpUploadService, CsvParser csvParser,
			FieldMappingRepository fieldMappingRepository, EntityMasterRepository entityMasterRepository,
			LookupService lookupService) {

		super(csvAggregationStrategy, csvValidatorService, csvMappingService, errorCsvService, loggerService,
				sftpUploadService, csvParser, fieldMappingRepository, entityMasterRepository, lookupService);
		this.sftpConfig = sftpConfig;
	}

	@Override
	protected EndpointConsumerBuilder getSourceSftpEndpoint() {
		return sftpConfig.getObToNetsuiteSftpEndpoint();
	}

	@Override
	protected String getProcessingDirectUri() {
		return "direct:processCsvFile-obToNetsuite";
	}

	@Override
	protected EndpointProducerBuilder getSuccessOutputSftpEndpoint() {
		return sftpConfig.getNetsuiteToObInSftpEndpoint();
	}

	@Override
	protected EndpointProducerBuilder getErrorOutputSftpEndpoint() {
		return sftpConfig.getObToNetsuiteErrorSftpEndpoint();
	}

	@Override
	protected String getSourceFlowType() {
		return FlowType.OPENBRAVO.toString();
	}

	@Override
	protected String getTargetFlowType() {
		return FlowType.NETSUITE.toString();
	}

	@Override
	protected String getDirectionLabel() {
		return "OB_TO_NETSUITE";
	}
}