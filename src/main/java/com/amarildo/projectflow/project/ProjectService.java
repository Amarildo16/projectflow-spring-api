package com.amarildo.projectflow.project;

import com.amarildo.projectflow.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> findAll() {
        return projectRepository.findAll()
                .stream()
                .map(ProjectResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProjectResponse findById(Long id) {
        Project project = getProjectOrFail(id);
        return ProjectResponse.fromEntity(project);
    }

    @Transactional
    public ProjectResponse create(CreateProjectRequest request) {
        if (projectRepository.existsByNameIgnoreCase(request.name())) {
            throw new IllegalArgumentException("A project with this name already exists");
        }

        Project project = new Project(
                request.name().trim(),
                normalizeText(request.description()),
                request.status()
        );

        Project savedProject = projectRepository.save(project);
        return ProjectResponse.fromEntity(savedProject);
    }

    @Transactional
    public ProjectResponse update(Long id, UpdateProjectRequest request) {
        Project project = getProjectOrFail(id);

        if (request.name() != null && !request.name().isBlank()) {
            project.setName(request.name().trim());
        }

        if (request.description() != null) {
            project.setDescription(normalizeText(request.description()));
        }

        if (request.status() != null) {
            project.setStatus(request.status());
        }

        return ProjectResponse.fromEntity(project);
    }

    @Transactional
    public void delete(Long id) {
        Project project = getProjectOrFail(id);
        projectRepository.delete(project);
    }

    private Project getProjectOrFail(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));
    }

    private String normalizeText(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}
