package org.example.util;

import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.ui.LoginUI;
import org.example.ui.UserManagementUI;

public class ViewManager {

    private static Stage primaryStage;

    public static void setStage(Stage stage) {
        primaryStage = stage;
    }

    public static void showLoginView() {
        LoginUI loginUI = new LoginUI();
        Scene scene = new Scene(loginUI.getRoot(), 400, 300);

        primaryStage.setScene(scene);
        primaryStage.setTitle("User Login");
        primaryStage.setWidth(400);
        primaryStage.setHeight(300);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    public static void showUserManagementView() {
        UserManagementUI userManagementUI = new UserManagementUI(primaryStage);

        primaryStage.getScene().setRoot(userManagementUI.getRoot());
        primaryStage.setTitle("User Management");
        primaryStage.setWidth(1000);
        primaryStage.setHeight(700);
        primaryStage.centerOnScreen();
    }
}
