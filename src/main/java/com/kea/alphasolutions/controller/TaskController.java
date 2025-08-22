package com.kea.alphasolutions.controller;

import com.kea.alphasolutions.model.Resource;
import com.kea.alphasolutions.model.Task;
import com.kea.alphasolutions.model.TimeRegistration;
import com.kea.alphasolutions.service.ResourceService;
import com.kea.alphasolutions.service.TaskService;
import com.kea.alphasolutions.service.TimeRegistrationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class TaskController {

    private final TaskService taskService;
    private final TimeRegistrationService timeRegistrationService;
    private final ResourceService resourceService;

    public TaskController(TaskService taskService, TimeRegistrationService timeRegistrationService, ResourceService resourceService) {
        this.taskService = taskService;
        this.timeRegistrationService = timeRegistrationService;
        this.resourceService = resourceService;
    }

    // Show form to create new task
    @GetMapping("/subprojects/{subprojectId}/tasks/new")
    public String showCreateForm(@PathVariable int subprojectId, Model model) {
        Task task = new Task();
        task.setSubprojectId(subprojectId);
        task.setStatus("TO_DO");
        model.addAttribute("task", task);
        return "task/form";
    }

    // Handle form submission (create)
    @PostMapping("/subprojects/{subprojectId}/tasks")
    public String saveTask(@PathVariable int subprojectId, @ModelAttribute Task task) {
        task.setSubprojectId(subprojectId);
        taskService.addTask(task);
        return "redirect:/subprojects/" + subprojectId;
    }

    // Show task detail
    @GetMapping("/tasks/{id}")
    public String showTaskDetail(@PathVariable int id, Model model) {
        Task task = taskService.getTaskById(id);
        if (task == null) {
            return "shared/error";
        }

        double totalHours = timeRegistrationService.getTotalHoursByTaskId(id);
        List<TimeRegistration> timeRegistrations = timeRegistrationService.getTimeRegistrationsByTaskId(id);

        // Create resource name mapping
        Map<Integer, String> resourceNames = new HashMap<>();
        for (TimeRegistration tr : timeRegistrations) {
            if (tr.getResourceId() != null) {
                Resource resource = resourceService.getResourceById(tr.getResourceId());
                if (resource != null) {
                    resourceNames.put(tr.getResourceId(), resource.getName());
                }
            }
        }

        model.addAttribute("task", task);
        model.addAttribute("totalHours", totalHours);
        model.addAttribute("timeRegistrations", timeRegistrations);
        model.addAttribute("resourceNames", resourceNames);
        return "task/detail";
    }

    // Show edit form
    @GetMapping("/tasks/{id}/edit")
    public String showEditForm(@PathVariable int id, Model model) {
        Task task = taskService.getTaskById(id);
        if (task == null) {
            return "shared/error";
        }
        model.addAttribute("task", task);
        return "task/form";
    }

    // Handle edit form submission
    @PostMapping("/tasks/{id}/update")
    public String updateTask(@PathVariable int id, @ModelAttribute Task task) {
        task.setTaskId(id);
        taskService.updateTask(task);
        return "redirect:/subprojects/" + task.getSubprojectId();
    }

    // Delete task
    @GetMapping("/tasks/{id}/delete")
    public String deleteTask(@PathVariable int id) {
        Task task = taskService.getTaskById(id);
        if (task != null) {
            taskService.deleteTask(id);
            return "redirect:/subprojects/" + task.getSubprojectId();
        }
        return "shared/error";
    }
}
