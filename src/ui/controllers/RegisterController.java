package ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import models.User;
import ui.utils.AlertUtil;
import ui.utils.NavigationUtil;
import ui.utils.ValidationUtil;

/**
 * RegisterController - Handles user registration
 * UC1: Register/Login
 *
 * GRASP Patterns Applied:
 * - Controller: Handles UI events and coordinates registration logic
 * - Low Coupling: Only depends on utility classes and models
 * - High Cohesion: Focused only on registration functionality
 */
public class RegisterController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField phoneField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private ComboBox<String> roleComboBox;

    @FXML
    private Label errorLabel;

    /**
     * Initialize method - called after FXML is loaded
     */
    @FXML
    private void initialize() {
        // Hide error label initially
        errorLabel.setVisible(false);

        // Populate role dropdown
        roleComboBox.getItems().addAll(
            "Guest (Looking for properties)",
            "Host (Listing properties)",
            "Both (Guest & Host)"
        );
        roleComboBox.setValue("Guest (Looking for properties)");

        // Add enter key listener to last field
        confirmPasswordField.setOnAction(event -> handleRegister());
    }

    /**
     * Handle register button click
     */
    @FXML
    private void handleRegister() {
        // Hide previous error
        errorLabel.setVisible(false);

        // Get input
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String roleSelection = roleComboBox.getValue();

        // Validate input
        if (!validateInput(name, email, phone, password, confirmPassword, roleSelection)) {
            return;
        }

        // Convert role selection to database format
        String role = convertRoleSelection(roleSelection);

        // TODO: Call business logic layer (Ibrahim's UserService)
        // For now, use mock registration
        boolean success = mockRegistration(name, email, phone, password, role);

        if (success) {
            // Show success message
            AlertUtil.showSuccess(
                "Registration Successful!",
                "Your account has been created successfully.\n\nYou can now login with your credentials."
            );

            // Navigate to login screen
            NavigationUtil.navigateTo("login.fxml");
        } else {
            // Registration failed
            showError("Registration failed! This email may already be registered.");
        }
    }

    /**
     * Handle login link click
     */
    @FXML
    private void handleLoginLink() {
        NavigationUtil.navigateTo("login.fxml");
    }

    /**
     * Validate all input fields
     */
    private boolean validateInput(String name, String email, String phone,
                                   String password, String confirmPassword, String roleSelection) {
        // Check if fields are empty
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showError("Please fill in all required fields");
            return false;
        }

        // Validate name (at least 2 characters)
        if (name.length() < 2) {
            showError("Name must be at least 2 characters long");
            return false;
        }

        // Validate email format
        if (!ValidationUtil.isValidEmail(email)) {
            showError(ValidationUtil.getEmailErrorMessage());
            return false;
        }

        // Validate phone (if provided)
        if (!phone.isEmpty() && !ValidationUtil.isValidPhone(phone)) {
            showError(ValidationUtil.getPhoneErrorMessage());
            return false;
        }

        // Validate password strength
        if (!ValidationUtil.isValidPassword(password)) {
            showError(ValidationUtil.getPasswordErrorMessage());
            return false;
        }

        // Check if passwords match
        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match! Please re-enter.");
            return false;
        }

        // Check role selection
        if (roleSelection == null || roleSelection.isEmpty()) {
            showError("Please select your role");
            return false;
        }

        return true;
    }

    /**
     * Convert user-friendly role selection to database format
     */
    private String convertRoleSelection(String selection) {
        if (selection.startsWith("Guest")) {
            return "guest";
        } else if (selection.startsWith("Host")) {
            return "host";
        } else if (selection.startsWith("Both")) {
            return "both";
        }
        return "guest"; // default
    }

    /**
     * Show error message
     */
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    /**
     * MOCK REGISTRATION - Temporary until Ibrahim provides UserService
     * Replace this with real registration later
     *
     * @return true if registration successful, false if email already exists
     */
    private boolean mockRegistration(String name, String email, String phone,
                                      String password, String role) {
        // Mock check: prevent duplicate emails
        if (email.equals("guest@saraye.com") ||
            email.equals("host@saraye.com") ||
            email.equals("both@saraye.com")) {
            return false; // Email already registered
        }

        // Simulate successful registration
        System.out.println("=== MOCK REGISTRATION ===");
        System.out.println("Name: " + name);
        System.out.println("Email: " + email);
        System.out.println("Phone: " + phone);
        System.out.println("Role: " + role);
        System.out.println("========================");

        // In real implementation, this would:
        // 1. Hash the password
        // 2. Create user in database
        // 3. Send verification email (optional)

        return true; // Registration successful
    }
}
