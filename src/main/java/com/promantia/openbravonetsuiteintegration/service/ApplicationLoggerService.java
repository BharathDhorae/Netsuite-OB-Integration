package com.promantia.openbravonetsuiteintegration.service;

import com.promantia.openbravonetsuiteintegration.dto.ApplicationLogDTO;
import com.promantia.openbravonetsuiteintegration.repository.ApplicationLogRepository;

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