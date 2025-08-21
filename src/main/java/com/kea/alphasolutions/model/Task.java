package com.kea.alphasolutions.model;

import java.time.LocalDate;

public class Task {
    private int taskId;
    private int subprojectId;
    private String name;
    private String description;
    private double estimatedHours;
    private LocalDate deadline;
    private String status;

    // Getters & Setters
    public int getTaskId() {
        return taskId;
    }
    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public int getSubprojectId() {
        return subprojectId;
    }
    public void setSubprojectId(int subprojectId) {
        this.subprojectId = subprojectId;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public double getEstimatedHours() {
        return estimatedHours;
    }
    public void setEstimatedHours(double estimatedHours) {
        this.estimatedHours = estimatedHours;
    }

    public LocalDate getDeadline() {
        return deadline;
    }
    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
}
