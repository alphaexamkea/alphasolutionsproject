package com.kea.alphasolutions.service;

import com.kea.alphasolutions.model.Resource;
import com.kea.alphasolutions.repository.ResourceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResourceService {

    private final ResourceRepository resourceRepository;

    public ResourceService(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    public List<Resource> getAllResources() {
        return resourceRepository.findAll();
    }

    public Resource getResourceById(int id) {
        return resourceRepository.findById(id);
    }

    public void addResource(Resource resource) {
        resourceRepository.save(resource);
    }

    public void updateResource(Resource resource) {
        resourceRepository.update(resource);
    }

    public void deleteResource(int id) {
        resourceRepository.delete(id);
    }
}
