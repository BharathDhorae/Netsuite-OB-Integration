
package com.promantia.openbravonetsuiteintegration.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.promantia.openbravonetsuiteintegration.dto.ApplicationLogDTO;
import com.promantia.openbravonetsuiteintegration.service.DashboardService;

@Controller
public class LoginController {

	private static final Logger log = LoggerFactory.getLogger(LoginController.class);

	private final DashboardService dashboardService;

	public LoginController(DashboardService dashboardService) {
		this.dashboardService = dashboardService;
	}

	@GetMapping("/login")
	public String loginPage() {
		return "redirect:/logs";
	}

	@GetMapping("/logs")
	public String getApplicationLogs(@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "10") int size, @RequestParam(required = false) String flow,
			@RequestParam(required = false) String flowType, @RequestParam(required = false) String logLevel,
			@RequestParam(required = false) String logDate, Model model, RedirectAttributes redirectAttributes) {

		try {

			// Validate rows per page
			if (size != 0 && size != 10 && size != 20 && size != 50 && size != 100) {
				size = 10;
			}

			if (page < 1) {
				page = 1;
			}

			List<ApplicationLogDTO> logs = dashboardService.getApplicationLogs(page, size, flow, flowType, logLevel,
					logDate);

			model.addAttribute("logs", logs);
			model.addAttribute("page", page);
			model.addAttribute("size", size);

			model.addAttribute("flow", flow);
			model.addAttribute("flowType", flowType);
			model.addAttribute("logLevel", logLevel);
			model.addAttribute("logDate", logDate);

			model.addAttribute("flowTypes", dashboardService.getFlowTypes());

			return "logs";

		} catch (Exception e) {

			log.error("Failed to load application logs", e);

			redirectAttributes.addFlashAttribute("serverError",
					"Server is currently unavailable. Please try again later.");

			return "redirect:/login";
		}
	}
}
