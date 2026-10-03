package org.example.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class User {

    private int id;
    private String name;
    private String email;
    private String password;

    // Audit Fields
    private Integer createdBy;       // Foreign key ID (Nullable)
    private String createdByName;   // Display name for UI joins
    private LocalDateTime createdDate;

    private Integer updatedBy;       // Foreign key ID (Nullable)
    private String updatedByName;   // Display name for UI joins
    private LocalDateTime updatedDate;

    // RBAC Fields
    private Set<Role> roles = new HashSet<>();
    private Set<String> permissions = new HashSet<>();

    // 1. No-argument constructor
    public User() {
    }

    // 2. Constructor for creation without password
    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    // 3. Constructor for creation with password
    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    // 4. Constructor with ID (without password) - FIXES THE COMPILER ERROR
    public User(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    // 5. Constructor with ID & password
    public User(int id, String name, String email, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Integer getCreatedBy() { return createdBy; }
    public void setCreatedBy(Integer createdBy) { this.createdBy = createdBy; }

    public String getCreatedByName() { return createdByName; }
    public void setCreatedByName(String createdByName) { this.createdByName = createdByName; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }

    public Integer getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(Integer updatedBy) { this.updatedBy = updatedBy; }

    public String getUpdatedByName() { return updatedByName; }
    public void setUpdatedByName(String updatedByName) { this.updatedByName = updatedByName; }

    public LocalDateTime getUpdatedDate() { return updatedDate; }
    public void setUpdatedDate(LocalDateTime updatedDate) { this.updatedDate = updatedDate; }

    public Set<Role> getRoles() { return roles; }
    public void setRoles(Set<Role> roles) { this.roles = roles; }

    public Set<String> getPermissions() { return permissions; }
    public void setPermissions(Set<String> permissions) { this.permissions = permissions; }

    public boolean hasPermission(String permissionName) {
        return permissions.contains(permissionName);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
