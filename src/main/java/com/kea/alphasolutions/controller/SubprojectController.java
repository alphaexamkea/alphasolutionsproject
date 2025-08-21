package com.kea.alphasolutions.controller;

import com.kea.alphasolutions.model.Subproject;
import com.kea.alphasolutions.model.TimeRegistration;
import com.kea.alphasolutions.service.SubprojectService;
import com.kea.alphasolutions.service.TimeRegistrationService;
import com.kea.alphasolutions.service.TaskService;
import com.kea.alphasolutions.model.Task;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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


    // List subprojects for a project
    @GetMapping("/projects/{projectId}/subprojects")
    public String listSubprojects(@PathVariable int projectId, Model model) {
        List<Subproject> subprojects = subprojectService.getSubprojectsByProjectId(projectId);
        model.addAttribute("subprojects", subprojects);
        model.addAttribute("projectId", projectId);
        return "subproject/list";
    }

    // Show form to create new subproject
    @GetMapping("/projects/{projectId}/subprojects/new")
    public String showCreateForm(@PathVariable int projectId, Model model) {
        Subproject subproject = new Subproject();
        subproject.setProjectId(projectId);
        model.addAttribute("subproject", subproject);
        return "subproject/form";
    }

    // Handle form submission (create)
    @PostMapping("/projects/{projectId}/subprojects")
    public String saveSubproject(@PathVariable int projectId, @ModelAttribute Subproject subproject) {
        subproject.setProjectId(projectId);
        subprojectService.addSubproject(subproject);
        return "redirect:/projects/" + projectId + "/subprojects";
    }

    // Show subproject detail
    @GetMapping("/subprojects/{id}")
    public String showSubprojectDetail(@PathVariable int id, Model model) {
        Subproject subproject = subprojectService.getSubprojectById(id);
        if (subproject == null) {
            return "shared/error";
        }
        List<Task> tasks = taskService.getTasksBySubprojectId(id);

        double totalHours = timeRegistrationService.getTotalHoursBySubprojectId(id);
        double estimatedHours = taskService.getTotalEstimatedHoursBySubprojectId(id);

        model.addAttribute("subproject", subproject);
        model.addAttribute("tasks", tasks);
        model.addAttribute("totalHours", totalHours);
        model.addAttribute("estimatedHours", estimatedHours);

        return "subproject/detail";
    }

    // Show edit form
    @GetMapping("/subprojects/{id}/edit")
    public String showEditForm(@PathVariable int id, Model model) {
        Subproject subproject = subprojectService.getSubprojectById(id);
        if (subproject == null) {
            return "shared/error";
        }
        model.addAttribute("subproject", subproject);
        return "subproject/form";
    }

    // Handle edit form submission
    @PostMapping("/subprojects/{id}/update")
    public String updateSubproject(@PathVariable int id, @ModelAttribute Subproject subproject) {
        subproject.setSubprojectId(id);
        subprojectService.updateSubproject(subproject);
        return "redirect:/projects/" + subproject.getProjectId() + "/subprojects";
    }

    // Delete subproject
    @GetMapping("/subprojects/{id}/delete")
    public String deleteSubproject(@PathVariable int id) {
        Subproject subproject = subprojectService.getSubprojectById(id);
        if (subproject != null) {
            subprojectService.deleteSubproject(id);
            return "redirect:/projects/" + subproject.getProjectId() + "/subprojects";
        }
        return "shared/error";
    }
}
