package ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import models.Property;
import models.User;
import ui.utils.AlertUtil;
import ui.utils.NavigationUtil;
import ui.utils.ValidationUtil;

/**
 * AddPropertyController - Handles adding new property listings
 * UC8: Manage Property Listing
 *
 * GRASP Patterns Applied:
 * - Controller: Handles add property UI events and coordinates validation
 * - Information Expert: Knows how to validate property data
 * - Low Coupling: Depends only on models and utilities
 */
public class AddPropertyController {

    @FXML
    private TextField titleField;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private TextField addressField;

    @FXML
    private TextField cityField;

    @FXML
    private TextField priceField;

    @FXML
    private TextField maxGuestsField;

    @FXML
    private TextField bedroomsField;

    @FXML
    private TextField bathroomsField;

    @FXML
    private TextField amenitiesField;

    private User currentUser;

    /**
     * Set user data (called by NavigationUtil)
     */
    public void setUser(Object userData) {
        if (userData instanceof User) {
            this.currentUser = (User) userData;
        }
    }

    /**
     * Handle Add Property button
     */
    @FXML
    private void handleAddProperty() {
        // Get input values
        String title = titleField.getText().trim();
        String description = descriptionArea.getText().trim();
        String address = addressField.getText().trim();
        String city = cityField.getText().trim();
        String priceStr = priceField.getText().trim();
        String maxGuestsStr = maxGuestsField.getText().trim();
        String bedroomsStr = bedroomsField.getText().trim();
        String bathroomsStr = bathroomsField.getText().trim();
        String amenities = amenitiesField.getText().trim();

        // Validate required fields
        if (title.isEmpty()) {
            AlertUtil.showError("Validation Error", "Please enter a property title!");
            titleField.requestFocus();
            return;
        }

        if (description.isEmpty()) {
            AlertUtil.showError("Validation Error", "Please enter a property description!");
            descriptionArea.requestFocus();
            return;
        }

        if (address.isEmpty()) {
            AlertUtil.showError("Validation Error", "Please enter the property address!");
            addressField.requestFocus();
            return;
        }

        if (city.isEmpty()) {
            AlertUtil.showError("Validation Error", "Please enter the city!");
            cityField.requestFocus();
            return;
        }

        if (priceStr.isEmpty()) {
            AlertUtil.showError("Validation Error", "Please enter the price per night!");
            priceField.requestFocus();
            return;
        }

        if (maxGuestsStr.isEmpty()) {
            AlertUtil.showError("Validation Error", "Please enter the maximum number of guests!");
            maxGuestsField.requestFocus();
            return;
        }

        if (bedroomsStr.isEmpty()) {
            AlertUtil.showError("Validation Error", "Please enter the number of bedrooms!");
            bedroomsField.requestFocus();
            return;
        }

        if (bathroomsStr.isEmpty()) {
            AlertUtil.showError("Validation Error", "Please enter the number of bathrooms!");
            bathroomsField.requestFocus();
            return;
        }

        if (amenities.isEmpty()) {
            AlertUtil.showError("Validation Error", "Please enter at least one amenity!");
            amenitiesField.requestFocus();
            return;
        }

        // Validate numeric fields
        Double price = ValidationUtil.parseDoubleOrNull(priceStr);
        if (price == null || price <= 0) {
            AlertUtil.showError("Invalid Input", "Price per night must be a positive number!");
            priceField.requestFocus();
            return;
        }

        Integer maxGuests = ValidationUtil.parseIntOrNull(maxGuestsStr);
        if (maxGuests == null || maxGuests <= 0) {
            AlertUtil.showError("Invalid Input", "Maximum guests must be a positive number!");
            maxGuestsField.requestFocus();
            return;
        }

        Integer bedrooms = ValidationUtil.parseIntOrNull(bedroomsStr);
        if (bedrooms == null || bedrooms <= 0) {
            AlertUtil.showError("Invalid Input", "Number of bedrooms must be a positive number!");
            bedroomsField.requestFocus();
            return;
        }

        Integer bathrooms = ValidationUtil.parseIntOrNull(bathroomsStr);
        if (bathrooms == null || bathrooms <= 0) {
            AlertUtil.showError("Invalid Input", "Number of bathrooms must be a positive number!");
            bathroomsField.requestFocus();
            return;
        }

        // Validate title length
        if (title.length() < 5) {
            AlertUtil.showError("Validation Error", "Property title must be at least 5 characters long!");
            titleField.requestFocus();
            return;
        }

        if (title.length() > 100) {
            AlertUtil.showError("Validation Error", "Property title must not exceed 100 characters!");
            titleField.requestFocus();
            return;
        }

        // Validate description length
        if (description.length() < 20) {
            AlertUtil.showError("Validation Error", "Description must be at least 20 characters long!");
            descriptionArea.requestFocus();
            return;
        }

        if (description.length() > 1000) {
            AlertUtil.showError("Validation Error", "Description must not exceed 1000 characters!");
            descriptionArea.requestFocus();
            return;
        }

        // TODO: Call business logic layer (Ibrahim's PropertyService)
        // For now, use mock property creation
        boolean success = mockAddProperty(title, description, address, city, price,
                                         maxGuests, bedrooms, bathrooms, amenities);

        if (success) {
            AlertUtil.showSuccess(
                "Property Added Successfully!",
                "Your property has been listed successfully!\n\n" +
                "Title: " + title + "\n" +
                "City: " + city + "\n" +
                "Price: PKR " + String.format("%,.0f", price) + " per night\n\n" +
                "Your property is now visible to guests searching in " + city + "."
            );

            // Navigate back to host dashboard
            NavigationUtil.navigateTo("host-dashboard.fxml", currentUser);
        }
    }

    /**
     * MOCK ADD PROPERTY - Replace with real PropertyService later
     */
    private boolean mockAddProperty(String title, String description, String address,
                                    String city, double price, int maxGuests,
                                    int bedrooms, int bathrooms, String amenities) {
        // In real implementation, this would call:
        // PropertyService.createProperty(currentUser.getUserId(), title, description,
        //                                address, city, price, maxGuests, bedrooms,
        //                                bathrooms, amenities);

        // Create a mock property object for demonstration
        Property newProperty = new Property(
            999, // Mock property ID
            currentUser != null ? currentUser.getUserId() : 0,
            title,
            description,
            address,
            city,
            price,
            maxGuests,
            bedrooms,
            bathrooms,
            amenities,
            "available"
        );

        System.out.println("MOCK: Property created with ID " + newProperty.getPropertyId());
        System.out.println("      Title: " + newProperty.getTitle());
        System.out.println("      City: " + newProperty.getCity());
        System.out.println("      Price: PKR " + newProperty.getPricePerNight());

        return true;
    }

    /**
     * Handle Back button
     */
    @FXML
    private void handleBack() {
        boolean confirmed = AlertUtil.showConfirmation(
            "Cancel Adding Property",
            "Are you sure you want to cancel?\n\nAll entered data will be lost."
        );

        if (confirmed) {
            NavigationUtil.navigateTo("host-dashboard.fxml", currentUser);
        }
    }
}
