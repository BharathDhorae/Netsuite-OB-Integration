package com.promanatia.CamelDemo.service;

import com.promanatia.CamelDemo.DTO.ApplicationLogDTO;
import com.promanatia.CamelDemo.repository.ApplicationLogRepository;
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