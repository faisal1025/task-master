package org.airtribe.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedBy;

import java.time.LocalDateTime;

@Entity
@Table(name = "task")
public class Task extends BaseEntity {
    @NotNull
    private String title;

    @NotNull
    private String description;

    @NotNull
    private LocalDateTime dueDate;

    @Enumerated(EnumType.STRING)
    private Status status = Status.OPEN;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;

    // Many Task to One User
    @ManyToOne
    @JoinColumn(name = "assigned_user_id")
    private User userAssigned;

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



    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public User getUserAssigned() {
        return userAssigned;
    }

    public void setUserAssigned(User userAssigned) {
        this.userAssigned = userAssigned;
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
