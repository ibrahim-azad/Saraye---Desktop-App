package ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import models.User;
import ui.utils.AlertUtil;
import ui.utils.NavigationUtil;
import utils.SessionManager;

/**
 * ChangePasswordController - Handles password change functionality
 * Provides a dedicated screen for secure password updates
 *
 * GRASP Patterns Applied:
 * - Controller: Handles password change UI events
 * - Information Expert: Validates password requirements
 * - Low Coupling: Depends only on models and business logic
 */
public class ChangePasswordController {

    @FXML
    private PasswordField currentPasswordField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private PasswordField confirmPasswordField;

    private User currentUser;

    /**
     * Initialize the controller
     */
    @FXML
    public void initialize() {
        // Get current user from session
        SessionManager sessionManager = SessionManager.getInstance();
        currentUser = sessionManager.getCurrentUser();

        if (currentUser == null) {
            AlertUtil.showError("Session Error", "No user is logged in!");
            handleBack();
        }
    }

    /**
     * Handle Change Password button
     */
    @FXML
    private void handleChangePassword() {
        if (currentUser == null) {
            AlertUtil.showError("Session Error", "No user is logged in!");
            return;
        }

        // Get input values
        String currentPassword = currentPasswordField.getText().trim();
        String newPassword = newPasswordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();

        // Validate inputs
        if (currentPassword.isEmpty()) {
            AlertUtil.showError("Validation Error", "Please enter your current password!");
            currentPasswordField.requestFocus();
            return;
        }

        if (newPassword.isEmpty()) {
            AlertUtil.showError("Validation Error", "Please enter a new password!");
            newPasswordField.requestFocus();
            return;
        }

        if (newPassword.length() < 6) {
            AlertUtil.showError("Validation Error", "New password must be at least 6 characters long!");
            newPasswordField.requestFocus();
            return;
        }

        if (confirmPassword.isEmpty()) {
            AlertUtil.showError("Validation Error", "Please confirm your new password!");
            confirmPasswordField.requestFocus();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            AlertUtil.showError("Validation Error", "New passwords do not match!");
            confirmPasswordField.clear();
            confirmPasswordField.requestFocus();
            return;
        }

        if (currentPassword.equals(newPassword)) {
            AlertUtil.showError("Validation Error", "New password must be different from current password!");
            newPasswordField.clear();
            confirmPasswordField.clear();
            newPasswordField.requestFocus();
            return;
        }

        // Call business logic to change password
        controllers.AccountController accountController = controllers.AccountController.getInstance();
        String result = accountController.changePassword(currentPassword, newPassword);

        if ("SUCCESS".equals(result)) {
            AlertUtil.showSuccess("Password Changed",
                    "Your password has been changed successfully!\n\nPlease use your new password for future logins.");

            // Navigate back to profile
            handleBack();
        } else {
            AlertUtil.showError("Password Change Failed", result);

            // Clear all fields if current password was incorrect
            if (result.contains("current password")) {
                currentPasswordField.clear();
                newPasswordField.clear();
                confirmPasswordField.clear();
                currentPasswordField.requestFocus();
            }
        }
    }

    /**
     * Handle Cancel button
     */
    @FXML
    private void handleCancel() {
        handleBack();
    }

    /**
     * Handle Back button - navigate to profile
     */
    @FXML
    private void handleBack() {
        NavigationUtil.navigateTo("profile.fxml", currentUser);
    }
}
