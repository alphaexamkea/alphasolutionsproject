package com.kea.alphasolutions.controller;

import com.kea.alphasolutions.model.TimeRegistration;
import com.kea.alphasolutions.model.Resource;
import com.kea.alphasolutions.service.TimeRegistrationService;
import com.kea.alphasolutions.service.ResourceService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class TimeRegistrationController {

    private final TimeRegistrationService timeRegistrationService;
    private final ResourceService resourceService;

    public TimeRegistrationController(TimeRegistrationService timeRegistrationService, ResourceService resourceService) {
        this.timeRegistrationService = timeRegistrationService;
        this.resourceService = resourceService;
    }

    // List time registrations for a task
    @GetMapping("/tasks/{taskId}/timeregistrations")
    public String listTimeRegistrations(@PathVariable int taskId, Model model) {
        List<TimeRegistration> timeRegistrations = timeRegistrationService.getTimeRegistrationsByTaskId(taskId);
        double totalHours = timeRegistrationService.getTotalHoursByTaskId(taskId);

        model.addAttribute("timeRegistrations", timeRegistrations);
        model.addAttribute("taskId", taskId);
        model.addAttribute("totalHours", totalHours);
        return "timeregistration/list";
    }

    // Show form to create new time registration
    @GetMapping("/tasks/{taskId}/timeregistrations/new")
    public String showCreateForm(@PathVariable int taskId, Model model) {
        TimeRegistration timeRegistration = new TimeRegistration();
        timeRegistration.setTaskId(taskId);

        List<Resource> resources = resourceService.getAllResources();

        model.addAttribute("timeRegistration", timeRegistration);
        model.addAttribute("resources", resources);
        return "timeregistration/form";
    }

    // Handle form submission (create)
    @PostMapping("/tasks/{taskId}/timeregistrations")
    public String saveTimeRegistration(@PathVariable int taskId, @ModelAttribute TimeRegistration timeRegistration) {
        timeRegistration.setTaskId(taskId);
        timeRegistrationService.addTimeRegistration(timeRegistration);
        return "redirect:/tasks/" + taskId + "/timeregistrations";
    }

    // Show time registration detail
    @GetMapping("/timeregistrations/{id}")
    public String showTimeRegistrationDetail(@PathVariable int id, Model model) {
        TimeRegistration timeRegistration = timeRegistrationService.getTimeRegistrationById(id);
        if (timeRegistration == null) {
            return "shared/error";
        }
        model.addAttribute("timeRegistration", timeRegistration);
        return "timeregistration/detail";
    }

    // Show edit form
    @GetMapping("/timeregistrations/{id}/edit")
    public String showEditForm(@PathVariable int id, Model model) {
        TimeRegistration timeRegistration = timeRegistrationService.getTimeRegistrationById(id);
        if (timeRegistration == null) {
            return "shared/error";
        }

        List<Resource> resources = resourceService.getAllResources();

        model.addAttribute("timeRegistration", timeRegistration);
        model.addAttribute("resources", resources);
        return "timeregistration/form";
    }

    // Handle edit form submission
    @PostMapping("/timeregistrations/{id}/update")
    public String updateTimeRegistration(@PathVariable int id, @ModelAttribute TimeRegistration timeRegistration) {
        timeRegistration.setTimeId(id);
        timeRegistrationService.updateTimeRegistration(timeRegistration);
        return "redirect:/tasks/" + timeRegistration.getTaskId() + "/timeregistrations";
    }

    // Delete time registration
    @GetMapping("/timeregistrations/{id}/delete")
    public String deleteTimeRegistration(@PathVariable int id) {
        TimeRegistration timeRegistration = timeRegistrationService.getTimeRegistrationById(id);
        if (timeRegistration != null) {
            timeRegistrationService.deleteTimeRegistration(id);
            return "redirect:/tasks/" + timeRegistration.getTaskId() + "/timeregistrations";
        }
        return "shared/error";
    }
}