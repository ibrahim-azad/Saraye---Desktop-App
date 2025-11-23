package ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import models.Property;
import models.User;
import controllers.ReportController;
import ui.utils.AlertUtil;
import ui.utils.NavigationUtil;

public class ReportIssueController {

    @FXML private Label propertyTitleLabel;
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private TextArea descriptionTextArea;
    @FXML private Label charCountLabel;
    @FXML private Label messageLabel;

    private User currentUser;
    private Property currentProperty;
    private ReportController reportController;

    @FXML
    private void initialize() {
        reportController = ReportController.getInstance();

        // Populate category ComboBox
        categoryComboBox.getItems().addAll(
            "Misleading Information",
            "Property Condition",
            "Safety Concerns",
            "Pricing Issues",
            "Host Behavior",
            "Other"
        );

        // Character counter for description
        descriptionTextArea.textProperty().addListener((observable, oldValue, newValue) -> {
            int length = newValue.length();
            charCountLabel.setText(length + " / 500 characters");

            // Limit to 500 characters
            if (length > 500) {
                descriptionTextArea.setText(oldValue);
            }

            // Change color based on length
            if (length < 20) {
                charCountLabel.setStyle("-fx-text-fill: #d32f2f;");
            } else if (length < 100) {
                charCountLabel.setStyle("-fx-text-fill: #ff9800;");
            } else {
                charCountLabel.setStyle("-fx-text-fill: #4caf50;");
            }
        });
    }

    public void setData(User user, Property property) {
        this.currentUser = user;
        this.currentProperty = property;

        if (property != null) {
            propertyTitleLabel.setText(property.getTitle() + " (ID: " + property.getPropertyId() + ")");
        }
    }

    @FXML
    private void handleSubmitReport() {
        // Reset message
        messageLabel.setVisible(false);

        // Validate inputs
        String category = categoryComboBox.getValue();
        String description = descriptionTextArea.getText().trim();

        if (category == null || category.isEmpty()) {
            showError("Please select an issue category");
            return;
        }

        if (description.isEmpty()) {
            showError("Please describe the issue");
            return;
        }

        if (description.length() < 20) {
            showError("Description must be at least 20 characters");
            return;
        }

        // Create full description with category
        String fullDescription = "[" + category + "] " + description;

        // Submit report through controller
        String result = reportController.createReport(
            currentProperty.getPropertyId(),
            fullDescription
        );

        if ("SUCCESS".equals(result)) {
            AlertUtil.showSuccess("Report Submitted",
                "Your report has been submitted successfully.\n" +
                "Our moderation team will review it within 24-48 hours.");

            // Navigate back to property details
            NavigationUtil.navigateWithMultipleData("property-details.fxml", currentUser, currentProperty);
        } else {
            showError(result);
        }
    }

    @FXML
    private void handleCancel() {
        // Navigate back to property details
        NavigationUtil.navigateWithMultipleData("property-details.fxml", currentUser, currentProperty);
    }

    private void showError(String message) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: #d32f2f; -fx-font-weight: bold;");
        messageLabel.setVisible(true);
    }
}
