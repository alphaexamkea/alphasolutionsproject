package com.kea.alphasolutions.service;

import com.kea.alphasolutions.model.TimeRegistration;
import com.kea.alphasolutions.repository.TimeRegistrationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TimeRegistrationService {

    private final TimeRegistrationRepository timeRegistrationRepository;

    public TimeRegistrationService(TimeRegistrationRepository timeRegistrationRepository) {
        this.timeRegistrationRepository = timeRegistrationRepository;
    }

    public List<TimeRegistration> getTimeRegistrationsByTaskId(int taskId) {
        return timeRegistrationRepository.findAllByTaskId(taskId);
    }

    public List<TimeRegistration> getTimeRegistrationsByResourceId(int resourceId) {
        return timeRegistrationRepository.findAllByResourceId(resourceId);
    }

    public TimeRegistration getTimeRegistrationById(int id) {
        return timeRegistrationRepository.findById(id);
    }

    public void addTimeRegistration(TimeRegistration timeRegistration) {
        timeRegistrationRepository.save(timeRegistration);
    }

    public void updateTimeRegistration(TimeRegistration timeRegistration) {
        timeRegistrationRepository.update(timeRegistration);
    }

    public void deleteTimeRegistration(int id) {
        timeRegistrationRepository.delete(id);
    }

    public double getTotalHoursByTaskId(int taskId) {
        return timeRegistrationRepository.getTotalHoursByTaskId(taskId);
    }

    public double getTotalHoursBySubprojectId(int subprojectId) {
        return timeRegistrationRepository.getTotalHoursBySubprojectId(subprojectId);
    }

    public double getTotalHoursByProjectId(int projectId) {
        return timeRegistrationRepository.getTotalHoursByProjectId(projectId);
    }
}