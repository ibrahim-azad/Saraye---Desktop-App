package ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import models.Property;
import models.User;
import models.Address;
import models.Amenity;
import databases.PropertyDAO;
import ui.utils.AlertUtil;
import ui.utils.NavigationUtil;
import ui.utils.ValidationUtil;
import java.util.ArrayList;
import java.util.List;

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
    private VBox amenitiesContainer;

    private User currentUser;
    private List<CheckBox> amenityCheckBoxes = new ArrayList<>();
    private PropertyDAO propertyDAO = databases.DAOFactory.getPropertyDAO();

    /**
     * Initialize method - loads amenities from database
     */
    @FXML
    private void initialize() {
        loadAmenities();
    }

    /**
     * Load amenities from database and create checkboxes
     */
    private void loadAmenities() {
        List<Amenity> amenities = propertyDAO.getAllAmenities();

        for (Amenity amenity : amenities) {
            CheckBox checkBox = new CheckBox(amenity.getName());
            checkBox.setUserData(amenity); // Store amenity object
            checkBox.setStyle("-fx-font-size: 14px;");
            amenityCheckBoxes.add(checkBox);
            amenitiesContainer.getChildren().add(checkBox);
        }
    }

    /**
     * Get selected amenities from checkboxes
     */
    private List<Amenity> getSelectedAmenities() {
        List<Amenity> selected = new ArrayList<>();
        for (CheckBox checkBox : amenityCheckBoxes) {
            if (checkBox.isSelected()) {
                selected.add((Amenity) checkBox.getUserData());
            }
        }
        return selected;
    }

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

        // Check if at least one amenity is selected
        List<Amenity> selectedAmenities = getSelectedAmenities();
        if (selectedAmenities.isEmpty()) {
            AlertUtil.showError("Validation Error", "Please select at least one amenity!");
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

        // Generate proper IDs
        String addressID = propertyDAO.generateAddressID();
        String propertyID = propertyDAO.generatePropertyID();

        // Create Address object
        Address propertyAddress = new Address(
                addressID,
                address,
                city,
                "Pakistan", // Default country
                "" // Zip code (can be added to form later)
        );

        // Create Property object
        Property newProperty = new Property(
                propertyID,
                currentUser != null ? currentUser.getUserId() : "0", // Host ID
                title,
                price,
                propertyAddress);

        // Set additional fields
        newProperty.setDescription(description);
        newProperty.setMaxGuests(maxGuests);
        newProperty.setBedrooms(bedrooms);
        newProperty.setBathrooms(bathrooms);
        newProperty.setStatus("available");
        newProperty.setAmenities(selectedAmenities);

        // Save property to database
        boolean success = propertyDAO.saveProperty(newProperty);

        if (success) {
            AlertUtil.showSuccess(
                    "Property Added Successfully!",
                    "Your property has been listed successfully!\n\n" +
                            "Property ID: " + propertyID + "\n" +
                            "Title: " + title + "\n" +
                            "City: " + city + "\n" +
                            "Price: PKR " + price + "/night\n\n" +
                            "Your property is now visible to guests searching in " + city + ".");

            handleBack(); // Return to dashboard
        } else {
            AlertUtil.showError("Error", "Failed to add property. Please try again.");
        }
    }

    /**
     * Handle Back/Cancel button
     */
    @FXML
    private void handleBack() {
        // Navigate back to host dashboard
        NavigationUtil.navigateTo("host-dashboard.fxml", currentUser);
    }
}
