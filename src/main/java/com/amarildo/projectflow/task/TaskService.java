package com.amarildo.projectflow.task;

import com.amarildo.projectflow.exception.ResourceNotFoundException;
import com.amarildo.projectflow.project.Project;
import com.amarildo.projectflow.project.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;

    public TaskService(TaskRepository taskRepository, ProjectRepository projectRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> findByProject(Long projectId) {
        ensureProjectExists(projectId);

        return taskRepository.findByProjectIdOrderByCreatedAtDesc(projectId)
                .stream()
                .map(TaskResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public TaskResponse findById(Long taskId) {
        ProjectTask task = getTaskOrFail(taskId);
        return TaskResponse.fromEntity(task);
    }

    @Transactional
    public TaskResponse create(Long projectId, CreateTaskRequest request) {
        Project project = getProjectOrFail(projectId);

        ProjectTask task = new ProjectTask(
                request.title().trim(),
                normalizeText(request.description()),
                request.priority(),
                request.dueDate(),
                project
        );

        ProjectTask savedTask = taskRepository.save(task);
        return TaskResponse.fromEntity(savedTask);
    }

    @Transactional
    public TaskResponse update(Long taskId, UpdateTaskRequest request) {
        ProjectTask task = getTaskOrFail(taskId);

        if (request.title() != null && !request.title().isBlank()) {
            task.setTitle(request.title().trim());
        }

        if (request.description() != null) {
            task.setDescription(normalizeText(request.description()));
        }

        if (request.status() != null) {
            task.setStatus(request.status());
        }

        if (request.priority() != null) {
            task.setPriority(request.priority());
        }

        if (request.dueDate() != null) {
            task.setDueDate(request.dueDate());
        }

        task.markUpdated();

        return TaskResponse.fromEntity(task);
    }

    @Transactional
    public TaskResponse updateStatus(Long taskId, UpdateTaskStatusRequest request) {
        ProjectTask task = getTaskOrFail(taskId);
        task.setStatus(request.status());
        task.markUpdated();

        return TaskResponse.fromEntity(task);
    }

    @Transactional
    public void delete(Long taskId) {
        ProjectTask task = getTaskOrFail(taskId);
        taskRepository.delete(task);
    }

    private Project getProjectOrFail(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));
    }

    private void ensureProjectExists(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new ResourceNotFoundException("Project not found with id: " + projectId);
        }
    }

    private ProjectTask getTaskOrFail(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));
    }

    private String normalizeText(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}