package com.promanatia.openbravonetsuiteintegration.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.promanatia.openbravonetsuiteintegration.dto.ApplicationLogDTO;
import com.promanatia.openbravonetsuiteintegration.service.DashboardService;

@Controller
public class DashboardController {

	private final DashboardService dashboardService;

	public DashboardController(DashboardService dashboardService) {
		this.dashboardService = dashboardService;
	}

	@GetMapping("/logs")
	public String getApplicationLogs(@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "10") int size, Model model) {

		List<ApplicationLogDTO> logs = dashboardService.getApplicationLogs(page, size);

		model.addAttribute("logs", logs);
		model.addAttribute("page", page);
		model.addAttribute("size", size);

		return "logs";
	}
}