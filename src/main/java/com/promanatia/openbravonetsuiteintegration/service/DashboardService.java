package com.promanatia.openbravonetsuiteintegration.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.promanatia.openbravonetsuiteintegration.dto.ApplicationLogDTO;
import com.promanatia.openbravonetsuiteintegration.repository.ApplicationLogRepository;

@Service
public class DashboardService {

	private ApplicationLogRepository applicationLogRepository;

	public DashboardService(ApplicationLogRepository applicationLogRepository) {
		this.applicationLogRepository = applicationLogRepository;
	}

	public List<ApplicationLogDTO> getApplicationLogs(int page, int size) {

		return applicationLogRepository.findLogs(page, size);
	}
}
