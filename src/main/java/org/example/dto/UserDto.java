package org.example.dto;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.HashSet;
import java.util.Set;

public class UserDto {

//    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty id = new SimpleStringProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty email = new SimpleStringProperty();
    private final StringProperty password = new SimpleStringProperty();

    // Audit Properties
    private final StringProperty createdByName = new SimpleStringProperty();
    private final StringProperty createdDate = new SimpleStringProperty();
    private final StringProperty updatedByName = new SimpleStringProperty();
    private final StringProperty updatedDate = new SimpleStringProperty();

    // RBAC Security Collections
    private Set<String> roles = new HashSet<>();
    private Set<String> permissions = new HashSet<>();

    public UserDto() {
    }

    public UserDto(String id, String name, String email, String password) {
        this.id.set(id);
        this.name.set(name);
        this.email.set(email);
        this.password.set(password);
    }

    public String getId() {
        return id.get();
    }

    public StringProperty idProperty() {
        return id;
    }

    public void setId(String id) {
        this.id.set(id);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getEmail() {
        return email.get();
    }

    public StringProperty emailProperty() {
        return email;
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public String getPassword() {
        return password.get();
    }

    public StringProperty passwordProperty() {
        return password;
    }

    public void setPassword(String password) {
        this.password.set(password);
    }

    // Audit Getters & Setters
    public String getCreatedByName() { return createdByName.get(); }
    public StringProperty createdByNameProperty() { return createdByName; }
    public void setCreatedByName(String createdByName) { this.createdByName.set(createdByName); }

    public String getCreatedDate() { return createdDate.get(); }
    public StringProperty createdDateProperty() { return createdDate; }
    public void setCreatedDate(String createdDate) { this.createdDate.set(createdDate); }

    public String getUpdatedByName() { return updatedByName.get(); }
    public StringProperty updatedByNameProperty() { return updatedByName; }
    public void setUpdatedByName(String updatedByName) { this.updatedByName.set(updatedByName); }

    public String getUpdatedDate() { return updatedDate.get(); }
    public StringProperty updatedDateProperty() { return updatedDate; }
    public void setUpdatedDate(String updatedDate) { this.updatedDate.set(updatedDate); }

    public Set<String> getRoles() { return roles; }
    public void setRoles(Set<String> roles) { this.roles = roles; }

    public Set<String> getPermissions() { return permissions; }
    public void setPermissions(Set<String> permissions) { this.permissions = permissions; }

    @Override
    public String toString() {
        return "UserDto{" +
                "id=" + id +
                ", name=" + name +
                ", email=" + email +
                '}';
    }
}
