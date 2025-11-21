package ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import models.User;
import ui.utils.AlertUtil;
import ui.utils.NavigationUtil;

/**
 * GuestDashboardController - Main dashboard for guests
 * Shows actions available to guests (search, bookings, profile)
 *
 * GRASP Patterns Applied:
 * - Controller: Handles guest dashboard UI events
 * - Low Coupling: Minimal dependencies
 * - High Cohesion: Focused on guest-specific actions
 */
public class GuestDashboardController {

    @FXML
    private Label welcomeLabel;

    private User currentUser;

    /**
     * Set user data (called by NavigationUtil after loading)
     */
    public void setUser(Object userData) {
        if (userData instanceof User) {
            this.currentUser = (User) userData;
            updateWelcomeMessage();
        }
    }

    /**
     * Update welcome message with user's name
     */
    private void updateWelcomeMessage() {
        if (currentUser != null) {
            welcomeLabel.setText("Welcome, " + currentUser.getName() + "!");
        }
    }

    /**
     * Handle Search Properties button
     * UC2: Search Properties
     */
    @FXML
    private void handleSearchProperties() {
        NavigationUtil.navigateTo("search-properties.fxml", currentUser);
    }

    /**
     * Handle My Bookings button
     * UC7: View/Cancel Bookings
     */
    @FXML
    private void handleMyBookings() {
        // TODO: Create My Bookings screen
        AlertUtil.showInfo(
            "My Bookings",
            "This feature will show all your booking requests and reservations.\n\n" +
            "Status: Coming soon!"
        );
    }

    /**
     * Handle View Profile button
     * UC9: Manage Account
     */
    @FXML
    private void handleViewProfile() {
        // Navigate to profile screen
        NavigationUtil.navigateTo("profile.fxml", currentUser);
    }

    /**
     * Handle Become a Host button
     * Switches user to host mode
     */
    @FXML
    private void handleBecomeHost() {
        boolean confirmed = AlertUtil.showConfirmation(
            "Become a Host",
            "Would you like to switch to Host mode to list your properties?\n\n" +
            "You can switch back to Guest mode anytime."
        );

        if (confirmed) {
            // Update user role to 'both' (guest + host)
            if (currentUser != null) {
                currentUser.setRole("both");
            }

            // Navigate to host dashboard
            NavigationUtil.navigateTo("host-dashboard.fxml", currentUser);
        }
    }

    /**
     * Handle Logout button
     */
    @FXML
    private void handleLogout() {
        boolean confirmed = AlertUtil.showConfirmation(
            "Logout",
            "Are you sure you want to logout?"
        );

        if (confirmed) {
            NavigationUtil.navigateTo("login.fxml");
        }
    }
}
