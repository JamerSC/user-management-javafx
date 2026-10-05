package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.dao.UserDAO;
import org.example.dto.UserDto;
import org.example.service.UserService;
import org.example.service.UserSession;

import java.util.List;

public class UserController {

    @FXML private TableView<UserDto> tableView;
    @FXML private TableColumn<UserDto, Integer> idColumn;
    @FXML private TableColumn<UserDto, String> nameColumn;
    @FXML private TableColumn<UserDto, String> emailColumn;

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    @FXML private TableColumn<UserDto, String> createdByColumn;
    @FXML private TableColumn<UserDto, String> createdDateColumn;
    @FXML private TableColumn<UserDto, String> updatedByColumn;
    @FXML private TableColumn<UserDto, String> updatedDateColumn;

    @FXML private TextField searchField;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;

    private final ObservableList<UserDto> userData = FXCollections.observableArrayList();
    private FilteredList<UserDto> filteredData;
    private final UserService userService = new UserService(new UserDAO());

    @FXML
    public void initialize() {
        if (idColumn != null) idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        if (nameColumn != null) nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        if (emailColumn != null) emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        if (createdByColumn != null) createdByColumn.setCellValueFactory(new PropertyValueFactory<>("createdByName"));
        if (createdDateColumn != null) createdDateColumn.setCellValueFactory(new PropertyValueFactory<>("createdDate"));
        if (updatedByColumn != null) updatedByColumn.setCellValueFactory(new PropertyValueFactory<>("updatedByName"));
        if (updatedDateColumn != null) updatedDateColumn.setCellValueFactory(new PropertyValueFactory<>("updatedDate"));

        // Disable or hide UI controls based on permissions
        if (updateButton != null) {
            updateButton.setDisable(!UserSession.hasPermission("USER_UPDATE"));
        }
        if (deleteButton != null) {
            deleteButton.setDisable(!UserSession.hasPermission("USER_DELETE"));
        }

        loadUsers();

        if (searchField != null) {
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (filteredData != null) {
                    filteredData.setPredicate(user -> {
                        if (newValue == null || newValue.isBlank()) return true;
                        String filter = newValue.toLowerCase().trim();
                        return (user.getName() != null && user.getName().toLowerCase().contains(filter))
                                || (user.getEmail() != null && user.getEmail().toLowerCase().contains(filter));
                    });
                }
            });
        }
    }

    public void loadUsers() {
        try {
            List<UserDto> users = userService.getAllUsers();
            userData.setAll(users);

            if (filteredData == null) {
                filteredData = new FilteredList<>(userData, user -> true);
                SortedList<UserDto> sortedData = new SortedList<>(filteredData);
                sortedData.comparatorProperty().bind(tableView.comparatorProperty());
                tableView.setItems(sortedData);
            }
        } catch (SecurityException e) {
            showError("Access Denied", e.getMessage());
        }
    }

    public void addUser() {
        try {
            String name = nameField.getText();
            String email = emailField.getText();
            String password = passwordField.getText();
            userService.createUser(name, email, password);
            loadUsers();
        } catch (Exception e) {
            showError("Operation Failed", e.getMessage());
        }
    }

    public void updateUser() {
        try {
            UserDto selectedUser = tableView.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                userService.updateUser(
                        selectedUser.getId(),
                        nameField.getText(),
                        emailField.getText(),
                        passwordField.getText()
                );
                loadUsers();
            }
        } catch (Exception e) {
            showError("Operation Failed", e.getMessage());
        }
    }

    public void deleteUser() {
        try {
            UserDto selectedUser = tableView.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                userService.deleteUserById(selectedUser.getId());
                loadUsers();
            }
        } catch (Exception e) {
            showError("Operation Failed", e.getMessage());
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
