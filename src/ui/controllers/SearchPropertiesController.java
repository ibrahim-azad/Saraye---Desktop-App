package ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import models.Property;
import models.User;
import models.Address;
import databases.PropertyDAO;
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
                viewBtn.setStyle(
                        "-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 5 15; -fx-background-radius: 5;");
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

        // Load properties from database on initialization
        loadPropertiesFromDatabase();
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
        // Search properties from database
        List<Property> results = searchPropertiesInDatabase(city, checkIn, checkOut, numGuestsStr, minPriceStr,
                maxPriceStr);

        // Display results
        resultsTable.getItems().clear();
        resultsTable.getItems().addAll(results);

        // Update count
        resultsCountLabel.setText("(" + results.size() + " properties found)");

        if (results.isEmpty()) {
            AlertUtil.showInfo("No Results",
                    "No properties found matching your criteria.\nTry adjusting your filters.");
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
        loadPropertiesFromDatabase();
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
     * Load properties from database for initial display
     */
    private void loadPropertiesFromDatabase() {
        PropertyDAO propertyDAO = databases.DAOFactory.getPropertyDAO();
        List<Property> properties = propertyDAO.getAllProperties();
        resultsTable.getItems().clear();
        resultsTable.getItems().addAll(properties);
        resultsCountLabel.setText("(" + properties.size() + " properties found)");
    }

    /**
     * Search properties in database with filters
     */
    private List<Property> searchPropertiesInDatabase(String city, LocalDate checkIn, LocalDate checkOut,
            String numGuestsStr, String minPriceStr, String maxPriceStr) {
        PropertyDAO propertyDAO = databases.DAOFactory.getPropertyDAO();
        List<Property> allProperties = propertyDAO.getAllProperties();
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
}
