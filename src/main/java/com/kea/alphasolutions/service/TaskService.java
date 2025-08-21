package com.kea.alphasolutions.service;

import com.kea.alphasolutions.model.Subproject;
import com.kea.alphasolutions.model.Task;
import com.kea.alphasolutions.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final SubprojectService subprojectService;

    public TaskService(TaskRepository taskRepository, SubprojectService subprojectService) {
        this.taskRepository = taskRepository;
        this.subprojectService = subprojectService;
    }

    public List<Task> getTasksBySubprojectId(int subprojectId) {
        return taskRepository.findAllBySubprojectId(subprojectId);
    }

    public Task getTaskById(int id) {
        return taskRepository.findById(id);
    }

    public void addTask(Task task) {
        taskRepository.save(task);
    }

    public void updateTask(Task task) {
        taskRepository.update(task);
    }

    public void deleteTask(int id) {
        taskRepository.delete(id);
    }
    public double getTotalEstimatedHoursBySubprojectId(int subprojectId) {
        List<Task> tasks = taskRepository.findAllBySubprojectId(subprojectId);
        double total = 0.0;
        for (Task task : tasks) {
            total += task.getEstimatedHours();
        }
        return total;
    }

    public double getTotalEstimatedHoursByProjectId(int projectId) {
        // Get all subprojects for this project
        List<Subproject> subprojects = subprojectService.getSubprojectsByProjectId(projectId);
        double total = 0.0;
        for (Subproject subproject : subprojects) {
            total += getTotalEstimatedHoursBySubprojectId(subproject.getSubprojectId());
        }
        return total;
    }
}
