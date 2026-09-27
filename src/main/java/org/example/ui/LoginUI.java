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
        this.authService = new AuthService(new UserService(new UserDAO()));

        Label titleLabel = new Label("System Login");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username / Email");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        ProgressIndicator loadingSpinner = new ProgressIndicator();
        loadingSpinner.setMaxSize(24, 24);
        loadingSpinner.setVisible(false);

        Button loginButton = new Button("Login");
        loginButton.setDefaultButton(true);

        // Async Login Handler
        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            // Disable controls during login task
            loginButton.setDisable(true);
            usernameField.setDisable(true);
            passwordField.setDisable(true);
            loadingSpinner.setVisible(true);
            errorLabel.setText("");

            // Execute authentication in a background thread
            // UI Thread Safety: Integrated javafx.concurrent.
            // Task to perform authentication asynchronously so the app stays responsive.

            Task<Boolean> loginTask = new Task<>() {
                @Override
                protected Boolean call() {
                    return authService.authenticate(username, password);
                }
            };

            loginTask.setOnSucceeded(event -> {
                boolean authenticated = loginTask.getValue();
                loadingSpinner.setVisible(false);

                if (authenticated) {
                    ViewManager.showUserManagementView();
                } else {
                    loginButton.setDisable(false);
                    usernameField.setDisable(false);
                    passwordField.setDisable(false);
                    errorLabel.setText("Invalid username or password.");
                }
            });

            loginTask.setOnFailed(event -> {
                loadingSpinner.setVisible(false);
                loginButton.setDisable(false);
                usernameField.setDisable(false);
                passwordField.setDisable(false);
                errorLabel.setText("An unexpected error occurred. Please try again.");
            });

            new Thread(loginTask).start();
        });

        root = new VBox(12, titleLabel, usernameField, passwordField, loginButton, loadingSpinner, errorLabel);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));
    }

    public VBox getRoot() {
        return root;
    }
}