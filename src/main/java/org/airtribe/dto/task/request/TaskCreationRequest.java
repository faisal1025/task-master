package org.airtribe.dto.task.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class TaskCreationRequest {
    @NotNull
    private String title;
    @NotNull
    private String description;
    @NotNull
    private LocalDateTime dueDate;

    public TaskCreationRequest() {
    }

    public TaskCreationRequest(String title, String description, LocalDateTime dueDate) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
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
