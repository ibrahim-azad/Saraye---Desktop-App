package ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import models.User;
import ui.utils.AlertUtil;
import ui.utils.NavigationUtil;

/**
 * ProfileController - Handles user profile view and management
 * Allows users to view their account information
 *
 * GRASP Patterns Applied:
 * - Controller: Handles profile UI events
 * - Information Expert: Knows how to display user information
 * - Low Coupling: Depends only on models and utilities
 */
public class ProfileController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField phoneField;

    @FXML
    private TextField roleField;

    @FXML
    private Label memberSinceLabel;

    @FXML
    private Label statsIconLabel;

    @FXML
    private Label statsTypeLabel;

    @FXML
    private Label statsValueLabel;

    @FXML
    private HBox editButtonsBox;

    private User currentUser;

    /**
     * Set user data (called by NavigationUtil)
     */
    public void setUser(Object userData) {
        if (userData instanceof User) {
            this.currentUser = (User) userData;
            displayUserProfile();
        }
    }

    /**
     * Display user profile information
     */
    private void displayUserProfile() {
        if (currentUser == null) {
            return;
        }

        // Display basic information
        nameField.setText(currentUser.getName());
        emailField.setText(currentUser.getEmail());
        phoneField.setText(currentUser.getPhone());

        // Format role display
        String roleDisplay = formatRole(currentUser.getRole());
        roleField.setText(roleDisplay);

        // Set member since (mock - would come from database)
        memberSinceLabel.setText("November 2025");

        // Set stats based on role
        if ("guest".equalsIgnoreCase(currentUser.getRole())) {
            statsIconLabel.setText("📋");
            statsTypeLabel.setText("Total Bookings");
            statsValueLabel.setText("0"); // Mock - would come from database
        } else if ("host".equalsIgnoreCase(currentUser.getRole())) {
            statsIconLabel.setText("🏠");
            statsTypeLabel.setText("Properties Listed");
            statsValueLabel.setText("0"); // Mock - would come from database
        } else if ("both".equalsIgnoreCase(currentUser.getRole())) {
            statsIconLabel.setText("🌟");
            statsTypeLabel.setText("Activities");
            statsValueLabel.setText("0"); // Mock - would come from database
        }
    }

    /**
     * Format role string for display
     */
    private String formatRole(String role) {
        if (role == null) {
            return "Unknown";
        }

        switch (role.toLowerCase()) {
            case "guest":
                return "Guest Account";
            case "host":
                return "Host Account";
            case "both":
                return "Guest & Host Account";
            default:
                return role;
        }
    }

    /**
     * Handle Edit Profile button
     */
    @FXML
    private void handleEditProfile() {
        // Enable editing of name and phone fields
        nameField.setEditable(true);
        phoneField.setEditable(true);

        // Change field styles to indicate editable
        nameField.setStyle(
                "-fx-padding: 12; -fx-background-color: #ffffff; -fx-font-size: 16px; -fx-border-color: #3498db; -fx-border-width: 2;");
        phoneField.setStyle(
                "-fx-padding: 12; -fx-background-color: #ffffff; -fx-font-size: 16px; -fx-border-color: #3498db; -fx-border-width: 2;");

        // Show edit action buttons
        if (editButtonsBox != null) {
            editButtonsBox.setVisible(true);
            editButtonsBox.setManaged(true);
        }

        // Focus on name field
        nameField.requestFocus();

        AlertUtil.showInfo(
                "Edit Mode",
                "You can now edit your profile information.\n\n" +
                        "Editable fields:\n" +
                        "• Name\n" +
                        "• Phone Number\n\n" +
                        "Click 'Save Changes' when done or 'Cancel' to discard changes.");
    }

    /**
     * Handle Save Changes button
     */
    @FXML
    private void handleSaveChanges() {
        if (currentUser == null)
            return;

        String newName = nameField.getText().trim();
        String newPhone = phoneField.getText().trim();

        // Validate inputs
        if (newName.isEmpty()) {
            AlertUtil.showError("Validation Error", "Name cannot be empty!");
            return;
        }
        if (newPhone.isEmpty()) {
            AlertUtil.showError("Validation Error", "Phone number cannot be empty!");
            return;
        }

        // Call business logic to update profile
        controllers.AccountController accountController = controllers.AccountController.getInstance();
        String result = accountController.updateProfile(newName, newPhone);

        if ("SUCCESS".equals(result)) {
            // Update current user object
            currentUser.setName(newName);
            currentUser.setPhone(newPhone);

            // Disable editing
            nameField.setEditable(false);
            phoneField.setEditable(false);

            // Restore read-only styling
            nameField.setStyle("-fx-padding: 12; -fx-background-color: #ecf0f1; -fx-font-size: 16px;");
            phoneField.setStyle("-fx-padding: 12; -fx-background-color: #ecf0f1; -fx-font-size: 16px;");

            // Hide edit action buttons
            if (editButtonsBox != null) {
                editButtonsBox.setVisible(false);
                editButtonsBox.setManaged(false);
            }

            AlertUtil.showSuccess("Profile Updated", "Your profile has been updated successfully!");
        } else {
            AlertUtil.showError("Update Failed", result);
        }
    }

    /**
     * Handle Cancel Edit button
     */
    @FXML
    private void handleCancelEdit() {
        // Restore original values
        displayUserProfile();

        // Disable editing
        nameField.setEditable(false);
        phoneField.setEditable(false);

        // Restore read-only styling
        nameField.setStyle("-fx-padding: 12; -fx-background-color: #ecf0f1; -fx-font-size: 16px;");
        phoneField.setStyle("-fx-padding: 12; -fx-background-color: #ecf0f1; -fx-font-size: 16px;");

        // Hide edit action buttons
        if (editButtonsBox != null) {
            editButtonsBox.setVisible(false);
            editButtonsBox.setManaged(false);
        }

        AlertUtil.showInfo("Changes Discarded", "Your profile has not been modified.");
    }

    /**
     * Handle Change Password button - navigate to password change screen
     */
    @FXML
    private void handleChangePassword() {
        NavigationUtil.navigateTo("change-password.fxml", currentUser);
    }

    /**
     * Handle Back button
     * Navigate back to appropriate dashboard based on role
     */
    @FXML
    private void handleBack() {
        if (currentUser == null) {
            NavigationUtil.navigateTo("login.fxml");
            return;
        }

        // Navigate to appropriate dashboard based on role
        String role = currentUser.getRole();
        if ("host".equalsIgnoreCase(role)) {
            NavigationUtil.navigateTo("host-dashboard.fxml", currentUser);
        } else {
            // Default to guest dashboard for guest or both roles
            NavigationUtil.navigateTo("guest-dashboard.fxml", currentUser);
        }
    }
}
