package com.promanatia.CamelDemo.controller;

import com.promanatia.CamelDemo.service.DeadLetterService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DeadLetterViewController {

    private final DeadLetterService deadLetterService;

    public DeadLetterViewController(
            DeadLetterService deadLetterService) {

        this.deadLetterService = deadLetterService;
    }

    @GetMapping("/logs")
    public String showLogs(Model model) {

        model.addAttribute(
                "records",
                deadLetterService.getFailedRecords()
        );

        return "dead-letter";
    }
}