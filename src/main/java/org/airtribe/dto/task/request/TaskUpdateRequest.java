package org.airtribe.dto.task.request;

import jakarta.validation.constraints.NotNull;
import org.airtribe.entity.Status;

import java.time.LocalDateTime;

public class TaskUpdateRequest {
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private Status status;
    private Long assignedUser;

    public TaskUpdateRequest() {}

    public Long getAssignedUser() {
        return assignedUser;
    }

    public void setAssignedUser(Long assignedUser) {
        this.assignedUser = assignedUser;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}

