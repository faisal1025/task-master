package org.airtribe.service;

import org.airtribe.dto.task.request.TaskCreationRequest;
import org.airtribe.dto.task.request.TaskUpdateRequest;
import org.airtribe.dto.task.response.TaskResponse;
import org.airtribe.entity.Task;
import org.airtribe.entity.Status;
import org.airtribe.repository.TaskRepository;
import org.airtribe.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskManagementService {
    @Autowired
    private TaskRepository taskRepository;

    public TaskResponse createTask(TaskCreationRequest taskRequest) {
        Task task = new Task();
        task.setTitle(taskRequest.getTitle());
        task.setDescription(taskRequest.getDescription());
        task.setDueDate(taskRequest.getDueDate());
        Task savedTask = taskRepository.save(task);
        return mapToResponse(savedTask);
    }

    public List<TaskResponse> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public TaskResponse getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
        return mapToResponse(task);
    }

    public TaskResponse updateTask(Long id, TaskUpdateRequest updateRequest) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        if (updateRequest.getTitle() != null) task.setTitle(updateRequest.getTitle());
        if (updateRequest.getDescription() != null) task.setDescription(updateRequest.getDescription());
        if (updateRequest.getDueDate() != null) task.setDueDate(updateRequest.getDueDate());
        if (updateRequest.getStatus() != null) task.setStatus(updateRequest.getStatus());

        Task saved = taskRepository.save(task);
        return mapToResponse(saved);
    }

    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
        taskRepository.delete(task);
    }

    private TaskResponse mapToResponse(Task savedTask) {
        return new TaskResponse(
                savedTask.getId(),
                savedTask.getCreatedAt(),
                savedTask.getModifiedAt(),
                savedTask.getTitle(),
                savedTask.getDescription(),
                savedTask.getDueDate(),
                savedTask.getStatus()
        );
    }
}
