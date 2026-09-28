package org.example.ui;

import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.dao.UserDAO;
import org.example.service.AuthService;
import org.example.service.UserService;
import org.example.util.ViewManager;

public class LoginUI {
    private final VBox root;
    private final AuthService authService;

    public LoginUI() {
        this.authService = new AuthService(new UserDAO());

        Label titleLabel = new Label("System Login");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TextField emailField = new TextField();
        emailField.setPromptText("Email Address");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        ProgressIndicator loadingSpinner = new ProgressIndicator();
        loadingSpinner.setMaxSize(24, 24);
        loadingSpinner.setVisible(false);

        Button loginButton = new Button("Login");
        loginButton.setDefaultButton(true);

        loginButton.setOnAction(e -> {
            String email = emailField.getText();
            String password = passwordField.getText();

            if (email.isBlank() || password.isBlank()) {
                errorLabel.setText("Please enter both email and password.");
                return;
            }

            // Lock UI controls during async request
            setFormDisabled(true, loginButton, emailField, passwordField, loadingSpinner);
            errorLabel.setText("");

            // Background Authentication Task
            Task<Boolean> authTask = new Task<>() {
                @Override
                protected Boolean call() {
                    return authService.authenticate(email, password);
                }
            };

            authTask.setOnSucceeded(event -> {
                boolean isAuthenticated = authTask.getValue();
                setFormDisabled(false, loginButton, emailField, passwordField, loadingSpinner);

                if (isAuthenticated) {
                    ViewManager.showUserManagementView();
                } else {
                    errorLabel.setText("Invalid email or password.");
                }
            });

            authTask.setOnFailed(event -> {
                setFormDisabled(false, loginButton, emailField, passwordField, loadingSpinner);
                errorLabel.setText("Connection error. Please try again.");
            });

            new Thread(authTask).start();
        });

        root = new VBox(12, titleLabel, emailField, passwordField, loginButton, loadingSpinner, errorLabel);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));
    }

    private void setFormDisabled(boolean disabled, Button btn, TextField email, PasswordField pwd, ProgressIndicator spinner) {
        btn.setDisable(disabled);
        email.setDisable(disabled);
        pwd.setDisable(disabled);
        spinner.setVisible(disabled);
    }

    public VBox getRoot() {
        return root;
    }
}