package com.promanatia.openbravonetsuiteintegration.service;

import com.promanatia.openbravonetsuiteintegration.dto.ApplicationLogDTO;
import com.promanatia.openbravonetsuiteintegration.repository.ApplicationLogRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApplicationLoggerService {

	@Autowired
	private ApplicationLogRepository repository;

	public void error(String product, String flowType, String documentId, String message, String errorMessage) {
		repository.save(ApplicationLogDTO.builder().product(product).flowType(flowType).documentId(documentId)
				.logLevel("ERROR").message(message).errorMessage(errorMessage).build());
	}
}