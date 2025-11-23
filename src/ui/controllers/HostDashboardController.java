package ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import models.User;
import ui.utils.AlertUtil;
import ui.utils.NavigationUtil;

/**
 * HostDashboardController - Main dashboard for hosts
 * Shows actions available to hosts (add property, manage bookings)
 *
 * GRASP Patterns Applied:
 * - Controller: Handles host dashboard UI events
 * - Low Coupling: Minimal dependencies
 * - High Cohesion: Focused on host-specific actions
 */
public class HostDashboardController {

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
     * Handle Add Property button
     * UC8: Manage Property Listing
     */
    @FXML
    private void handleAddProperty() {
        NavigationUtil.navigateTo("add-property.fxml", currentUser);
    }

    /**
     * Handle My Properties button
     * Shows list of host's properties
     */
    @FXML
    private void handleMyProperties() {
        NavigationUtil.navigateTo("host-properties.fxml", currentUser);
    }

    /**
     * Handle Booking Requests button
     * UC6: Host Approve/Decline Booking
     */
    @FXML
    private void handleBookingRequests() {
        NavigationUtil.navigateTo("host-bookings.fxml", currentUser);
    }

    /**
     * Handle Switch to Guest button
     * Switches user to guest mode
     */
    @FXML
    private void handleSwitchToGuest() {
        boolean confirmed = AlertUtil.showConfirmation(
                "Switch to Guest Mode",
                "Would you like to switch to Guest mode to browse properties?\n\n" +
                        "You can switch back to Host mode anytime.");

        if (confirmed) {
            // Navigate to guest dashboard
            NavigationUtil.navigateTo("guest-dashboard.fxml", currentUser);
        }
    }

    /**
     * Handle Moderation button
     * Navigate to admin moderation screen
     * UC12: Report/Dispute
     */
    @FXML
    private void handleModeration() {
        NavigationUtil.navigateTo("admin-moderation.fxml", currentUser);
    }

    /**
     * Handle My Profile button
     * UC9: Manage Account
     */
    @FXML
    private void handleProfile() {
        NavigationUtil.navigateTo("profile.fxml", currentUser);
    }

    /**
     * Handle Logout button
     */
    @FXML
    private void handleLogout() {
        boolean confirmed = AlertUtil.showConfirmation(
                "Logout",
                "Are you sure you want to logout?");

        if (confirmed) {
            NavigationUtil.navigateTo("login.fxml");
        }
    }
}
