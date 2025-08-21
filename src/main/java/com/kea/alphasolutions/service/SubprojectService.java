package com.kea.alphasolutions.service;

import com.kea.alphasolutions.model.Subproject;
import com.kea.alphasolutions.repository.SubprojectRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubprojectService {

    private final SubprojectRepository subprojectRepository;

    public SubprojectService(SubprojectRepository subprojectRepository) {
        this.subprojectRepository = subprojectRepository;
    }

    public List<Subproject> getSubprojectsByProjectId(int projectId) {
        return subprojectRepository.findAllByProjectId(projectId);
    }

    public Subproject getSubprojectById(int id) {
        return subprojectRepository.findById(id);
    }

    public void addSubproject(Subproject subproject) {
        subprojectRepository.save(subproject);
    }

    public void updateSubproject(Subproject subproject) {
        subprojectRepository.update(subproject);
    }

    public void deleteSubproject(int id) {
        subprojectRepository.delete(id);
    }
}
