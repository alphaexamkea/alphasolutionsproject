package com.kea.alphasolutions.model;

import java.time.LocalDate;

public class Subproject {
    private int subprojectId;
    private int projectId; // FK → Project
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;

    // Getters & Setters
    public int getSubprojectId() {
        return subprojectId;
    }
    public void setSubprojectId(int subprojectId) {
        this.subprojectId = subprojectId;
    }

    public int getProjectId() {
        return projectId;
    }
    public void setProjectId(int projectId) {
        this.projectId = projectId;
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

    public LocalDate getStartDate() {
        return startDate;
    }
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
