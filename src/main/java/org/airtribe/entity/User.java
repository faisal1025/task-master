package org.airtribe.entity;

import jakarta.persistence.*;
import org.hibernate.engine.internal.Cascade;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="user")
public class User extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String fullName;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    private boolean enabled = false; // email verification

    @OneToMany(mappedBy = "creator", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Task> createdTasks = new ArrayList<>();

    @OneToMany(mappedBy = "userAssigned")
    private List<Task> assignedTasks = new ArrayList<>();

    @PreRemove
    private void preRemove() {
        for (Task t: assignedTasks){
            if(t.getCreator() == null || !this.equals(t.getCreator())){
                t.setUserAssigned(null);
            }
        }
    }

    public User() {}

    public List<Task> getCreatedTasks() {
        return createdTasks;
    }

    public void setCreatedTasks(List<Task> createdTasks) {
        this.createdTasks = createdTasks;
    }

    public List<Task> getAssignedTasks() {
        return assignedTasks;
    }

    public void setAssignedTasks(List<Task> assignedTasks) {
        this.assignedTasks = assignedTasks;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}

