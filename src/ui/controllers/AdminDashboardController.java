package ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import models.User;
import controllers.ModerationController;
import databases.DAOFactory;
import ui.utils.AlertUtil;
import ui.utils.NavigationUtil;

/**
 * AdminDashboardController - Admin control panel with metrics and statistics
 */
public class AdminDashboardController {

    @FXML
    private Label welcomeLabel;
    @FXML
    private Label totalUsersLabel;
    @FXML
    private Label totalPropertiesLabel;
    @FXML
    private Label totalBookingsLabel;
    @FXML
    private Label pendingReportsLabel;
    @FXML
    private Label totalReviewsLabel;
    @FXML
    private Label activeHostsLabel;

    private User currentUser;

    public void setUser(Object userData) {
        if (userData instanceof User) {
            this.currentUser = (User) userData;
            updateWelcomeMessage();
            loadStatistics();
        }
    }

    private void updateWelcomeMessage() {
        if (currentUser != null) {
            welcomeLabel.setText("Welcome, " + currentUser.getName() + "!");
        }
    }

    private void loadStatistics() {
        try {
            // Get statistics from DAOs with proper error handling
            int totalUsers = 0;
            int totalProperties = 0;
            int totalBookings = 0;
            int pendingReports = 0;
            int totalReviews = 0;
            int activeHosts = 0;

            try {
                totalUsers = DAOFactory.getUserDAO().getAllUsers().size();
            } catch (Exception e) {
                System.out.println("Error loading users: " + e.getMessage());
            }

            try {
                totalProperties = DAOFactory.getPropertyDAO().getAllProperties().size();
            } catch (Exception e) {
                System.out.println("Error loading properties: " + e.getMessage());
            }

            try {
                totalBookings = DAOFactory.getBookingDAO().getAllBookings().size();
            } catch (Exception e) {
                System.out.println("Error loading bookings: " + e.getMessage());
            }

            try {
                pendingReports = ModerationController.getInstance().getPendingReports().size();
            } catch (Exception e) {
                System.out.println("Error loading reports: " + e.getMessage());
            }

            try {
                totalReviews = DAOFactory.getReviewDAO().getAllReviews().size();
            } catch (Exception e) {
                System.out.println("Error loading reviews: " + e.getMessage());
            }

            try {
                // Count active hosts (users with HOST or BOTH role)
                activeHosts = (int) DAOFactory.getUserDAO().getAllUsers().stream()
                        .filter(u -> u != null && u.getRole() != null &&
                                ("HOST".equalsIgnoreCase(u.getRole()) || "BOTH".equalsIgnoreCase(u.getRole())))
                        .count();
            } catch (Exception e) {
                System.out.println("Error counting hosts: " + e.getMessage());
            }

            // Update labels with actual values
            totalUsersLabel.setText(String.valueOf(totalUsers));
            totalPropertiesLabel.setText(String.valueOf(totalProperties));
            totalBookingsLabel.setText(String.valueOf(totalBookings));
            pendingReportsLabel.setText(String.valueOf(pendingReports));
            totalReviewsLabel.setText(String.valueOf(totalReviews));
            activeHostsLabel.setText(String.valueOf(activeHosts));

        } catch (Exception e) {
            e.printStackTrace();
            // Set default values if complete failure
            totalUsersLabel.setText("0");
            totalPropertiesLabel.setText("0");
            totalBookingsLabel.setText("0");
            pendingReportsLabel.setText("0");
            totalReviewsLabel.setText("0");
            activeHostsLabel.setText("0");

            AlertUtil.showError("Error", "Failed to load statistics. Please check database connection.");
        }
    }

    @FXML
    private void handleViewReports() {
        NavigationUtil.navigateTo("admin-moderation.fxml", currentUser);
    }

    @FXML
    private void handleRefresh() {
        loadStatistics();
        AlertUtil.showSuccess("Refreshed", "Statistics updated successfully!");
    }

    @FXML
    private void handleProfile() {
        NavigationUtil.navigateTo("profile.fxml", currentUser);
    }

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
