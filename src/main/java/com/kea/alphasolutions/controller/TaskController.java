package com.kea.alphasolutions.controller;

import com.kea.alphasolutions.model.Resource;
import com.kea.alphasolutions.model.Task;
import com.kea.alphasolutions.model.TimeRegistration;
import com.kea.alphasolutions.service.ResourceService;
import com.kea.alphasolutions.service.TaskService;
import com.kea.alphasolutions.service.TimeRegistrationService;
import com.kea.alphasolutions.util.AuthenticationUtil;
import jakarta.servlet.http.HttpSession;
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

    private void checkNotFound(Object entity, String message) {
        if (entity == null) {
            throw new RuntimeException(message);
        }
    }

    // Show form to create new task
    @GetMapping("/subprojects/{subprojectId}/tasks/new")
    public String showCreateForm(HttpSession session, @PathVariable int subprojectId, Model model) {
        if (!AuthenticationUtil.isAuthenticated(session)) {
            return "redirect:/login";
        }
        Task task = new Task();
        task.setSubprojectId(subprojectId);
        task.setStatus("TO_DO");
        model.addAttribute("task", task);
        return "task/form";
    }

    // Handle form submission (create)
    @PostMapping("/subprojects/{subprojectId}/tasks")
    public String saveTask(HttpSession session, @PathVariable int subprojectId, @ModelAttribute Task task) {
        if (!AuthenticationUtil.isAuthenticated(session)) {
            return "redirect:/login";
        }
        task.setSubprojectId(subprojectId);
        taskService.addTask(task);
        return "redirect:/subprojects/" + subprojectId;
    }

    // Show task detail
    @GetMapping("/tasks/{id}")
    public String showTaskDetail(HttpSession session, @PathVariable int id, Model model) {
        if (!AuthenticationUtil.isAuthenticated(session)) {
            return "redirect:/login";
        }
        Task task = taskService.getTaskById(id);
        checkNotFound(task, "Task not found with id: " + id);

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
    public String showEditForm(HttpSession session, @PathVariable int id, Model model) {
        if (!AuthenticationUtil.isAuthenticated(session)) {
            return "redirect:/login";
        }
        Task task = taskService.getTaskById(id);
        checkNotFound(task, "Task not found with id: " + id);
        model.addAttribute("task", task);
        return "task/form";
    }

    // Handle edit form submission
    @PostMapping("/tasks/{id}/update")
    public String updateTask(HttpSession session, @PathVariable int id, @ModelAttribute Task task) {
        if (!AuthenticationUtil.isAuthenticated(session)) {
            return "redirect:/login";
        }
        Task existingTask = taskService.getTaskById(id);
        checkNotFound(existingTask, "Task not found with id: " + id);
        
        // If status changes to DONE, require admin rights
        if ("DONE".equals(task.getStatus()) && 
            !"DONE".equals(existingTask.getStatus())) {
            if (!AuthenticationUtil.isAdmin(session)) {
                // Keep existing status if not admin
                task.setStatus(existingTask.getStatus());
            }
        }
        
        task.setTaskId(id);
        taskService.updateTask(task);
        return "redirect:/subprojects/" + task.getSubprojectId();
    }

    // Delete task
    @GetMapping("/tasks/{id}/delete")
    public String deleteTask(HttpSession session, @PathVariable int id) {
        if (!AuthenticationUtil.isAuthenticated(session)) {
            return "redirect:/login";
        }
        Task task = taskService.getTaskById(id);
        checkNotFound(task, "Task not found with id: " + id);
        
        taskService.deleteTask(id);
        return "redirect:/subprojects/" + task.getSubprojectId();
    }
}
