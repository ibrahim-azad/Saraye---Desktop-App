package ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import models.Property;
import models.User;
import models.Amenity;
import ui.utils.NavigationUtil;
import java.util.stream.Collectors;

/**
 * PropertyDetailsController - Displays detailed property information
 * UC3: View Property Details
 *
 * GRASP Patterns Applied:
 * - Controller: Handles property details UI events
 * - Information Expert: Knows how to display property information
 * - Low Coupling: Depends only on models and utilities
 */
public class PropertyDetailsController {

    @FXML
    private Label titleLabel;

    @FXML
    private Label addressLabel;

    @FXML
    private Label priceLabel;

    @FXML
    private Label guestsLabel;

    @FXML
    private Label bedroomsLabel;

    @FXML
    private Label bathroomsLabel;

    @FXML
    private Label descriptionLabel;

    @FXML
    private Label amenitiesLabel;

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
            displayPropertyDetails();
        }
    }

    /**
     * Display property details in UI
     */
    private void displayPropertyDetails() {
        if (currentProperty == null) {
            return;
        }

        titleLabel.setText(currentProperty.getTitle());
        addressLabel.setText(currentProperty.getAddress().getFullAddress());
        priceLabel.setText(String.format("%,.0f", currentProperty.getPricePerNight()));
        guestsLabel.setText(String.valueOf(currentProperty.getMaxGuests()));
        bedroomsLabel.setText(String.valueOf(currentProperty.getBedrooms()));
        bathroomsLabel.setText(String.valueOf(currentProperty.getBathrooms()));
        descriptionLabel.setText(currentProperty.getDescription());

        // Convert amenities list to comma-separated string
        if (currentProperty.getAmenities() != null && !currentProperty.getAmenities().isEmpty()) {
            String amenitiesStr = currentProperty.getAmenities().stream()
                .map(Amenity::getName)
                .collect(Collectors.joining(", "));
            amenitiesLabel.setText(amenitiesStr);
        } else {
            amenitiesLabel.setText("No amenities listed");
        }
    }

    /**
     * Handle Book Now button
     * Navigate to booking request screen
     */
    @FXML
    private void handleBookNow() {
        // Navigate to booking request screen with user and property data
        NavigationUtil.navigateWithMultipleData("booking-request.fxml", currentUser, currentProperty);
    }

    /**
     * Handle Back button
     * Return to search properties
     */
    @FXML
    private void handleBack() {
        NavigationUtil.navigateTo("search-properties.fxml", currentUser);
    }
}
