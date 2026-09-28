package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.dto.UserDto;
import org.example.model.User;
import org.example.service.UserService;

public class AddUserModalController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField idField;

    private UserService userService;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

//    @FXML
//    private void handleAddUser() {
//        String name = nameField.getText();
//        String email = emailField.getText();
//        try {
//            userService.createUser(name, email);
//            closeModal();
//        } catch (Exception e) {
//            System.err.println("Error: " + e.getMessage());
//        }
//    }

    @FXML
    private void handleSaveUser() {
        String idText = idField.getText();
        String name = nameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();

        if (!validateInput(name, email)) {
            return; // Stop execution if validation fails
        }

        try {
            if (idText == null || idText.isBlank()) {
                // Add new user
                userService.createUser(name, email, password);
            } else {
                // Edit existing user
                userService.updateUser(idText, name, email, password);
            }
            closeModal();
        } catch (Exception e) {
            showError("Error", "An error occurred: " + e.getMessage());
        }
    }

    public void setUser(UserDto user) {
        if (user != null) {
            idField.setText(String.valueOf(user.getId()));
            nameField.setText(user.getName());
            emailField.setText(user.getEmail());
            passwordField.setText(user.getPassword());
        }
    }

    private boolean validateInput(String name, String email) {
        if (name == null || name.isBlank()) {
            showError("Validation Error", "Name cannot be empty.");
            return false;
        }
        if (email == null || email.isBlank()) {
            showError("Validation Error", "Email cannot be empty.");
            return false;
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            showError("Validation Error", "Invalid email format.");
            return false;
        }
        return true;
    }


    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void closeModal() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }
}
