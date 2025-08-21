package com.kea.alphasolutions.service;

import com.kea.alphasolutions.model.Project;
import com.kea.alphasolutions.repository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final TimeRegistrationService timeRegistrationService;
    private final TaskService taskService;

    public ProjectService(ProjectRepository projectRepository,
                          TimeRegistrationService timeRegistrationService,
                          TaskService taskService) {
        this.projectRepository = projectRepository;
        this.timeRegistrationService = timeRegistrationService;
        this.taskService = taskService;
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public Project getProjectById(int id) {
        return projectRepository.findById(id); // returns Project or null
    }

    public void addProject(Project project) {
        projectRepository.save(project);
    }

    public void updateProject(Project project) {
        projectRepository.update(project);
    }

    public void deleteProject(int id) {
        projectRepository.delete(id);
    }

    public int getSubprojectsCount(int projectId) {
        return projectRepository.countSubprojectsByProjectId(projectId);
    }

    public int getTasksCount(int projectId) {
        return projectRepository.countTasksByProjectId(projectId);
    }

    public int getResourcesCount(int projectId) {
        return projectRepository.countResourcesByProjectId(projectId);
    }

    public double getProgressPercentage(int projectId) {
        double totalHours = timeRegistrationService.getTotalHoursByProjectId(projectId);
        double estimatedHours = taskService.getTotalEstimatedHoursByProjectId(projectId);

        if (estimatedHours > 0) {
            double progress = (totalHours / estimatedHours) * 100;
            return progress > 100 ? 100 : progress;
        }
        return 0.0;
    }
}
