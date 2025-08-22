package com.kea.alphasolutions.controller;

import com.kea.alphasolutions.model.Resource;
import com.kea.alphasolutions.model.TimeRegistration;
import com.kea.alphasolutions.service.ResourceService;
import com.kea.alphasolutions.service.TimeRegistrationService;
import com.kea.alphasolutions.service.TaskService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class ResourceController {

    private final ResourceService resourceService;
    private final TimeRegistrationService timeRegistrationService;
    private final TaskService taskService;

    public ResourceController(ResourceService resourceService, TimeRegistrationService timeRegistrationService, TaskService taskService) {
        this.resourceService = resourceService;
        this.timeRegistrationService = timeRegistrationService;
        this.taskService = taskService;
    }

    // List all resources
    @GetMapping("/resources")
    public String listResources(Model model) {
        List<Resource> resources = resourceService.getAllResources();
        model.addAttribute("resources", resources);
        return "resource/list";
    }

    // Show form to create new resource
    @GetMapping("/resources/new")
    public String showCreateForm(Model model) {
        model.addAttribute("resource", new Resource());
        return "resource/form";
    }

    // Handle form submission (create)
    @PostMapping("/resources")
    public String saveResource(@ModelAttribute Resource resource) {
        resourceService.addResource(resource);
        return "redirect:/resources";
    }

    // Show resource detail
    @GetMapping("/resources/{id}")
    public String showResourceDetail(@PathVariable int id, Model model) {
        Resource resource = resourceService.getResourceById(id);
        if (resource == null) {
            return "shared/error";
        }

        List<TimeRegistration> recentTimeEntries = timeRegistrationService.getRecentTimeEntriesByResourceId(id, 10);
        double totalHours = timeRegistrationService.getTotalHoursByResourceId(id);

        model.addAttribute("resource", resource);
        model.addAttribute("recentTimeEntries", recentTimeEntries);
        model.addAttribute("totalHours", totalHours);
        model.addAttribute("taskService", taskService);
        return "resource/detail";
    }

    // Show edit form
    @GetMapping("/resources/{id}/edit")
    public String showEditForm(@PathVariable int id, Model model) {
        Resource resource = resourceService.getResourceById(id);
        if (resource == null) {
            return "shared/error";
        }
        model.addAttribute("resource", resource);
        return "resource/form";
    }

    // Handle edit form submission
    @PostMapping("/resources/{id}/update")
    public String updateResource(@PathVariable int id, @ModelAttribute Resource resource) {
        resource.setResourceId(id);
        resourceService.updateResource(resource);
        return "redirect:/resources";
    }

    // Delete resource
    @GetMapping("/resources/{id}/delete")
    public String deleteResource(@PathVariable int id) {
        resourceService.deleteResource(id);
        return "redirect:/resources";
    }
}
