package ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import models.Property;
import models.User;
import databases.PropertyDAO;
import ui.utils.AlertUtil;
import ui.utils.NavigationUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * HostPropertiesController - Manages host's property listings
 * Displays all properties owned by the host with their status
 *
 * GRASP Patterns Applied:
 * - Controller: Handles property management UI events
 * - Information Expert: Knows how to display and filter properties
 * - Low Coupling: Depends only on models and utilities
 */
public class HostPropertiesController {

    @FXML
    private TableView<Property> propertiesTable;

    @FXML
    private TableColumn<Property, String> titleColumn;

    @FXML
    private TableColumn<Property, String> cityColumn;

    @FXML
    private TableColumn<Property, Double> priceColumn;

    @FXML
    private TableColumn<Property, Integer> bedroomsColumn;

    @FXML
    private TableColumn<Property, Integer> maxGuestsColumn;

    @FXML
    private TableColumn<Property, String> statusColumn;

    @FXML
    private TableColumn<Property, Void> actionsColumn;

    @FXML
    private Label statusLabel;

    @FXML
    private Label countLabel;

    private User currentUser;
    private List<Property> allProperties;
    private String currentFilter = "all";

    /**
     * Initialize method - called after FXML is loaded
     */
    @FXML
    private void initialize() {
        // Setup table columns
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        cityColumn.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getAddress().getCity()));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("pricePerNight"));
        bedroomsColumn.setCellValueFactory(new PropertyValueFactory<>("bedrooms"));
        maxGuestsColumn.setCellValueFactory(new PropertyValueFactory<>("maxGuests"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Format price column
        priceColumn.setCellFactory(column -> new TableCell<Property, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(String.format("PKR %,.0f", price));
                }
            }
        });

        // Format status column with colors
        statusColumn.setCellFactory(column -> new TableCell<Property, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status.toUpperCase());
                    if ("available".equalsIgnoreCase(status)) {
                        setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #95a5a6; -fx-font-weight: bold;");
                    }
                }
            }
        });

        // Add action buttons
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button viewBtn = new Button("View");
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            private final HBox container = new HBox(5, viewBtn, editBtn, deleteBtn);

            {
                viewBtn.setStyle(
                        "-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 5 10; -fx-background-radius: 3; -fx-font-size: 11px;");
                editBtn.setStyle(
                        "-fx-background-color: #f39c12; -fx-text-fill: white; -fx-padding: 5 10; -fx-background-radius: 3; -fx-font-size: 11px;");
                deleteBtn.setStyle(
                        "-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-padding: 5 10; -fx-background-radius: 3; -fx-font-size: 11px;");

                viewBtn.setOnAction(event -> {
                    Property property = getTableView().getItems().get(getIndex());
                    handleViewProperty(property);
                });

                editBtn.setOnAction(event -> {
                    Property property = getTableView().getItems().get(getIndex());
                    handleEditProperty(property);
                });

                deleteBtn.setOnAction(event -> {
                    Property property = getTableView().getItems().get(getIndex());
                    handleDeleteProperty(property);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(container);
                }
            }
        });
    }

    /**
     * Set user data
     */
    public void setUser(Object userData) {
        if (userData instanceof User) {
            this.currentUser = (User) userData;
            loadProperties();
        }
    }

    /**
     * Load properties from database
     */
    private void loadProperties() {
        if (currentUser != null) {
            PropertyDAO propertyDAO = databases.DAOFactory.getPropertyDAO();
            allProperties = propertyDAO.getPropertiesByHostId(currentUser.getUserId());
        } else {
            allProperties = new ArrayList<>();
        }
        filterProperties();
    }

    /**
     * Handle Show All filter
     */
    @FXML
    private void handleShowAll() {
        currentFilter = "all";
        filterProperties();
    }

    /**
     * Handle Show Available filter
     */
    @FXML
    private void handleShowAvailable() {
        currentFilter = "available";
        filterProperties();
    }

    /**
     * Handle Show Unavailable filter
     */
    @FXML
    private void handleShowUnavailable() {
        currentFilter = "unavailable";
        filterProperties();
    }

    /**
     * Filter properties based on current filter
     */
    private void filterProperties() {
        List<Property> filtered;

        if ("all".equals(currentFilter)) {
            filtered = new ArrayList<>(allProperties);
            statusLabel.setText("Showing: All Properties");
        } else {
            filtered = allProperties.stream()
                    .filter(p -> currentFilter.equalsIgnoreCase(p.getStatus()))
                    .collect(Collectors.toList());
            statusLabel.setText("Showing: " + currentFilter.substring(0, 1).toUpperCase() +
                    currentFilter.substring(1) + " Properties");
        }

        propertiesTable.getItems().clear();
        propertiesTable.getItems().addAll(filtered);
        countLabel.setText("(" + filtered.size() + " properties)");
    }

    /**
     * Handle View Property
     */
    private void handleViewProperty(Property property) {
        NavigationUtil.navigateWithMultipleData("property-details.fxml", currentUser, property);
    }

    /**
     * Handle Edit Property
     */
    private void handleEditProperty(Property property) {
        AlertUtil.showInfo("Edit Property", "Edit property feature coming soon!\n\nProperty: " + property.getTitle());
        // TODO: Create edit-property.fxml and EditPropertyController
        // NavigationUtil.navigateWithMultipleData("edit-property.fxml", currentUser,
        // property);
    }

    /**
     * Handle Delete Property
     */
    private void handleDeleteProperty(Property property) {
        boolean confirmed = AlertUtil.showConfirmation(
                "Delete Property",
                "Are you sure you want to delete this property?\n\n" +
                        "Property: " + property.getTitle() + "\n" +
                        "City: " + property.getCity() + "\n\n" +
                        "This action cannot be undone!");

        if (confirmed) {
            PropertyDAO propertyDAO = databases.DAOFactory.getPropertyDAO();
            boolean success = propertyDAO.deleteProperty(property.getPropertyID());

            if (success) {
                AlertUtil.showSuccess("Property Deleted",
                        "Property has been deleted successfully!");
                loadProperties(); // Refresh the list
            } else {
                AlertUtil.showError("Error", "Failed to delete property. It may have active bookings.");
            }
        }
    }

    /**
     * Handle Add Property button
     */
    @FXML
    private void handleAddProperty() {
        NavigationUtil.navigateTo("add-property.fxml", currentUser);
    }

    /**
     * Handle Back button
     */
    @FXML
    private void handleBack() {
        NavigationUtil.navigateTo("host-dashboard.fxml", currentUser);
    }
}
