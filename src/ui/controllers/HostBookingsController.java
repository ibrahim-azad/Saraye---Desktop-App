package ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import models.Booking;
import models.User;
import databases.BookingDAO;
import ui.utils.AlertUtil;
import ui.utils.NavigationUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * HostBookingsController - Manages booking requests for host's properties
 * UC6: Host Approve/Decline Booking
 *
 * GRASP Patterns Applied:
 * - Controller: Handles booking management UI events
 * - Information Expert: Knows how to filter and display bookings
 * - Low Coupling: Depends only on models and utilities
 */
public class HostBookingsController {

    @FXML
    private TableView<Booking> bookingsTable;

    @FXML
    private TableColumn<Booking, String> guestNameColumn;

    @FXML
    private TableColumn<Booking, String> propertyColumn;

    @FXML
    private TableColumn<Booking, LocalDate> checkInColumn;

    @FXML
    private TableColumn<Booking, LocalDate> checkOutColumn;

    @FXML
    private TableColumn<Booking, Integer> guestsColumn;

    @FXML
    private TableColumn<Booking, Double> totalPriceColumn;

    @FXML
    private TableColumn<Booking, String> statusColumn;

    @FXML
    private TableColumn<Booking, Void> actionsColumn;

    @FXML
    private Label statusLabel;

    @FXML
    private Label countLabel;

    private User currentUser;
    private List<Booking> allBookings;
    private String currentFilter = "all";

    /**
     * Initialize method - called after FXML is loaded
     */
    @FXML
    private void initialize() {
        // Setup table columns
        guestNameColumn.setCellValueFactory(new PropertyValueFactory<>("guestName"));
        propertyColumn.setCellValueFactory(new PropertyValueFactory<>("propertyTitle"));
        checkInColumn.setCellValueFactory(new PropertyValueFactory<>("checkInDate"));
        checkOutColumn.setCellValueFactory(new PropertyValueFactory<>("checkOutDate"));
        guestsColumn.setCellValueFactory(new PropertyValueFactory<>("numGuests"));
        totalPriceColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Add action buttons to each row
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button approveBtn = new Button("✓ Approve");
            private final Button declineBtn = new Button("✗ Decline");
            private final HBox container = new HBox(5, approveBtn, declineBtn);

            {
                approveBtn.setStyle(
                        "-fx-background-color: #27ae60; -fx-text-fill: white; -fx-padding: 5 10; -fx-background-radius: 3;");
                declineBtn.setStyle(
                        "-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-padding: 5 10; -fx-background-radius: 3;");

                approveBtn.setOnAction(event -> {
                    Booking booking = getTableView().getItems().get(getIndex());
                    handleApproveBooking(booking);
                });

                declineBtn.setOnAction(event -> {
                    Booking booking = getTableView().getItems().get(getIndex());
                    handleDeclineBooking(booking);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Booking booking = getTableView().getItems().get(getIndex());
                    // Only show buttons for pending bookings
                    if ("pending".equalsIgnoreCase(booking.getStatus())) {
                        setGraphic(container);
                    } else {
                        Label statusLabel = new Label(booking.getStatus().toUpperCase());
                        if ("approved".equalsIgnoreCase(booking.getStatus())) {
                            statusLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                        } else if ("declined".equalsIgnoreCase(booking.getStatus())) {
                            statusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                        }
                        setGraphic(statusLabel);
                    }
                }
            }
        });

