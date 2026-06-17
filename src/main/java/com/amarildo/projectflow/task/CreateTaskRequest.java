package com.amarildo.projectflow.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateTaskRequest(
        @NotBlank(message = "Task title is required")
        @Size(max = 160, message = "Task title must not exceed 160 characters")
        String title,

        @Size(max = 1000, message = "Description must not exceed 1000 characters")
        String description,

        TaskPriority priority,

        LocalDate dueDate
) {
}