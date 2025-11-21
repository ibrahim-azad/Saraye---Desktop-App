package ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import models.User;
import models.Guest;
import models.Host;
import ui.utils.AlertUtil;
import ui.utils.NavigationUtil;
import ui.utils.ValidationUtil;

/**
 * LoginController - Handles login screen logic
 * UC1: Register/Login
 *
 * GRASP Patterns Applied:
 * - Controller: Handles UI events and coordinates with business logic
 * - Low Coupling: Only depends on utility classes and models
 */
public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    /**
     * Initialize method - called after FXML is loaded
     */
    @FXML
    private void initialize() {
        // Hide error label initially
        errorLabel.setVisible(false);

        // Add enter key listener to password field
        passwordField.setOnAction(event -> handleLogin());
    }

    /**
     * Handle login button click
     */
    @FXML
    private void handleLogin() {
        // Hide previous error
        errorLabel.setVisible(false);

        // Get input
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        // Validate input
        if (!validateInput(email, password)) {
            return;
        }

        // TODO: Call business logic layer (Ibrahim's UserService)
        // For now, use mock authentication
        User user = mockAuthentication(email, password);

        if (user != null) {
            // Login successful
            navigateToDashboard(user);
        } else {
            // Login failed
            showError("Invalid email or password. Please try again.");
        }
    }

    /**
     * Handle register link click
     */
    @FXML
    private void handleRegisterLink() {
        NavigationUtil.navigateTo("register.fxml");
    }

    /**
     * Validate input fields
     */
    private boolean validateInput(String email, String password) {
        // Check if fields are empty
        if (email.isEmpty() || password.isEmpty()) {
            showError("Please fill in all fields");
            return false;
        }

        // Validate email format
        if (!ValidationUtil.isValidEmail(email)) {
            showError(ValidationUtil.getEmailErrorMessage());
            return false;
        }

        return true;
    }

    /**
     * Navigate to appropriate dashboard based on user role
     */
    private void navigateToDashboard(User user) {
        String role = user.getRole().toLowerCase();

        switch (role) {
            case "guest":
                NavigationUtil.navigateTo("guest-dashboard.fxml", user);
                break;

            case "host":
                NavigationUtil.navigateTo("host-dashboard.fxml", user);
                break;

            case "both":
                // User is both guest and host, ask them which dashboard they want
                // For now, default to guest dashboard
                NavigationUtil.navigateTo("guest-dashboard.fxml", user);
                break;

            case "admin":
                // TODO: Create admin dashboard
                AlertUtil.showInfo("Admin Login", "Admin dashboard not yet implemented");
                break;

            default:
                showError("Invalid user role: " + role);
        }
    }

    /**
     * Show error message
     */
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    /**
     * MOCK AUTHENTICATION - Temporary until Ibrahim provides UserService
     * Replace this with real authentication later
     */
    private User mockAuthentication(String email, String password) {
        // Mock users for testing
        // Guest: guest@saraye.com / password123
        // Host: host@saraye.com / password123

        if (email.equals("guest@saraye.com") && password.equals("password123")) {
            return new Guest("1", "John Doe", email, password, "+92-300-1234567");
        }

        if (email.equals("host@saraye.com") && password.equals("password123")) {
            return new Host("2", "Jane Smith", email, password, "+92-300-7654321");
        }

        if (email.equals("both@saraye.com") && password.equals("password123")) {
            // For "both" role, we'll return a Guest but could also implement a separate class
            Guest user = new Guest("3", "Ali Ahmed", email, password, "+92-300-1111111");
            return user;
        }

        // Authentication failed
        return null;
    }
}
