package com.promanatia.openbravonetsuiteintegration.service;

import com.promanatia.openbravonetsuiteintegration.dto.ApplicationLogDTO;
import com.promanatia.openbravonetsuiteintegration.repository.ApplicationLogRepository;

import org.springframework.stereotype.Service;

@Service
public class ApplicationLoggerService {

	private ApplicationLogRepository repository;

	public ApplicationLoggerService(ApplicationLogRepository repository) {
		this.repository = repository;
	}

	public void error(String flow, String flowType, String documentId, String message, String errorMessage) {
		repository.save(ApplicationLogDTO.builder().flow(flow).flowType(flowType).documentId(documentId)
				.logLevel("ERROR").message(message).errorMessage(errorMessage).build());
	}
}