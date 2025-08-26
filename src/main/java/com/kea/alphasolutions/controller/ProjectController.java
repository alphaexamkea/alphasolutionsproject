package com.kea.alphasolutions.controller;

import com.kea.alphasolutions.model.Project;
import com.kea.alphasolutions.model.Subproject;
import com.kea.alphasolutions.model.Task;
import com.kea.alphasolutions.service.ProjectService;
import com.kea.alphasolutions.service.SubprojectService;
import com.kea.alphasolutions.service.TimeRegistrationService;
import com.kea.alphasolutions.service.TaskService;
import com.kea.alphasolutions.util.AuthenticationUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private void checkNotFound(Object entity, String message) {
        if (entity == null) {
            throw new RuntimeException(message);
        }
    }

    // Show all projects
    @GetMapping("/projects")
    public String listProjects(HttpSession session, Model model) {
        if (!AuthenticationUtil.isAuthenticated(session)) {
            return "redirect:/login";
        }
        List<Project> projects = projectService.getAllProjects();
        Map<Integer, Integer> subprojectsCounts = new HashMap<>();
        Map<Integer, Integer> tasksCounts = new HashMap<>();
        Map<Integer, Integer> resourcesCounts = new HashMap<>();
        Map<Integer, Double> progressPercentages = new HashMap<>();

        for (Project project : projects) {
            int projectId = project.getProjectId();
            subprojectsCounts.put(projectId, projectService.getSubprojectsCount(projectId));
            tasksCounts.put(projectId, projectService.getTasksCount(projectId));
            resourcesCounts.put(projectId, projectService.getResourcesCount(projectId));
            progressPercentages.put(projectId, projectService.getProgressPercentage(projectId));
        }

        model.addAttribute("projects", projects);
        model.addAttribute("subprojectsCounts", subprojectsCounts);
        model.addAttribute("tasksCounts", tasksCounts);
        model.addAttribute("resourcesCounts", resourcesCounts);
        model.addAttribute("progressPercentages", progressPercentages);
        return "project/list";
    }

    // Show form to create new project
    @GetMapping("/projects/new")
    public String showCreateForm(HttpSession session, Model model) {
        if (!AuthenticationUtil.isAuthenticated(session)) {
            return "redirect:/login";
        }
        model.addAttribute("project", new Project());
        return "project/form";
    }

    // Handle form submission (create)
    @PostMapping("/projects")
    public String saveProject(HttpSession session, @ModelAttribute Project project) {
        if (!AuthenticationUtil.isAuthenticated(session)) {
            return "redirect:/login";
        }
        projectService.addProject(project);
        return "redirect:/projects";
    }

    @GetMapping("/projects/{id}")
    public String showProjectDetail(HttpSession session, @PathVariable int id, Model model) {
        if (!AuthenticationUtil.isAuthenticated(session)) {
            return "redirect:/login";
        }
        Project project = projectService.getProjectById(id);
        checkNotFound(project, "Project not found with id: " + id);

        List<Subproject> subprojects = subprojectService.getSubprojectsByProjectId(id);

        // Add time calculations using existing methods
        double totalHours = timeRegistrationService.getTotalHoursByProjectId(id);
        double estimatedHours = taskService.getTotalEstimatedHoursByProjectId(id);

        // Calculate metrics for each subproject
        Map<Integer, Integer> taskCounts = new HashMap<>();
        Map<Integer, Double> subprojectProgress = new HashMap<>();
        Map<Integer, Double> subprojectEstimatedHours = new HashMap<>();
        Map<Integer, Double> subprojectLoggedHours = new HashMap<>();

        for (Subproject subproject : subprojects) {
            int subprojectId = subproject.getSubprojectId();

            // Task count
            List<Task> tasks = taskService.getTasksBySubprojectId(subprojectId);
            taskCounts.put(subprojectId, tasks.size());

            // Hours calculations
            double subEstimated = taskService.getTotalEstimatedHoursBySubprojectId(subprojectId);
            double subLogged = timeRegistrationService.getTotalHoursBySubprojectId(subprojectId);

            subprojectEstimatedHours.put(subprojectId, subEstimated);
            subprojectLoggedHours.put(subprojectId, subLogged);

            // Progress calculation
            double progress = 0.0;
            if (subEstimated > 0) {
                progress = (subLogged / subEstimated) * 100;
                progress = progress > 100 ? 100 : progress;
            }
            subprojectProgress.put(subprojectId, progress);
        }

        // Project-level metrics
        int totalSubprojects = subprojects.size();
        int totalTasks = projectService.getTasksCount(id);
        int totalResources = projectService.getResourcesCount(id);
        double projectProgress = projectService.getProgressPercentage(id);

        model.addAttribute("project", project);
        model.addAttribute("subprojects", subprojects);
        model.addAttribute("totalHours", totalHours);
        model.addAttribute("estimatedHours", estimatedHours);
        model.addAttribute("taskCounts", taskCounts);
        model.addAttribute("subprojectProgress", subprojectProgress);
        model.addAttribute("subprojectEstimatedHours", subprojectEstimatedHours);
        model.addAttribute("subprojectLoggedHours", subprojectLoggedHours);
        model.addAttribute("totalSubprojects", totalSubprojects);
        model.addAttribute("totalTasks", totalTasks);
        model.addAttribute("totalResources", totalResources);
        model.addAttribute("projectProgress", projectProgress);

        return "project/detail";
    }

    // Show edit form
    @GetMapping("/projects/{id}/edit")
    public String showEditForm(HttpSession session, @PathVariable int id, Model model) {
        if (!AuthenticationUtil.isAuthenticated(session)) {
            return "redirect:/login";
        }
        Project project = projectService.getProjectById(id);
        checkNotFound(project, "Project not found with id: " + id);
        model.addAttribute("project", project);
        return "project/form"; // reuse form
    }

    // Handle edit form submission
    @PostMapping("/projects/{id}/update")
    public String updateProject(HttpSession session, @PathVariable int id, @ModelAttribute Project project) {
        if (!AuthenticationUtil.isAuthenticated(session)) {
            return "redirect:/login";
        }
        project.setProjectId(id);
        projectService.updateProject(project);
        return "redirect:/projects";
    }

    // Delete project
    @GetMapping("/projects/{id}/delete")
    public String deleteProject(HttpSession session, @PathVariable int id) {
        if (!AuthenticationUtil.isAuthenticated(session)) {
            return "redirect:/login";
        }
        projectService.deleteProject(id);
        return "redirect:/projects";
    }
}
