package ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
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
        // TODO: Implement profile editing functionality
        // This will be available after business logic integration
        AlertUtil.showInfo(
            "Edit Profile",
            "Profile editing will be available soon!\n\n" +
            "This feature will allow you to:\n" +
            "• Update your name\n" +
            "• Change your phone number\n" +
            "• Update your profile picture\n\n" +
            "Note: Email cannot be changed for security reasons."
        );
    }

    /**
     * Handle Change Password button
     */
    @FXML
    private void handleChangePassword() {
        // TODO: Implement password change functionality
        // This will be available after business logic integration
        AlertUtil.showInfo(
            "Change Password",
            "Password change will be available soon!\n\n" +
            "This feature will allow you to:\n" +
            "• Enter current password\n" +
            "• Set new password\n" +
            "• Confirm new password\n\n" +
            "Your password will be securely encrypted."
        );
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
