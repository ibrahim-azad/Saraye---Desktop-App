package ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import models.Property;
import models.User;
import models.Address;
import ui.utils.AlertUtil;
import ui.utils.NavigationUtil;
import ui.utils.ValidationUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * SearchPropertiesController - Handles property search functionality
 * UC2: Search Properties
 *
 * GRASP Patterns Applied:
 * - Controller: Handles search UI events and coordinates with business logic
 * - Information Expert: Knows how to filter and display properties
 * - Low Coupling: Depends only on models and utilities
 */
public class SearchPropertiesController {

    @FXML
    private TextField cityField;

    @FXML
    private DatePicker checkInDatePicker;

    @FXML
    private DatePicker checkOutDatePicker;

    @FXML
    private TextField numGuestsField;

    @FXML
    private TextField minPriceField;

    @FXML
    private TextField maxPriceField;

    @FXML
    private TableView<Property> resultsTable;

    @FXML
    private TableColumn<Property, String> titleColumn;

    @FXML
    private TableColumn<Property, String> cityColumn;

    @FXML
    private TableColumn<Property, Double> priceColumn;

    @FXML
    private TableColumn<Property, Integer> guestsColumn;

    @FXML
    private TableColumn<Property, Integer> bedroomsColumn;

    @FXML
    private TableColumn<Property, Void> actionColumn;

    @FXML
    private Label resultsCountLabel;

    private User currentUser;

