package controllers;

import models.Property;
import databases.PropertyDAO;
import databases.DAOFactory;
import utils.SessionManager;
import java.util.List;

/**
 * PropertyController - Business Logic Layer
 * Handles property search, details, and management
 * UC2: Search Properties
 * UC3: View Property Details
 * UC8: Manage Property Listing
 */
public class PropertyController {

    private static PropertyController instance;
    private PropertyDAO propertyDAO;
    private SessionManager sessionManager;

    private PropertyController() {
        this.propertyDAO = DAOFactory.getPropertyDAO();
        this.sessionManager = SessionManager.getInstance();
    }

    public static PropertyController getInstance() {
        if (instance == null) {
            instance = new PropertyController();
        }
        return instance;
    }

    /**
     * Search properties with filters
     * UC2: Search Properties
     */
    public List<Property> searchProperties(String city, String checkIn, String checkOut, int guests) {
        try {
            // Convert String dates to LocalDate
            java.time.LocalDate checkInDate = (checkIn != null && !checkIn.isEmpty())
                    ? java.time.LocalDate.parse(checkIn)
                    : null;
            java.time.LocalDate checkOutDate = (checkOut != null && !checkOut.isEmpty())
                    ? java.time.LocalDate.parse(checkOut)
                    : null;

            // Use PropertyDAO to search
            return propertyDAO.searchProperties(city, checkInDate, checkOutDate, guests);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get property details by ID
     * UC3: View Property Details
     */
    public Property getPropertyById(String propertyId) {
        try {
            return propertyDAO.getPropertyById(propertyId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get all properties for a host
     * UC8: Manage Property Listing
     */
    public List<Property> getPropertiesByHost(String hostId) {
        try {
            return propertyDAO.getPropertiesByHostId(hostId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Add a new property
     * UC8: Manage Property Listing
     */
    public String addProperty(Property property) {
        try {
            // Validate host is logged in
            if (!sessionManager.isLoggedIn()) {
                return "You must be logged in to add a property";
            }

            // Validate property data
            if (property.getTitle() == null || property.getTitle().trim().isEmpty()) {
                return "Property title is required";
            }
            if (property.getPricePerNight() <= 0) {
                return "Valid price is required";
            }

            boolean success = propertyDAO.createProperty(property);
            return success ? "SUCCESS" : "Failed to add property";

        } catch (Exception e) {
            e.printStackTrace();
            return "Database error: " + e.getMessage();
        }
    }

    /**
     * Update existing property
     * UC8: Manage Property Listing
     */
    public String updateProperty(Property property) {
        try {
            boolean success = propertyDAO.updateProperty(property);
            return success ? "SUCCESS" : "Failed to update property";
        } catch (Exception e) {
            e.printStackTrace();
            return "Database error: " + e.getMessage();
        }
    }

    /**
     * Delete a property
     * UC8: Manage Property Listing
     */
    public String deleteProperty(String propertyId) {
        try {
            boolean success = propertyDAO.deleteProperty(propertyId);
            return success ? "SUCCESS" : "Failed to delete property";
        } catch (Exception e) {
            e.printStackTrace();
            return "Database error: " + e.getMessage();
        }
    }
}