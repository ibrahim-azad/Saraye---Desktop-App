package ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import models.Booking;
import models.User;
import ui.utils.AlertUtil;
import ui.utils.NavigationUtil;

import java.util.List;

/**
 * GuestBookingsController - Displays guest's bookings with cancel functionality
 * UC7: Cancel Booking
 *
 * GRASP Patterns Applied:
 * - Controller: Handles booking display and cancellation UI events
 * - Low Coupling: Depends only on models and utilities
 */
public class GuestBookingsController {

    @FXML
    private TableView<Booking> bookingsTable;

    @FXML
    private TableColumn<Booking, String> bookingIdColumn;

    @FXML
    private TableColumn<Booking, String> propertyIdColumn;

    @FXML
    private TableColumn<Booking, String> checkInColumn;

    @FXML
    private TableColumn<Booking, String> checkOutColumn;

    @FXML
    private TableColumn<Booking, Double> totalPriceColumn;

    @FXML
    private TableColumn<Booking, String> statusColumn;

    @FXML
    private TableColumn<Booking, Void> actionColumn;

    @FXML
    private Label bookingCountLabel;

    private User currentUser;

    /**
     * Set user data (called by NavigationUtil)
     */
    public void setUser(Object userData) {
        if (userData instanceof User) {
            this.currentUser = (User) userData;
            loadBookings();
        }
    }

    /**
     * Initialize method - called after FXML is loaded
     */
    @FXML
    private void initialize() {
        // Setup table columns
        bookingIdColumn.setCellValueFactory(new PropertyValueFactory<>("bookingID"));
        propertyIdColumn.setCellValueFactory(new PropertyValueFactory<>("propertyID"));
        checkInColumn.setCellValueFactory(new PropertyValueFactory<>("checkInDate"));
        checkOutColumn.setCellValueFactory(new PropertyValueFactory<>("checkOutDate"));
        totalPriceColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Add action buttons
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button cancelBtn = new Button("Cancel");
            private final Button payBtn = new Button("💳 Pay Now");
            private final Button reviewBtn = new Button("⭐ Review");

            {
                cancelBtn.setOnAction(event -> {
                    Booking booking = getTableView().getItems().get(getIndex());
                    handleCancelBooking(booking);
                });
                cancelBtn.setStyle(
                        "-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-padding: 5 15; -fx-background-radius: 5;");

                payBtn.setOnAction(event -> {
                    Booking booking = getTableView().getItems().get(getIndex());
                    handlePayment(booking);
                });
                payBtn.setStyle(
                        "-fx-background-color: #27ae60; -fx-text-fill: white; -fx-padding: 5 15; -fx-background-radius: 5; -fx-font-weight: bold;");

                reviewBtn.setOnAction(event -> {
                    Booking booking = getTableView().getItems().get(getIndex());
                    handleLeaveReview(booking);
                });
                reviewBtn.setStyle(
                        "-fx-background-color: #f39c12; -fx-text-fill: white; -fx-padding: 5 15; -fx-background-radius: 5;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Booking booking = getTableView().getItems().get(getIndex());

                    // Show cancel button for PENDING bookings
                    if ("PENDING".equals(booking.getStatus())) {
                        setGraphic(cancelBtn);
                    }
                    // Show pay button for APPROVED bookings
                    else if ("APPROVED".equals(booking.getStatus())) {
                        setGraphic(payBtn);
                    }
                    // Show review button for CONFIRMED bookings
                    else if ("CONFIRMED".equals(booking.getStatus())) {
                        setGraphic(reviewBtn);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });
    }

    /**
     * Load bookings for current guest from database
     */
    private void loadBookings() {
        if (currentUser == null) {
            return;
        }

        try {
            // Get bookings from business logic layer
            controllers.BookingController bookingController = controllers.BookingController.getInstance();
            List<Booking> bookings = bookingController.getGuestBookings(currentUser.getUserId());

            if (bookings != null) {
                bookingsTable.getItems().clear();
                bookingsTable.getItems().addAll(bookings);
                bookingCountLabel.setText(bookings.size() + " booking(s) found");
            } else {
                bookingCountLabel.setText("0 bookings found");
            }

        } catch (Exception e) {
            e.printStackTrace();
            AlertUtil.showError("Error", "Failed to load bookings: " + e.getMessage());
        }
    }

    /**
     * Handle cancel booking button click
     */
    private void handleCancelBooking(Booking booking) {
        // Confirm cancellation
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Cancel Booking");
        confirmDialog.setHeaderText("Are you sure you want to cancel this booking?");
        confirmDialog.setContentText(
                "Booking ID: " + booking.getBookingID() + "\n" +
                        "Property: " + booking.getPropertyID() + "\n" +
                        "Check-in: " + booking.getCheckInDate() + "\n" +
                        "Check-out: " + booking.getCheckOutDate() + "\n" +
                        "Total Price: PKR " + String.format("%,.0f", booking.getTotalPrice()) + "\n\n" +
                        "This action cannot be undone.");

        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                performCancellation(booking);
            }
        });
    }

    /**
     * Perform the actual cancellation via business logic
     */
    private void performCancellation(Booking booking) {
        try {
            controllers.BookingController bookingController = controllers.BookingController.getInstance();
            String result = bookingController.cancelBooking(booking.getBookingID());

            if ("SUCCESS".equals(result)) {
                AlertUtil.showSuccess(
                        "Booking Cancelled",
                        "Your booking has been cancelled successfully!\n\n" +
                                "Booking ID: " + booking.getBookingID());

                // Reload bookings to reflect changes
                loadBookings();
            } else {
                AlertUtil.showError("Cancellation Failed", result);
            }

        } catch (Exception e) {
            e.printStackTrace();
            AlertUtil.showError("Error", "Failed to cancel booking: " + e.getMessage());
        }
    }

    /**
     * Handle Leave Review button
     */
    private void handleLeaveReview(Booking booking) {
        // Navigate to leave review screen
        NavigationUtil.navigateWithMultipleData("leave-review.fxml", currentUser, booking);
    }

    /**
     * Handle Payment button - Navigate to payment screen
     */
    private void handlePayment(Booking booking) {
        try {
            // Get property details for the booking
            controllers.PropertyController propertyController = controllers.PropertyController.getInstance();
            models.Property property = propertyController.getPropertyById(booking.getPropertyID());

            if (property != null) {
                // Navigate to payment screen with booking and property data
                Object[] paymentData = new Object[] { booking, property };
                NavigationUtil.navigateWithMultipleData("payment.fxml", currentUser, paymentData);
            } else {
                AlertUtil.showError("Error", "Property details not found for this booking.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtil.showError("Error", "Failed to load payment screen: " + e.getMessage());
        }
    }

    /**
     * Handle Refresh button
     */
    @FXML
    private void handleRefresh() {
        loadBookings();
        AlertUtil.showInfo("Refreshed", "Bookings list has been refreshed.");
    }

    /**
     * Handle Back button
     */
    @FXML
    private void handleBack() {
        NavigationUtil.navigateTo("guest-dashboard.fxml", currentUser);
    }
}
