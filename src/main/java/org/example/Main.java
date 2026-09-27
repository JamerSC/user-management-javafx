package org.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.ui.LoginUI;
import org.example.ui.UserManagementUI;
import org.example.util.ViewManager;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        ViewManager.setStage(primaryStage);
        ViewManager.showLoginView();
    }

    public static void main(String[] args) {
        launch(args);
    }
}