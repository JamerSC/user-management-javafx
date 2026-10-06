package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.example.dto.UserDto;

public class ViewUserModalController {

    @FXML
    private Label nameLabel;
    @FXML private Label emailLabel;
    @FXML private Label rolesLabel;
    @FXML private Label permissionsLabel;
    @FXML private Label createdByLabel;
    @FXML private Label createdDateLabel;
    @FXML private Label updatedByLabel;
    @FXML private Label updatedDateLabel;

    public void setUser(UserDto dto) {
        if (dto == null) return;

        nameLabel.setText(dto.getName() != null ? dto.getName() : "N/A");
        emailLabel.setText(dto.getEmail() != null ? dto.getEmail() : "N/A");

        // Roles and Permissions Formatting
        String rolesStr = (dto.getRoles() != null && !dto.getRoles().isEmpty())
                ? String.join(", ", dto.getRoles())
                : "None";
        rolesLabel.setText(rolesStr);

        String permsStr = (dto.getPermissions() != null && !dto.getPermissions().isEmpty())
                ? String.join(", ", dto.getPermissions())
                : "None";
        permissionsLabel.setText(permsStr);

        // Audit Metadata Formatting
        createdByLabel.setText(dto.getCreatedByName() != null ? dto.getCreatedByName() : "System / Initial");
        createdDateLabel.setText(dto.getCreatedDate() != null ? dto.getCreatedDate() : "N/A");
        updatedByLabel.setText(dto.getUpdatedByName() != null ? dto.getUpdatedByName() : "N/A");
        updatedDateLabel.setText(dto.getUpdatedDate() != null ? dto.getUpdatedDate() : "N/A");
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) nameLabel.getScene().getWindow();
        stage.close();
    }
}
