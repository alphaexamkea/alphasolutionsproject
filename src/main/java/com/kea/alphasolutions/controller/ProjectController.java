package com.kea.alphasolutions.controller;

import com.kea.alphasolutions.model.Project;
import com.kea.alphasolutions.model.Subproject;
import com.kea.alphasolutions.service.ProjectService;
import com.kea.alphasolutions.service.SubprojectService;
import com.kea.alphasolutions.service.TimeRegistrationService;
import com.kea.alphasolutions.service.TaskService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class ProjectController {

    private final ProjectService projectService;
    private final SubprojectService subprojectService;
    private final TimeRegistrationService timeRegistrationService;
    private final TaskService taskService;

    public ProjectController(ProjectService projectService, SubprojectService subprojectService,
                             TimeRegistrationService timeRegistrationService, TaskService taskService) {
        this.projectService = projectService;
        this.subprojectService = subprojectService;
        this.timeRegistrationService = timeRegistrationService;
        this.taskService = taskService;
    }

    // Show all projects
    @GetMapping("/projects")
    public String listProjects(Model model) {
        List<Project> projects = projectService.getAllProjects();
        model.addAttribute("projects", projects);
        return "project/list";
    }

    // Show form to create new project
    @GetMapping("/projects/new")
    public String showCreateForm(Model model) {
        model.addAttribute("project", new Project());
        return "project/form";
    }

    // Handle form submission (create)
    @PostMapping("/projects")
    public String saveProject(@ModelAttribute Project project) {
        projectService.addProject(project);
        return "redirect:/projects";
    }

    @GetMapping("/projects/{id}")
    public String showProjectDetail(@PathVariable int id, Model model) {
        Project project = projectService.getProjectById(id);
        if (project == null) {
            return "shared/error";
        }

        List<Subproject> subprojects = subprojectService.getSubprojectsByProjectId(id);

        // Add time calculations using existing methods
        double totalHours = timeRegistrationService.getTotalHoursByProjectId(id);
        double estimatedHours = taskService.getTotalEstimatedHoursByProjectId(id);

        model.addAttribute("project", project);
        model.addAttribute("subprojects", subprojects);
        model.addAttribute("totalHours", totalHours);
        model.addAttribute("estimatedHours", estimatedHours);

        return "project/detail";
    }

    // Show edit form
    @GetMapping("/projects/{id}/edit")
    public String showEditForm(@PathVariable int id, Model model) {
        Project project = projectService.getProjectById(id);
        if (project == null) {
            return "shared/error";
        }
        model.addAttribute("project", project);
        return "project/form"; // reuse form
    }

    // Handle edit form submission
    @PostMapping("/projects/{id}/update")
    public String updateProject(@PathVariable int id, @ModelAttribute Project project) {
        project.setProjectId(id);
        projectService.updateProject(project);
        return "redirect:/projects";
    }

    // Delete project
    @GetMapping("/projects/{id}/delete")
    public String deleteProject(@PathVariable int id) {
        projectService.deleteProject(id);
        return "redirect:/projects";
    }
}