    /**
     * Initialize method - called after FXML is loaded
     */
    @FXML
    private void initialize() {
        // Setup table columns
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        cityColumn.setCellValueFactory(new PropertyValueFactory<>("city"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("pricePerNight"));
        guestsColumn.setCellValueFactory(new PropertyValueFactory<>("maxGuests"));
        bedroomsColumn.setCellValueFactory(new PropertyValueFactory<>("bedrooms"));

        // Add "View Details" button to each row
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button viewBtn = new Button("View Details");

            {
                viewBtn.setOnAction(event -> {
                    Property property = getTableView().getItems().get(getIndex());
                    handleViewDetails(property);
                });
                viewBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 5 15; -fx-background-radius: 5;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(viewBtn);
                }
            }
        });

        // Load mock properties on initialization
        loadMockProperties();
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
     * Handle Search button
     */
    @FXML
    private void handleSearch() {
        // Get search criteria
        String city = cityField.getText().trim();
        LocalDate checkIn = checkInDatePicker.getValue();
        LocalDate checkOut = checkOutDatePicker.getValue();
        String numGuestsStr = numGuestsField.getText().trim();
        String minPriceStr = minPriceField.getText().trim();
        String maxPriceStr = maxPriceField.getText().trim();

        // Validate inputs
        if (city.isEmpty()) {
            AlertUtil.showError("Validation Error", "Please enter a city to search!");
            return;
        }

        // Validate dates if provided
        if (checkIn != null && checkOut != null) {
            if (!ValidationUtil.isValidDateRange(checkIn, checkOut)) {
                AlertUtil.showError("Invalid Dates", "Check-out date must be after check-in date!");
                return;
            }
        }

        // TODO: Call business logic layer (Ibrahim's PropertyService)
        // For now, use mock search
        List<Property> results = mockSearch(city, checkIn, checkOut, numGuestsStr, minPriceStr, maxPriceStr);

        // Display results
        resultsTable.getItems().clear();
        resultsTable.getItems().addAll(results);

        // Update count
        resultsCountLabel.setText("(" + results.size() + " properties found)");

        if (results.isEmpty()) {
            AlertUtil.showInfo("No Results", "No properties found matching your criteria.\nTry adjusting your filters.");
        }
    }

    /**
     * Handle Clear Filters button
     */
    @FXML
    private void handleClearFilters() {
        cityField.clear();
        checkInDatePicker.setValue(null);
        checkOutDatePicker.setValue(null);
        numGuestsField.clear();
        minPriceField.clear();
        maxPriceField.clear();

        // Reload all properties
        loadMockProperties();
    }

    /**
     * Handle View Details button for a property
     */
    private void handleViewDetails(Property property) {
        // Navigate to property details screen
        NavigationUtil.navigateWithMultipleData("property-details.fxml", currentUser, property);
    }

    /**
     * Handle Back button
     */
    @FXML
    private void handleBack() {
        NavigationUtil.navigateTo("guest-dashboard.fxml", currentUser);
    }

    /**
     * Load mock properties for initial display
     */
    private void loadMockProperties() {
        List<Property> mockProperties = getMockProperties();
        resultsTable.getItems().clear();
        resultsTable.getItems().addAll(mockProperties);
        resultsCountLabel.setText("(" + mockProperties.size() + " properties found)");
    }

    /**
     * MOCK SEARCH - Replace with real PropertyService later
     */
    private List<Property> mockSearch(String city, LocalDate checkIn, LocalDate checkOut,
                                       String numGuestsStr, String minPriceStr, String maxPriceStr) {
        List<Property> allProperties = getMockProperties();
        List<Property> filtered = new ArrayList<>();

        // Parse optional fields
        Integer numGuests = ValidationUtil.parseIntOrNull(numGuestsStr);
        Double minPrice = ValidationUtil.parseDoubleOrNull(minPriceStr);
        Double maxPrice = ValidationUtil.parseDoubleOrNull(maxPriceStr);

        // Filter properties
        for (Property p : allProperties) {
            // Filter by city (case-insensitive)
            if (!p.getCity().toLowerCase().contains(city.toLowerCase())) {
                continue;
            }

            // Filter by number of guests
            if (numGuests != null && p.getMaxGuests() < numGuests) {
                continue;
            }

            // Filter by price range
            if (minPrice != null && p.getPricePerNight() < minPrice) {
                continue;
            }
            if (maxPrice != null && p.getPricePerNight() > maxPrice) {
                continue;
            }

            // Property matches all criteria
            filtered.add(p);
        }

        return filtered;
    }

    /**
     * Get mock properties for testing
     */
    private List<Property> getMockProperties() {
        List<Property> properties = new ArrayList<>();

        // Property 1: Luxury Apartment in DHA
        Address addr1 = new Address("1", "DHA Phase 5", "Lahore", "Pakistan", "54000");
        Property p1 = new Property("1", "2", "Luxury Apartment in DHA", 15000, addr1);
        p1.setDescription("Beautiful 3BR apartment");
        p1.setMaxGuests(6);
        p1.setBedrooms(3);
        p1.setBathrooms(2);
        p1.setStatus("available");
        properties.add(p1);

        // Property 2: Cozy Studio in Gulberg
        Address addr2 = new Address("2", "Gulberg III", "Lahore", "Pakistan", "54000");
        Property p2 = new Property("2", "2", "Cozy Studio in Gulberg", 8000, addr2);
        p2.setDescription("Modern studio apartment");
        p2.setMaxGuests(2);
        p2.setBedrooms(1);
        p2.setBathrooms(1);
        p2.setStatus("available");
        properties.add(p2);

        // Property 3: Beach House in Clifton
        Address addr3 = new Address("3", "Clifton Block 2", "Karachi", "Pakistan", "75600");
        Property p3 = new Property("3", "3", "Beach House in Clifton", 25000, addr3);
        p3.setDescription("Stunning sea view");
        p3.setMaxGuests(8);
        p3.setBedrooms(4);
        p3.setBathrooms(3);
        p3.setStatus("available");
        properties.add(p3);

        // Property 4: Family Home in Bahria
        Address addr4 = new Address("4", "Bahria Town", "Islamabad", "Pakistan", "44000");
        Property p4 = new Property("4", "3", "Family Home in Bahria", 12000, addr4);
        p4.setDescription("Spacious family house");
        p4.setMaxGuests(5);
        p4.setBedrooms(3);
        p4.setBathrooms(2);
        p4.setStatus("available");
        properties.add(p4);

        // Property 5: Modern Flat in Johar
        Address addr5 = new Address("5", "Johar Town", "Lahore", "Pakistan", "54000");
        Property p5 = new Property("5", "2", "Modern Flat in Johar", 10000, addr5);
        p5.setDescription("Contemporary 2BR flat");
        p5.setMaxGuests(4);
        p5.setBedrooms(2);
        p5.setBathrooms(2);
        p5.setStatus("available");
        properties.add(p5);

        return properties;
    }
}
