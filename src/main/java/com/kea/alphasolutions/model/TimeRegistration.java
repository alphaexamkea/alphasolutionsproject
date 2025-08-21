package com.kea.alphasolutions.model;

import java.time.LocalDate;

public class TimeRegistration {
    private int timeId;
    private int taskId;
    private Integer resourceId;
    private LocalDate date;
    private double hours;

    public TimeRegistration() {}

    public TimeRegistration(int taskId, Integer resourceId, LocalDate date, double hours) {
        this.taskId = taskId;
        this.resourceId = resourceId;
        this.date = date;
        this.hours = hours;
    }

    // Getters & Setters
    public int getTimeId() {
        return timeId;
    }

    public void setTimeId(int timeId) {
        this.timeId = timeId;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public Integer getResourceId() {
        return resourceId;
    }

    public void setResourceId(Integer resourceId) {
        this.resourceId = resourceId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public double getHours() {
        return hours;
    }

    public void setHours(double hours) {
        this.hours = hours;
    }
}