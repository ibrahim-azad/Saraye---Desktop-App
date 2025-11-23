package databases.mock;

import models.*;
import databases.PropertyDAO;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

/**
 * Mock implementation of PropertyDAO for UI-only development without database.
 * Contains hardcoded sample data for testing UI components.
 * Extends PropertyDAO to allow polymorphic usage in DAOFactory.
 */
public class MockPropertyDAO extends PropertyDAO {
    private List<Property> properties;
    private List<Amenity> amenities;
    private int propertyCounter = 3;
    private int addressCounter = 3;

    public MockPropertyDAO() {
        super(true); // Pass true to skip database initialization
        initializeSampleData();
    }

    private void initializeSampleData() {
        properties = new ArrayList<>();
        amenities = new ArrayList<>();

        // Sample amenities
        amenities.add(new Amenity(1, "WiFi", "wifi-icon.png"));
        amenities.add(new Amenity(2, "Pool", "pool-icon.png"));
        amenities.add(new Amenity(3, "Parking", "parking-icon.png"));
        amenities.add(new Amenity(4, "Air Conditioning", "ac-icon.png"));
        amenities.add(new Amenity(5, "Kitchen", "kitchen-icon.png"));
        amenities.add(new Amenity(6, "Gym", "gym-icon.png"));
        amenities.add(new Amenity(7, "Pet Friendly", "pet-icon.png"));
        amenities.add(new Amenity(8, "Balcony", "balcony-icon.png"));

        // Sample properties
        Address addr1 = new Address("ADDR001", "123 Main St", "Tehran", "Iran", "12345");
        Property prop1 = new Property("P001", "H001", "Luxury Downtown Apartment", 150.0, addr1);
        prop1.setDescription("Beautiful apartment in the heart of Tehran with stunning city views.");
        prop1.setMaxGuests(4);
        prop1.setBedrooms(2);
        prop1.setBathrooms(2);
        prop1.addAmenity(amenities.get(0)); // WiFi
        prop1.addAmenity(amenities.get(3)); // AC
        prop1.addAmenity(amenities.get(4)); // Kitchen
        properties.add(prop1);

        Address addr2 = new Address("ADDR002", "456 Beach Rd", "Isfahan", "Iran", "67890");
        Property prop2 = new Property("P002", "H001", "Cozy Beach House", 200.0, addr2);
        prop2.setDescription("Relaxing beachside property perfect for family vacations.");
        prop2.setMaxGuests(6);
        prop2.setBedrooms(3);
        prop2.setBathrooms(2);
        prop2.addAmenity(amenities.get(0)); // WiFi
        prop2.addAmenity(amenities.get(1)); // Pool
        prop2.addAmenity(amenities.get(2)); // Parking
        prop2.addAmenity(amenities.get(6)); // Pet Friendly
        properties.add(prop2);

        Address addr3 = new Address("ADDR003", "789 Mountain View", "Shiraz", "Iran", "11111");
        Property prop3 = new Property("P003", "H002", "Mountain Villa", 180.0, addr3);
        prop3.setDescription("Stunning mountain views with modern amenities and spacious rooms.");
        prop3.setMaxGuests(8);
        prop3.setBedrooms(4);
        prop3.setBathrooms(3);
        prop3.addAmenity(amenities.get(0)); // WiFi
        prop3.addAmenity(amenities.get(2)); // Parking
        prop3.addAmenity(amenities.get(3)); // AC
        prop3.addAmenity(amenities.get(5)); // Gym
        prop3.addAmenity(amenities.get(7)); // Balcony
        properties.add(prop3);
    }

    public boolean saveProperty(Property property) {
        // Simulate successful save
        properties.add(property);
        return true;
    }

    public List<Property> getAllProperties() {
        return new ArrayList<>(properties);
    }

    public List<Property> searchProperties(String city, LocalDate checkIn, LocalDate checkOut, int guests) {
        List<Property> results = new ArrayList<>();
        for (Property prop : properties) {
            if (city == null || city.isEmpty() ||
                    prop.getAddress().getCity().toLowerCase().contains(city.toLowerCase())) {
                if (prop.getMaxGuests() >= guests) {
                    results.add(prop);
                }
            }
        }
        return results;
    }

    public Property getPropertyById(String propertyID) {
        for (Property prop : properties) {
            if (prop.getPropertyID().equals(propertyID)) {
                return prop;
            }
        }
        return null;
    }

    public List<Property> getPropertiesByHost(String hostID) {
        List<Property> hostProperties = new ArrayList<>();
        for (Property prop : properties) {
            if (prop.getHostID().equals(hostID)) {
                hostProperties.add(prop);
            }
        }
        return hostProperties;
    }

    public boolean updateProperty(Property property) {
        for (int i = 0; i < properties.size(); i++) {
            if (properties.get(i).getPropertyID().equals(property.getPropertyID())) {
                properties.set(i, property);
                return true;
            }
        }
        return false;
    }

    public boolean deleteProperty(String propertyID) {
        return properties.removeIf(p -> p.getPropertyID().equals(propertyID));
    }

    public List<Amenity> getAllAmenities() {
        return new ArrayList<>(amenities);
    }

    public String generatePropertyID() {
        return String.format("P%03d", ++propertyCounter);
    }

    public String generateAddressID() {
        return String.format("ADDR%03d", ++addressCounter);
    }

    public boolean isPropertyAvailable(String propertyID, LocalDate checkIn, LocalDate checkOut) {
        // Mock: always available
        return true;
    }
}
