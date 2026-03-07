package org.airtribe.controller;

import org.airtribe.dto.task.request.TaskCreationRequest;
import org.airtribe.dto.task.request.TaskUpdateRequest;
import org.airtribe.dto.task.response.TaskResponse;
import org.airtribe.service.TaskManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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
    public ResponseEntity<List<TaskResponse>> listTasks(@RequestParam(value = "page", defaultValue = "0") int pageNo,
                                                        @RequestParam(value = "size", defaultValue = "10") int pageSize,
                                                        @RequestParam(value = "sort", defaultValue = "dueDate") String sortBy,
                                                        @RequestParam(value = "dir", defaultValue = "asc") String dir) {
        if(pageNo < 0) pageNo = 0;
        if(pageSize >= 200) pageSize = 10;
        Sort.Direction direction = dir.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        List<TaskResponse> tasks = taskService.getAllTasks(pageNo, pageSize, direction, sortBy);
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

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/assign/")
    public ResponseEntity<TaskResponse> assignTask(@PathVariable("id") Long taskId,
                                       @RequestParam("user_id") Long userId) {
        TaskResponse response = taskService.assignTask(taskId, userId);
        return ResponseEntity.ok(response);
    }
}
