package ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import models.Booking;
import models.User;
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
                approveBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-padding: 5 10; -fx-background-radius: 3;");
                declineBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-padding: 5 10; -fx-background-radius: 3;");

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

        // Load mock bookings
        loadMockBookings();
    }

    /**
     * Set user data
     */
    public void setUser(Object userData) {
        if (userData instanceof User) {
            this.currentUser = (User) userData;
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
            statusLabel.setText("Showing: " + currentFilter.substring(0, 1).toUpperCase() + currentFilter.substring(1) + " Bookings");
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
            "Total: PKR " + String.format("%,.0f", booking.getTotalPrice())
        );

        if (confirmed) {
            // TODO: Call business logic layer (Ibrahim's BookingService)
            // For now, use mock approval
            mockApproveBooking(booking);

            AlertUtil.showSuccess("Booking Approved", "The booking has been approved successfully!");

            // Refresh the table
            loadMockBookings();
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
            "Check-out: " + booking.getCheckOutDate()
        );

        if (confirmed) {
            // TODO: Call business logic layer (Ibrahim's BookingService)
            // For now, use mock decline
            mockDeclineBooking(booking);

            AlertUtil.showInfo("Booking Declined", "The booking has been declined.");

            // Refresh the table
            loadMockBookings();
            filterBookings();
        }
    }

    /**
     * MOCK APPROVE - Replace with real BookingService later
     */
    private void mockApproveBooking(Booking booking) {
        // In real implementation: BookingService.approveBooking(booking.getBookingId());
        booking.setStatus("approved");
        System.out.println("MOCK: Booking " + booking.getBookingID() + " approved");
    }

    /**
     * MOCK DECLINE - Replace with real BookingService later
     */
    private void mockDeclineBooking(Booking booking) {
        // In real implementation: BookingService.declineBooking(booking.getBookingId());
        booking.setStatus("declined");
        System.out.println("MOCK: Booking " + booking.getBookingID() + " declined");
    }

    /**
     * Load mock bookings for testing
     */
    private void loadMockBookings() {
        allBookings = getMockBookings();
        filterBookings();
    }

    /**
     * Get mock bookings for testing
     */
    private List<Booking> getMockBookings() {
        List<Booking> bookings = new ArrayList<>();

        Booking b1 = new Booking("1", "101", "1",
                LocalDate.of(2025, 12, 1), LocalDate.of(2025, 12, 5),
                60000, "pending");
        b1.setNumGuests(4);
        b1.setGuestName("Ali Khan");
        b1.setPropertyTitle("Luxury Apartment in DHA");
        bookings.add(b1);

        Booking b2 = new Booking("2", "102", "2",
                LocalDate.of(2025, 12, 10), LocalDate.of(2025, 12, 12),
                16000, "pending");
        b2.setNumGuests(2);
        b2.setGuestName("Sara Ahmed");
        b2.setPropertyTitle("Cozy Studio in Gulberg");
        bookings.add(b2);

        Booking b3 = new Booking("3", "103", "1",
                LocalDate.of(2025, 11, 25), LocalDate.of(2025, 11, 28),
                45000, "approved");
        b3.setNumGuests(6);
        b3.setGuestName("Usman Tariq");
        b3.setPropertyTitle("Luxury Apartment in DHA");
        bookings.add(b3);

        Booking b4 = new Booking("4", "104", "2",
                LocalDate.of(2025, 11, 20), LocalDate.of(2025, 11, 22),
                16000, "declined");
        b4.setNumGuests(2);
        b4.setGuestName("Ayesha Malik");
        b4.setPropertyTitle("Cozy Studio in Gulberg");
        bookings.add(b4);

        Booking b5 = new Booking("5", "105", "5",
                LocalDate.of(2025, 12, 15), LocalDate.of(2025, 12, 20),
                50000, "pending");
        b5.setNumGuests(4);
        b5.setGuestName("Hassan Raza");
        b5.setPropertyTitle("Modern Flat in Johar");
        bookings.add(b5);

        return bookings;
    }

    /**
     * Handle Back button
     */
    @FXML
    private void handleBack() {
        NavigationUtil.navigateTo("host-dashboard.fxml", currentUser);
    }
}
