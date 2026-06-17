package com.amarildo.projectflow.task;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/api/projects/{projectId}/tasks")
    public List<TaskResponse> findByProject(@PathVariable Long projectId) {
        return taskService.findByProject(projectId);
    }

    @PostMapping("/api/projects/{projectId}/tasks")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse create(
            @PathVariable Long projectId,
            @Valid @RequestBody CreateTaskRequest request
    ) {
        return taskService.create(projectId, request);
    }

    @GetMapping("/api/tasks/{taskId}")
    public TaskResponse findById(@PathVariable Long taskId) {
        return taskService.findById(taskId);
    }

    @PatchMapping("/api/tasks/{taskId}")
    public TaskResponse update(
            @PathVariable Long taskId,
            @Valid @RequestBody UpdateTaskRequest request
    ) {
        return taskService.update(taskId, request);
    }

    @PatchMapping("/api/tasks/{taskId}/status")
    public TaskResponse updateStatus(
            @PathVariable Long taskId,
            @Valid @RequestBody UpdateTaskStatusRequest request
    ) {
        return taskService.updateStatus(taskId, request);
    }

    @DeleteMapping("/api/tasks/{taskId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long taskId) {
        taskService.delete(taskId);
    }
}