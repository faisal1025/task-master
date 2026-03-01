package org.airtribe.entity;

import jakarta.persistence.Entity;

import java.time.LocalDateTime;

@Entity
public class Task extends BaseEntity {
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private Status status = Status.OPEN;

    public Task() {
    }

    public Task(String title, String description, LocalDateTime dueDate) {
        this.title = title;
        this.description = description;
        this.dueDate =dueDate;
    }

    public Task(String title, String description, LocalDateTime dueDate, Status status) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.status = status;
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
