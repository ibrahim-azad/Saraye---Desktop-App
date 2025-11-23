package ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import models.Property;
import models.User;
import ui.utils.AlertUtil;
import ui.utils.NavigationUtil;
import ui.utils.ValidationUtil;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * BookingRequestController - Handles booking request submission
 * UC4: Request Booking
 *
 * GRASP Patterns Applied:
 * - Controller: Handles booking UI events and coordinates validation
 * - Information Expert: Knows how to calculate total price and validate dates
 * - Low Coupling: Depends only on models and utilities
 */
public class BookingRequestController {

    @FXML
    private Label propertyTitleLabel;

    @FXML
    private Label propertyCityLabel;

    @FXML
    private Label pricePerNightLabel;

    @FXML
    private DatePicker checkInDatePicker;

    @FXML
    private DatePicker checkOutDatePicker;

    @FXML
    private TextField numGuestsField;

    @FXML
    private TextField numNightsField;

    @FXML
    private Label priceDetailLabel;

    @FXML
    private Label nightsDetailLabel;

    @FXML
    private Label totalPriceLabel;

    private User currentUser;
    private Property currentProperty;

    /**
     * Set user data (called by NavigationUtil)
     */
    public void setUser(Object userData) {
        if (userData instanceof User) {
            this.currentUser = (User) userData;
        }
    }

    /**
     * Set property data (called by NavigationUtil)
     */
    public void setData(Object data) {
        if (data instanceof Property) {
            this.currentProperty = (Property) data;
            displayPropertySummary();
        }
    }

    /**
     * Display property summary in UI
     */
    private void displayPropertySummary() {
        if (currentProperty == null) {
            return;
        }

        propertyTitleLabel.setText(currentProperty.getTitle());
        propertyCityLabel.setText(currentProperty.getCity() + ", " + currentProperty.getAddress());
        pricePerNightLabel.setText(String.format("%,.0f", currentProperty.getPricePerNight()));
        priceDetailLabel.setText("PKR " + String.format("%,.0f", currentProperty.getPricePerNight()));
    }

    /**
     * Handle date change - recalculate price
     */
    @FXML
    private void handleDateChange() {
        calculateTotalPrice();
    }

    /**
     * Calculate total price based on dates
     */
    private void calculateTotalPrice() {
        LocalDate checkIn = checkInDatePicker.getValue();
        LocalDate checkOut = checkOutDatePicker.getValue();

        if (checkIn == null || checkOut == null || currentProperty == null) {
            numNightsField.setText("");
            nightsDetailLabel.setText("0");
            totalPriceLabel.setText("PKR 0");
            return;
        }

        // Validate date range
        if (!ValidationUtil.isValidDateRange(checkIn, checkOut)) {
            numNightsField.setText("");
            nightsDetailLabel.setText("0");
            totalPriceLabel.setText("PKR 0");
            return;
        }

        // Calculate number of nights
        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        numNightsField.setText(String.valueOf(nights));
        nightsDetailLabel.setText(String.valueOf(nights));

        // Calculate total price
        double totalPrice = nights * currentProperty.getPricePerNight();
        totalPriceLabel.setText("PKR " + String.format("%,.0f", totalPrice));
    }

    /**
     * Handle Submit Booking button
     */
    @FXML
    private void handleSubmitBooking() {
        // Get input values
        LocalDate checkIn = checkInDatePicker.getValue();
        LocalDate checkOut = checkOutDatePicker.getValue();
        String numGuestsStr = numGuestsField.getText().trim();

        // Validate inputs
        if (checkIn == null) {
            AlertUtil.showError("Validation Error", "Please select a check-in date!");
            return;
        }

        if (checkOut == null) {
            AlertUtil.showError("Validation Error", "Please select a check-out date!");
            return;
        }

        if (!ValidationUtil.isValidDateRange(checkIn, checkOut)) {
            AlertUtil.showError("Invalid Dates", "Check-out date must be after check-in date!");
            return;
        }

        // Check if check-in is in the future
        if (checkIn.isBefore(LocalDate.now())) {
            AlertUtil.showError("Invalid Date", "Check-in date must be in the future!");
            return;
        }

        if (numGuestsStr.isEmpty()) {
            AlertUtil.showError("Validation Error", "Please enter the number of guests!");
            return;
        }

        Integer numGuests = ValidationUtil.parseIntOrNull(numGuestsStr);
        if (numGuests == null || numGuests <= 0) {
            AlertUtil.showError("Invalid Input", "Number of guests must be a positive number!");
            return;
        }

        // Check if number of guests exceeds property capacity
        if (numGuests > currentProperty.getMaxGuests()) {
            AlertUtil.showError("Capacity Exceeded",
                    "This property can accommodate maximum " + currentProperty.getMaxGuests() + " guests!\n" +
                            "You requested " + numGuests + " guests.");
            return;
        }

        // Calculate total price
        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        double totalPrice = nights * currentProperty.getPricePerNight();

        // Call business logic layer to create booking
        controllers.BookingController bookingController = controllers.BookingController.getInstance();
        String result = bookingController.createBooking(
                currentProperty.getPropertyID(),
                checkIn.toString(),
                checkOut.toString(),
                totalPrice);

        boolean success = "SUCCESS".equals(result);

        if (success) {
            AlertUtil.showSuccess(
                    "Booking Request Submitted!",
                    "Your booking request has been submitted successfully!\n\n" +
                            "Property: " + currentProperty.getTitle() + "\n" +
                            "Check-in: " + checkIn + "\n" +
                            "Check-out: " + checkOut + "\n" +
                            "Guests: " + numGuests + "\n" +
                            "Total Price: PKR " + String.format("%,.0f", totalPrice) + "\n\n" +
                            "The host will review your request and respond soon.\n" +
                            "You can check the status in 'My Bookings'.");

            // Navigate back to guest dashboard
            NavigationUtil.navigateTo("guest-dashboard.fxml", currentUser);
        } else {
            AlertUtil.showError("Booking Failed", result);
        }
    }

    /**
     * Handle Back button
     * Return to property details
     */
    @FXML
    private void handleBack() {
        NavigationUtil.navigateWithMultipleData("property-details.fxml", currentUser, currentProperty);
    }
}
