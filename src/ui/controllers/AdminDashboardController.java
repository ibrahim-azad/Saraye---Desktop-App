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

    @FXML private Label welcomeLabel;
    @FXML private Label totalUsersLabel;
    @FXML private Label totalPropertiesLabel;
    @FXML private Label totalBookingsLabel;
    @FXML private Label pendingReportsLabel;
    @FXML private Label totalReviewsLabel;
    @FXML private Label activeHostsLabel;

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
            // Get statistics from DAOs
            int totalUsers = DAOFactory.getUserDAO().getAllUsers().size();
            int totalProperties = DAOFactory.getPropertyDAO().getAllProperties().size();
            int totalBookings = DAOFactory.getBookingDAO().getAllBookings().size();
            int pendingReports = ModerationController.getInstance().getPendingReports().size();
            int totalReviews = DAOFactory.getReviewDAO().getAllReviews().size();

            // Count active hosts (users with HOST or BOTH role)
            int activeHosts = (int) DAOFactory.getUserDAO().getAllUsers().stream()
                    .filter(u -> "HOST".equalsIgnoreCase(u.getRole()) || "BOTH".equalsIgnoreCase(u.getRole()))
                    .count();

            // Update labels
            totalUsersLabel.setText(String.valueOf(totalUsers));
            totalPropertiesLabel.setText(String.valueOf(totalProperties));
            totalBookingsLabel.setText(String.valueOf(totalBookings));
            pendingReportsLabel.setText(String.valueOf(pendingReports));
            totalReviewsLabel.setText(String.valueOf(totalReviews));
            activeHostsLabel.setText(String.valueOf(activeHosts));

        } catch (Exception e) {
            e.printStackTrace();
            AlertUtil.showError("Error", "Failed to load statistics: " + e.getMessage());
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
