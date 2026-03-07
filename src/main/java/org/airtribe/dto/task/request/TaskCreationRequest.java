package org.airtribe.dto.task.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class TaskCreationRequest {
    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dueDate;

    private Long assignUser = null;

    public TaskCreationRequest() {
    }

    public TaskCreationRequest(String title, String description, LocalDateTime dueDate) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
    }

    public Long getAssignUser() {
        return assignUser;
    }

    public void setAssignUser(Long assignUser) {
        this.assignUser = assignUser;
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
}
