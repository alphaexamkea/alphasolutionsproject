package com.kea.alphasolutions.controller;

import com.kea.alphasolutions.model.Task;
import com.kea.alphasolutions.service.TaskService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    // List tasks for a subproject
    @GetMapping("/subprojects/{subprojectId}/tasks")
    public String listTasks(@PathVariable int subprojectId, Model model) {
        List<Task> tasks = taskService.getTasksBySubprojectId(subprojectId);
        model.addAttribute("tasks", tasks);
        model.addAttribute("subprojectId", subprojectId);
        return "task/list";
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
        return "redirect:/subprojects/" + subprojectId + "/tasks";
    }

    // Show task detail
    @GetMapping("/tasks/{id}")
    public String showTaskDetail(@PathVariable int id, Model model) {
        Task task = taskService.getTaskById(id);
        if (task == null) {
            return "shared/error";
        }
        model.addAttribute("task", task);
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
        return "redirect:/subprojects/" + task.getSubprojectId() + "/tasks";
    }

    // Delete task
    @GetMapping("/tasks/{id}/delete")
    public String deleteTask(@PathVariable int id) {
        Task task = taskService.getTaskById(id);
        if (task != null) {
            taskService.deleteTask(id);
            return "redirect:/subprojects/" + task.getSubprojectId() + "/tasks";
        }
        return "shared/error";
    }
}
