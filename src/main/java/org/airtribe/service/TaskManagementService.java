package org.airtribe.service;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.transaction.annotation.Transactional;
import org.airtribe.dto.task.request.TaskCreationRequest;
import org.airtribe.dto.task.request.TaskUpdateRequest;
import org.airtribe.dto.task.response.TaskResponse;
import org.airtribe.entity.Task;
import org.airtribe.entity.Status;
import org.airtribe.entity.User;
import org.airtribe.repository.TaskRepository;
import org.airtribe.exception.ResourceNotFoundException;
import org.airtribe.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TaskManagementService {
    private static final Logger log = LoggerFactory.getLogger(TaskManagementService.class);

    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private UserRepository userRepository;

    public TaskResponse assignTask(Long taskId, Long userId){
        Optional<Task> task = taskRepository.findById(taskId);
        if(task.isEmpty())
            throw new ResourceNotFoundException("Task not found with taskId: "+taskId);
        Optional<User> user = userRepository.findById(userId);
        if(user.isEmpty())
            throw new ResourceNotFoundException("User not found with userId: "+userId);

        task.get().setUserAssigned(user.get());
        Task savedTask = taskRepository.save(task.get());
        return mapToResponse(savedTask);
    }

    public TaskResponse createTask(TaskCreationRequest taskRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Optional<User> creator = userRepository.findByEmail(username);
        auth.getPrincipal();
        Task task = new Task();
        task.setTitle(taskRequest.getTitle());
        task.setDescription(taskRequest.getDescription());
        task.setDueDate(taskRequest.getDueDate());
        task.setCreator(creator.get());
        if(taskRequest.getAssignUser() != null){
            Long userId = taskRequest.getAssignUser();
            Optional<User> user = userRepository.findById(userId);
            if(user.isEmpty()){
                throw new ResourceNotFoundException("User Not Found");
            }
            task.setUserAssigned(user.get());
        }
        Task savedTask = taskRepository.save(task);
        return mapToResponse(savedTask);
    }

    public List<TaskResponse> getAllTasks(int pageNo, int pageSize, Sort.Direction dir, String sortBy) {
        Sort sort = Sort.by(dir, sortBy);
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        return taskRepository.findAll(pageable).stream()
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
        if (updateRequest.getAssignedUser() != null){
            Long userId = updateRequest.getAssignedUser();
            Optional<User> user = userRepository.findById(userId);
            if(user.isEmpty()){
                throw new ResourceNotFoundException("User Not Found");
            }
            task.setUserAssigned(user.get());
        }
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
