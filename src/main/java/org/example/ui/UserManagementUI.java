package org.example.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.controller.AddUserModalController;
import org.example.dto.UserDto;
import org.example.service.UserService;
import org.example.service.UserSession;
import org.example.util.ExceptionHandler;
import org.example.util.ViewManager;

import java.util.List;

public class UserManagementUI {

    private final UserService userService = new UserService(new org.example.dao.UserDAO());
    private final TableView<UserDto> tableView = new TableView<>();
    private final TextField searchField = new TextField();

    private final ObservableList<UserDto> userData = FXCollections.observableArrayList();
    private final FilteredList<UserDto> filteredData = new FilteredList<>(userData, p -> true);

    private final VBox root;

    public UserManagementUI(Stage primaryStage) {
        // 1. Configure Table Columns
        TableColumn<UserDto, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setVisible(false);

        TableColumn<UserDto, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<UserDto, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        // --- Add Audit Columns ---
        TableColumn<UserDto, String> createdByColumn = new TableColumn<>("Created By");
        createdByColumn.setCellValueFactory(new PropertyValueFactory<>("createdByName"));

        TableColumn<UserDto, String> createdDateColumn = new TableColumn<>("Created Date");
        createdDateColumn.setCellValueFactory(new PropertyValueFactory<>("createdDate"));

        TableColumn<UserDto, String> updatedByColumn = new TableColumn<>("Updated By");
        updatedByColumn.setCellValueFactory(new PropertyValueFactory<>("updatedByName"));

        TableColumn<UserDto, String> updatedDateColumn = new TableColumn<>("Updated Date");
        updatedDateColumn.setCellValueFactory(new PropertyValueFactory<>("updatedDate"));

        tableView.getColumns().addAll(
                idColumn,
                nameColumn,
                emailColumn,
                createdByColumn,
                createdDateColumn,
                updatedByColumn,
                updatedDateColumn
        );

        initializeActionColumn();

        // 2. Data binding & Filtered list setup
        SortedList<UserDto> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sortedData);

        // 3. Search Filter Logic
        searchField.setPromptText("Search by name or email...");
        HBox.setHgrow(searchField, Priority.ALWAYS);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(user -> {
                if (newValue == null || newValue.isBlank()) return true;

                String filter = newValue.toLowerCase().trim();
                boolean matchName = user.getName() != null && user.getName().toLowerCase().contains(filter);
                boolean matchEmail = user.getEmail() != null && user.getEmail().toLowerCase().contains(filter);

                return matchName || matchEmail;
            });
        });

        // 4. User Info Header & Logout Button
        // User Info Header & Logout Button
        UserDto loggedInUser = UserSession.getCurrentUser();
        String activeUserName = (loggedInUser != null) ? loggedInUser.getName() : "User";

        Label userLabel = new Label("Logged in as: " + activeUserName);
        userLabel.setStyle("-fx-font-weight: bold;");

        Button logoutButton = new Button("Logout");
        logoutButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        logoutButton.setOnAction(e -> {
            UserSession.cleanSession();
            ViewManager.showLoginView();
        });

        Button addButton = new Button("Add User");
        addButton.setOnAction(event -> openAddUserModal());

        HBox topBar = new HBox(10, userLabel, searchField, addButton, logoutButton);
        topBar.setStyle("-fx-alignment: center-left;");

        root = new VBox(10, topBar, tableView);
        VBox.setVgrow(tableView, Priority.ALWAYS);

        loadUsers();
    }

    public VBox getRoot() {
        return root;
    }

    private void loadUsers() {
        List<UserDto> users = userService.getAllUsers();
        userData.setAll(users);
    }

    private void openAddUserModal() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AddUserModal.fxml"));
            VBox modalRoot = loader.load();

            AddUserModalController controller = loader.getController();
            controller.setUserService(userService);

            Stage modalStage = new Stage();
            modalStage.setTitle("Add New User");
            modalStage.setScene(new Scene(modalRoot));
            modalStage.showAndWait();

            loadUsers();
        } catch (Exception e) {
            ExceptionHandler.handleException(e, "Failed to load the user modal. Please try again.");
        }
    }

    private void initializeActionColumn() {
        TableColumn<UserDto, Void> actionColumn = new TableColumn<>("Actions");

        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");
            private final HBox actionButtons = new HBox(10, editButton, deleteButton);

            {
                editButton.setOnAction(event -> {
                    UserDto user = getTableView().getItems().get(getIndex());
                    openEditUserModal(user);
                });

                deleteButton.setOnAction(event -> {
                    UserDto user = getTableView().getItems().get(getIndex());
                    deleteUser(user);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(actionButtons);
                }
            }
        });

        tableView.getColumns().add(actionColumn);
    }

    private void openEditUserModal(UserDto dto) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AddUserModal.fxml"));
            VBox modalRoot = loader.load();

            AddUserModalController controller = loader.getController();
            controller.setUserService(userService);
            controller.setUser(dto);

            Stage modalStage = new Stage();
            modalStage.setTitle("Edit User");
            modalStage.setScene(new Scene(modalRoot));
            modalStage.showAndWait();

            loadUsers();
        } catch (Exception e) {
            ExceptionHandler.handleException(e, "Failed to load the user modal. Please try again.");
        }
    }

    private void deleteUser(UserDto dto) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Delete User");
        confirmation.setHeaderText("Delete selected user?");
        confirmation.setContentText("User: " + dto.getName());
        if (confirmation.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                userService.deleteUserById(dto.getId());
                showMessage("Success", "User deleted successfully!");
                loadUsers();
            } catch (Exception e) {
                showMessage("Error", e.getMessage());
            }
        }
    }

    private void showMessage(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}