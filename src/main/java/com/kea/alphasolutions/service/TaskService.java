package com.kea.alphasolutions.service;

import com.kea.alphasolutions.model.Task;
import com.kea.alphasolutions.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
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
}
