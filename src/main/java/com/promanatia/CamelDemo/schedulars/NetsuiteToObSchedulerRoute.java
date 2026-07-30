package com.promanatia.CamelDemo.schedulars;

import com.promanatia.CamelDemo.DTO.FlowType;
import com.promanatia.CamelDemo.config.SftpConfig;
import com.promanatia.CamelDemo.repository.EntityMasterRepository;
import com.promanatia.CamelDemo.repository.FieldMappingRepository;
import com.promanatia.CamelDemo.service.*;
import com.promanatia.CamelDemo.utility.CsvAggregationStrategy;
import com.promanatia.CamelDemo.utility.CsvParser;
import com.promanatia.CamelDemo.utility.CsvValidator;

import org.apache.camel.builder.EndpointConsumerBuilder;
import org.apache.camel.builder.EndpointProducerBuilder;
import org.springframework.stereotype.Component;

/**
 * Dedicated scheduler for the NetSuite -> OpenBravo flow. Reads from the
 * NetSuite-to-OB SFTP folder, writes successfully mapped CSVs into the
 * OpenBravo "in" folder, and error CSVs into the NetSuite-to-OB error folder.
 *
 * All the actual parsing/validation/mapping logic lives in
 * {@link AbstractCsvSchedulerRoute} — this class only wires up direction
 * specific endpoints and identifiers.
 */
@Component
public class NetsuiteToObSchedulerRoute extends AbstractCsvSchedulerRoute {

	private final SftpConfig sftpConfig;

	public NetsuiteToObSchedulerRoute(SftpConfig sftpConfig, CsvAggregationStrategy csvAggregationStrategy,
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
		return sftpConfig.getNetsuiteToObSftpEndpoint();
	}

	@Override
	protected String getProcessingDirectUri() {
		return "direct:processCsvFile-netsuiteToOb";
	}

	@Override
	protected EndpointProducerBuilder getSuccessOutputSftpEndpoint() {
		return sftpConfig.getObToNetsuiteInSftpEndpoint();
	}

	@Override
	protected EndpointProducerBuilder getErrorOutputSftpEndpoint() {
		return sftpConfig.getNetsuiteToObErrorSftpEndpoint();
	}

	@Override
	protected String getSourceFlowType() {
		return FlowType.NETSUITE.toString();
	}

	@Override
	protected String getTargetFlowType() {
		return FlowType.OPENBRAVO.toString();
	}

	@Override
	protected String getDirectionLabel() {
		return "NETSUITE_TO_OB";
	}
}