        // Don't load bookings here - wait for setUser to be called
    }

    /**
     * Set user data
     */
    public void setUser(Object userData) {
        if (userData instanceof User) {
            this.currentUser = (User) userData;
            // Load bookings AFTER user is set
            loadBookingsFromDatabase();
        }
    }

    /**
     * Handle Show All filter
     */
    @FXML
    private void handleShowAll() {
        currentFilter = "all";
        filterBookings();
    }

    /**
     * Handle Show Pending filter
     */
    @FXML
    private void handleShowPending() {
        currentFilter = "pending";
        filterBookings();
    }

    /**
     * Handle Show Approved filter
     */
    @FXML
    private void handleShowApproved() {
        currentFilter = "approved";
        filterBookings();
    }

    /**
     * Handle Show Declined filter
     */
    @FXML
    private void handleShowDeclined() {
        currentFilter = "declined";
        filterBookings();
    }

    /**
     * Filter bookings based on current filter
     */
    private void filterBookings() {
        List<Booking> filtered;

        if ("all".equals(currentFilter)) {
            filtered = new ArrayList<>(allBookings);
            statusLabel.setText("Showing: All Bookings");
        } else {
            filtered = allBookings.stream()
                    .filter(b -> currentFilter.equalsIgnoreCase(b.getStatus()))
                    .collect(Collectors.toList());
            statusLabel.setText("Showing: " + currentFilter.substring(0, 1).toUpperCase() + currentFilter.substring(1)
                    + " Bookings");
        }

        bookingsTable.getItems().clear();
        bookingsTable.getItems().addAll(filtered);
        countLabel.setText("(" + filtered.size() + " bookings)");
    }

    /**
     * Handle Approve Booking
     */
    private void handleApproveBooking(Booking booking) {
        boolean confirmed = AlertUtil.showConfirmation(
                "Approve Booking",
                "Are you sure you want to approve this booking?\n\n" +
                        "Guest: " + booking.getGuestName() + "\n" +
                        "Property: " + booking.getPropertyTitle() + "\n" +
                        "Check-in: " + booking.getCheckInDate() + "\n" +
                        "Check-out: " + booking.getCheckOutDate() + "\n" +
                        "Total: PKR " + String.format("%,.0f", booking.getTotalPrice()));

        if (confirmed) {
            // Update booking status in database
            BookingDAO bookingDAO = new BookingDAO();
            boolean success = bookingDAO.updateStatus(booking.getBookingID(), "APPROVED");

            if (success) {
                booking.setStatus("approved");
                AlertUtil.showSuccess("Booking Approved", "The booking has been approved successfully!");
                System.out.println("Booking " + booking.getBookingID() + " approved in database");
            } else {
                AlertUtil.showError("Error", "Failed to approve booking in database");
            }

            // Refresh the table
            loadBookingsFromDatabase();
            filterBookings();
        }
    }

    /**
     * Handle Decline Booking
     */
    private void handleDeclineBooking(Booking booking) {
        boolean confirmed = AlertUtil.showConfirmation(
                "Decline Booking",
                "Are you sure you want to decline this booking?\n\n" +
                        "Guest: " + booking.getGuestName() + "\n" +
                        "Property: " + booking.getPropertyTitle() + "\n" +
                        "Check-in: " + booking.getCheckInDate() + "\n" +
                        "Check-out: " + booking.getCheckOutDate());

        if (confirmed) {
            // Update booking status in database
            BookingDAO bookingDAO = new BookingDAO();
            boolean success = bookingDAO.updateStatus(booking.getBookingID(), "DECLINED");

            if (success) {
                booking.setStatus("declined");
                AlertUtil.showInfo("Booking Declined", "The booking has been declined.");
                System.out.println("Booking " + booking.getBookingID() + " declined in database");
            } else {
                AlertUtil.showError("Error", "Failed to decline booking in database");
            }

            // Refresh the table
            loadBookingsFromDatabase();
            filterBookings();
        }
    }

    /**
     * Load bookings from database
     */
    private void loadBookingsFromDatabase() {
        if (currentUser != null) {
            BookingDAO bookingDAO = new BookingDAO();
            allBookings = bookingDAO.getBookingsByHost(currentUser.getUserId());
        } else {
            allBookings = new ArrayList<>();
        }
        filterBookings();
    }

    /**
     * Handle Back button
     */
    @FXML
    private void handleBack() {
        NavigationUtil.navigateTo("host-dashboard.fxml", currentUser);
    }
}
