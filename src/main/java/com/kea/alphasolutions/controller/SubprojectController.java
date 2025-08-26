package com.kea.alphasolutions.controller;

import com.kea.alphasolutions.model.Subproject;
import com.kea.alphasolutions.model.TimeRegistration;
import com.kea.alphasolutions.service.SubprojectService;
import com.kea.alphasolutions.service.TimeRegistrationService;
import com.kea.alphasolutions.service.TaskService;
import com.kea.alphasolutions.model.Task;
import com.kea.alphasolutions.util.AuthenticationUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SubprojectController {

    private final SubprojectService subprojectService;
    private final TaskService taskService;
    private final TimeRegistrationService timeRegistrationService;


    public SubprojectController(SubprojectService subprojectService, TaskService taskService, TimeRegistrationService timeRegistrationService) {
        this.subprojectService = subprojectService;
        this.taskService = taskService;
        this.timeRegistrationService = timeRegistrationService;
    }

    private void checkNotFound(Object entity, String message) {
        if (entity == null) {
            throw new RuntimeException(message);
        }
    }


    // Show form to create new subproject
    @GetMapping("/projects/{projectId}/subprojects/new")
    public String showCreateForm(HttpSession session, @PathVariable int projectId, Model model) {
        if (!AuthenticationUtil.isAuthenticated(session)) {
            return "redirect:/login";
        }
        Subproject subproject = new Subproject();
        subproject.setProjectId(projectId);
        model.addAttribute("subproject", subproject);
        return "subproject/form";
    }

    // Handle form submission (create)
    @PostMapping("/projects/{projectId}/subprojects")
    public String saveSubproject(HttpSession session, @PathVariable int projectId, @ModelAttribute Subproject subproject) {
        if (!AuthenticationUtil.isAuthenticated(session)) {
            return "redirect:/login";
        }
        subproject.setProjectId(projectId);
        subprojectService.addSubproject(subproject);
        return "redirect:/projects/" + projectId;
    }

    // Show subproject detail
    @GetMapping("/subprojects/{id}")
    public String showSubprojectDetail(HttpSession session, @PathVariable int id, Model model) {
        if (!AuthenticationUtil.isAuthenticated(session)) {
            return "redirect:/login";
        }
        Subproject subproject = subprojectService.getSubprojectById(id);
        checkNotFound(subproject, "Subproject not found with id: " + id);
        List<Task> tasks = taskService.getTasksBySubprojectId(id);

        double totalHours = timeRegistrationService.getTotalHoursBySubprojectId(id);
        double estimatedHours = taskService.getTotalEstimatedHoursBySubprojectId(id);

        Map<Integer, Double> taskHours = new HashMap<>();
        for (Task task : tasks) {
            double hours = timeRegistrationService.getTotalHoursByTaskId(task.getTaskId());
            taskHours.put(task.getTaskId(), hours);
        }

        model.addAttribute("subproject", subproject);
        model.addAttribute("tasks", tasks);
        model.addAttribute("totalHours", totalHours);
        model.addAttribute("estimatedHours", estimatedHours);
        model.addAttribute("taskHours", taskHours);

        return "subproject/detail";
    }

    // Show edit form
    @GetMapping("/subprojects/{id}/edit")
    public String showEditForm(HttpSession session, @PathVariable int id, Model model) {
        if (!AuthenticationUtil.isAuthenticated(session)) {
            return "redirect:/login";
        }
        Subproject subproject = subprojectService.getSubprojectById(id);
        checkNotFound(subproject, "Subproject not found with id: " + id);
        model.addAttribute("subproject", subproject);
        return "subproject/form";
    }

    // Handle edit form submission
    @PostMapping("/subprojects/{id}/update")
    public String updateSubproject(HttpSession session, @PathVariable int id, @ModelAttribute Subproject subproject) {
        if (!AuthenticationUtil.isAuthenticated(session)) {
            return "redirect:/login";
        }
        subproject.setSubprojectId(id);
        subprojectService.updateSubproject(subproject);
        return "redirect:/projects/" + subproject.getProjectId();
    }

    // Delete subproject
    @GetMapping("/subprojects/{id}/delete")
    public String deleteSubproject(HttpSession session, @PathVariable int id) {
        if (!AuthenticationUtil.isAuthenticated(session)) {
            return "redirect:/login";
        }
        Subproject subproject = subprojectService.getSubprojectById(id);
        if (subproject != null) {
            subprojectService.deleteSubproject(id);
            return "redirect:/projects/" + subproject.getProjectId();
        }
        throw new RuntimeException("Subproject not found with id: " + id);
    }
}
