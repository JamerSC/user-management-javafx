package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.dao.UserDAO;
import org.example.dto.UserDto;
import org.example.service.UserService;

import java.util.List;

public class UserController {


    @FXML
    private TableView<UserDto> tableView;

    @FXML
    private TableColumn<UserDto, Integer> idColumn;

    @FXML
    private TableColumn<UserDto, String> nameColumn;

    @FXML
    private TableColumn<UserDto, String> emailColumn;

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TableColumn<UserDto, String> createdByColumn;

    @FXML
    private TableColumn<UserDto, String> createdDateColumn;

    @FXML
    private TableColumn<UserDto, String> updatedByColumn;

    @FXML
    private TableColumn<UserDto, String> updatedDateColumn;

    @FXML
    private TextField searchField;

    private final ObservableList<UserDto> userData =
            FXCollections.observableArrayList();

    private FilteredList<UserDto> filteredData;

    private final UserService userService = new UserService(new UserDAO());

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        // Optional: Bind Audit columns
        if (createdByColumn != null) createdByColumn.setCellValueFactory(new PropertyValueFactory<>("createdByName"));
        if (createdDateColumn != null) createdDateColumn.setCellValueFactory(new PropertyValueFactory<>("createdDate"));
        if (updatedByColumn != null) updatedByColumn.setCellValueFactory(new PropertyValueFactory<>("updatedByName"));
        if (updatedDateColumn != null) updatedDateColumn.setCellValueFactory(new PropertyValueFactory<>("updatedDate"));

        loadUsers();

        // Search
        searchField.textProperty().addListener(
                (observable, oldValue, newValue) -> {

                    filteredData.setPredicate(user -> {

                        if (newValue == null || newValue.isBlank()) {
                            return true;
                        }

                        String filter =
                                newValue.toLowerCase().trim();

                        return user.getName()
                                .toLowerCase()
                                .contains(filter)

                                || user.getEmail()
                                .toLowerCase()
                                .contains(filter);
                    });
                }
        );
    }

    public void loadUsers() {
        List<UserDto> users =
                userService.getAllUsers();

        // Update existing ObservableList
        userData.setAll(users);

        // Create FilteredList only once
        if (filteredData == null) {

            filteredData =
                    new FilteredList<>(
                            userData,
                            user -> true
                    );

            SortedList<UserDto> sortedData =
                    new SortedList<>(filteredData);

            sortedData.comparatorProperty()
                    .bind(tableView.comparatorProperty());

            tableView.setItems(sortedData);
        }

//        tableView.setItems(FXCollections.observableArrayList(users));
    }

    public void addUser() {
        String name = nameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        userService.createUser(name, email, password);
        loadUsers();
    }

    public void updateUser() {
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
    }

    public void deleteUser() {
        UserDto selectedUser = tableView.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            userService.deleteUserById(selectedUser.getId());
            loadUsers();
        }
    }
}
