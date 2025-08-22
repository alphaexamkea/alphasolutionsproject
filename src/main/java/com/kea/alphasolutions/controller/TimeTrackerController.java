package com.kea.alphasolutions.controller;

import com.kea.alphasolutions.model.Resource;
import com.kea.alphasolutions.service.ResourceService;
import com.kea.alphasolutions.service.TimeRegistrationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class TimeTrackerController {

    private final ResourceService resourceService;
    private final TimeRegistrationService timeRegistrationService;

    public TimeTrackerController(ResourceService resourceService, TimeRegistrationService timeRegistrationService) {
        this.resourceService = resourceService;
        this.timeRegistrationService = timeRegistrationService;
    }

    @GetMapping("/time-tracker")
    public String showTimeTrackerOverview(Model model) {
        List<Resource> resources = resourceService.getAllResources();
        model.addAttribute("resources", resources);
        model.addAttribute("timeRegistrationService", timeRegistrationService);
        return "timetracker/overview";
    }
}