package org.airtribe.controller;

import org.airtribe.dto.task.request.TaskCreationRequest;
import org.airtribe.dto.task.request.TaskUpdateRequest;
import org.airtribe.dto.task.response.TaskResponse;
import org.airtribe.service.TaskManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@Validated
public class TaskController {
    @Autowired
    private TaskManagementService taskService;

    @PostMapping("/")
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskCreationRequest taskRequest) {
        TaskResponse taskResponse = taskService.createTask(taskRequest);
        URI location = URI.create("/api/tasks/" + taskResponse.getId());
        return ResponseEntity.created(location).body(taskResponse);
    }

    @GetMapping("/")
    public ResponseEntity<List<TaskResponse>> listTasks() {
        List<TaskResponse> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTask(@PathVariable Long id) {
        TaskResponse response = taskService.getTaskById(id);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable Long id, @Valid @RequestBody TaskUpdateRequest updateRequest) {
        TaskResponse updated = taskService.updateTask(id, updateRequest);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
