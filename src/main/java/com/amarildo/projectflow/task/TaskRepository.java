package com.amarildo.projectflow.task;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<ProjectTask, Long> {
    List<ProjectTask> findByProjectIdOrderByCreatedAtDesc(Long projectId);
}