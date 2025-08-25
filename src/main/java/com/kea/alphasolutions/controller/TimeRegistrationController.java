package com.kea.alphasolutions.controller;

import com.kea.alphasolutions.model.TimeRegistration;
import com.kea.alphasolutions.model.Resource;
import com.kea.alphasolutions.service.TimeRegistrationService;
import com.kea.alphasolutions.service.ResourceService;
import com.kea.alphasolutions.util.AuthenticationUtil;
import jakarta.servlet.http.HttpSession;
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

    // Show form to create new time registration
    @GetMapping("/tasks/{taskId}/timeregistrations/new")
    public String showCreateForm(HttpSession session, @PathVariable int taskId, Model model) {
        if (!AuthenticationUtil.isAuthenticated(session)) {
            return "redirect:/login";
        }
        TimeRegistration timeRegistration = new TimeRegistration();
        timeRegistration.setTaskId(taskId);

        List<Resource> resources = resourceService.getAllResources();

        model.addAttribute("timeRegistration", timeRegistration);
        model.addAttribute("resources", resources);
        return "timeregistration/form";
    }

    // Handle form submission (create)
    @PostMapping("/tasks/{taskId}/timeregistrations")
    public String saveTimeRegistration(HttpSession session, @PathVariable int taskId, @ModelAttribute TimeRegistration timeRegistration) {
        if (!AuthenticationUtil.isAuthenticated(session)) {
            return "redirect:/login";
        }
        timeRegistration.setTaskId(taskId);
        timeRegistrationService.addTimeRegistration(timeRegistration);
        return "redirect:/tasks/" + taskId;
    }

    // Show edit form
    @GetMapping("/timeregistrations/{id}/edit")
    public String showEditForm(HttpSession session, @PathVariable int id, Model model) {
        if (!AuthenticationUtil.isAuthenticated(session)) {
            return "redirect:/login";
        }
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
    public String updateTimeRegistration(HttpSession session, @PathVariable int id, @ModelAttribute TimeRegistration timeRegistration) {
        if (!AuthenticationUtil.isAuthenticated(session)) {
            return "redirect:/login";
        }
        timeRegistration.setTimeId(id);
        timeRegistrationService.updateTimeRegistration(timeRegistration);
        return "redirect:/tasks/" + timeRegistration.getTaskId();
    }

    // Delete time registration
    @GetMapping("/timeregistrations/{id}/delete")
    public String deleteTimeRegistration(HttpSession session, @PathVariable int id) {
        if (!AuthenticationUtil.isAuthenticated(session)) {
            return "redirect:/login";
        }
        TimeRegistration timeRegistration = timeRegistrationService.getTimeRegistrationById(id);
        if (timeRegistration != null) {
            timeRegistrationService.deleteTimeRegistration(id);
            return "redirect:/tasks/" + timeRegistration.getTaskId();
        }
        return "shared/error";
    }
}