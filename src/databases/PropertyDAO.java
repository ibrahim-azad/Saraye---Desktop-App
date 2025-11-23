package databases;

import models.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PropertyDAO {
    private Connection conn;

    public PropertyDAO() {
        this.conn = DatabaseConnection.getInstance().getConnection();
    }

    // Protected constructor for mock DAOs (skips database initialization)
    protected PropertyDAO(boolean skipInit) {
        this.conn = null;
    }

    public boolean saveProperty(Property property) {
        String sqlAddress = "INSERT INTO Addresses (addressID, street, city, country, zipCode) VALUES (?, ?, ?, ?, ?)";
        String sqlProperty = "INSERT INTO Properties (propertyID, hostID, addressID, title, description, pricePerNight, maxGuests, bedrooms, bathrooms, isActive) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 1)";
        String sqlAmenityLink = "INSERT INTO PropertyAmenities (propertyID, amenityID) VALUES (?, (SELECT amenityID FROM Amenities WHERE name = ?))";

        try {
            conn.setAutoCommit(false); // BEGIN TRANSACTION

            // 1. Save Address
            try (PreparedStatement stmt = conn.prepareStatement(sqlAddress)) {
                Address addr = property.getAddress();
                stmt.setString(1, addr.getAddressID());
                stmt.setString(2, addr.getStreet());
                stmt.setString(3, addr.getCity());
                stmt.setString(4, addr.getCountry());
                stmt.setString(5, addr.getZipCode());
                stmt.executeUpdate();
            }

            // 2. Save Property
            try (PreparedStatement stmt = conn.prepareStatement(sqlProperty)) {
                stmt.setString(1, property.getPropertyID());
                stmt.setString(2, property.getHostID());
                stmt.setString(3, property.getAddress().getAddressID());
                stmt.setString(4, property.getTitle());
                stmt.setString(5, property.getDescription());
                stmt.setDouble(6, property.getPricePerNight());
                stmt.setInt(7, property.getMaxGuests());
                stmt.setInt(8, property.getBedrooms());
                stmt.setInt(9, property.getBathrooms());
                stmt.executeUpdate();
            }

            // 3. Save Amenities (Many-to-Many)
            try (PreparedStatement stmt = conn.prepareStatement(sqlAmenityLink)) {
                for (Amenity amenity : property.getAmenities()) {
                    stmt.setString(1, property.getPropertyID());
                    stmt.setString(2, amenity.getName());
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }

            conn.commit(); // COMMIT TRANSACTION
            return true;

        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.err.println("Save Property Error: " + e.getMessage());
            return false;
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public List<Property> getAllProperties() {
        List<Property> list = new ArrayList<>();
        // Use JOIN to fetch Property + Address data in one go
        String sql = "SELECT p.*, a.addressID, a.street, a.city, a.country, a.zipCode FROM Properties p JOIN Addresses a ON p.addressID = a.addressID WHERE p.isActive = 1";

        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Address addr = new Address(
                        rs.getString("addressID"),
                        rs.getString("street"),
                        rs.getString("city"),
                        rs.getString("country"),
                        rs.getString("zipCode"));

                Property prop = new Property(
                        rs.getString("propertyID"),
                        rs.getString("hostID"),
                        rs.getString("title"),
                        rs.getDouble("pricePerNight"),
                        addr);
                prop.setDescription(rs.getString("description"));

                // Set additional property details
                try {
                    prop.setMaxGuests(rs.getInt("maxGuests"));
                    prop.setBedrooms(rs.getInt("bedrooms"));
                    prop.setBathrooms(rs.getInt("bathrooms"));
                } catch (SQLException e) {
                    // Columns might not exist in older database schemas
                    // Set default values
                    prop.setMaxGuests(2);
                    prop.setBedrooms(1);
                    prop.setBathrooms(1);
                }

                // Fetch amenities for this property
                loadAmenitiesForProperty(prop);
                list.add(prop);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private void loadAmenitiesForProperty(Property property) {
        String sql = "SELECT a.amenityID, a.name, a.iconPath FROM Amenities a " +
                "JOIN PropertyAmenities pa ON a.amenityID = pa.amenityID " +
                "WHERE pa.propertyID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, property.getPropertyID());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Amenity amenity = new Amenity(
                        rs.getInt("amenityID"),
                        rs.getString("name"),
                        rs.getString("iconPath"));
                property.addAmenity(amenity);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Search properties by city, check-in/out dates, and guest count
    public List<Property> searchProperties(String city, java.time.LocalDate checkIn, java.time.LocalDate checkOut,
            int guests) {
        List<Property> list = new ArrayList<>();
        // Basic search by city and active status
        // Advanced: Can add availability check by querying Bookings table
        String sql = "SELECT p.*, a.addressID, a.street, a.city, a.country, a.zipCode " +
                "FROM Properties p " +
                "JOIN Addresses a ON p.addressID = a.addressID " +
                "WHERE p.isActive = 1 AND a.city LIKE ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + city + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Address addr = new Address(
                        rs.getString("addressID"),
                        rs.getString("street"),
                        rs.getString("city"),
                        rs.getString("country"),
                        rs.getString("zipCode"));

                Property prop = new Property(
                        rs.getString("propertyID"),
                        rs.getString("hostID"),
                        rs.getString("title"),
                        rs.getDouble("pricePerNight"),
                        addr);
                prop.setDescription(rs.getString("description"));

                // Set additional property details
                try {
                    prop.setMaxGuests(rs.getInt("maxGuests"));
                    prop.setBedrooms(rs.getInt("bedrooms"));
                    prop.setBathrooms(rs.getInt("bathrooms"));
                } catch (SQLException e) {
                    // Columns might not exist, use defaults
                    prop.setMaxGuests(2);
                    prop.setBedrooms(1);
                    prop.setBathrooms(1);
                }

                loadAmenitiesForProperty(prop);
                list.add(prop);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Get single property by ID
    public Property getPropertyById(String propertyID) {
        String sql = "SELECT p.*, a.addressID, a.street, a.city, a.country, a.zipCode " +
                "FROM Properties p " +
                "JOIN Addresses a ON p.addressID = a.addressID " +
                "WHERE p.propertyID = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, propertyID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Address addr = new Address(
                        rs.getString("addressID"),
                        rs.getString("street"),
                        rs.getString("city"),
                        rs.getString("country"),
                        rs.getString("zipCode"));

                Property prop = new Property(
                        rs.getString("propertyID"),
                        rs.getString("hostID"),
                        rs.getString("title"),
                        rs.getDouble("pricePerNight"),
                        addr);
                prop.setDescription(rs.getString("description"));

                // Set additional property details
                try {
                    prop.setMaxGuests(rs.getInt("maxGuests"));
                    prop.setBedrooms(rs.getInt("bedrooms"));
                    prop.setBathrooms(rs.getInt("bathrooms"));
                } catch (SQLException e) {
                    // Columns might not exist, use defaults
                    prop.setMaxGuests(2);
                    prop.setBedrooms(1);
                    prop.setBathrooms(1);
                }

                loadAmenitiesForProperty(prop);
                return prop;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    } // Get all properties by host ID

    public List<Property> getPropertiesByHostId(String hostID) {
        List<Property> list = new ArrayList<>();
        String sql = "SELECT p.*, a.addressID, a.street, a.city, a.country, a.zipCode " +
                "FROM Properties p " +
                "JOIN Addresses a ON p.addressID = a.addressID " +
                "WHERE p.hostID = ? AND p.isActive = 1";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, hostID);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Address addr = new Address(
                        rs.getString("addressID"),
                        rs.getString("street"),
                        rs.getString("city"),
                        rs.getString("country"),
                        rs.getString("zipCode"));

                Property prop = new Property(
                        rs.getString("propertyID"),
                        rs.getString("hostID"),
                        rs.getString("title"),
                        rs.getDouble("pricePerNight"),
                        addr);
                prop.setDescription(rs.getString("description"));

                // Set additional property details
                try {
                    prop.setMaxGuests(rs.getInt("maxGuests"));
                    prop.setBedrooms(rs.getInt("bedrooms"));
                    prop.setBathrooms(rs.getInt("bathrooms"));
                } catch (SQLException e) {
                    // Columns might not exist, use defaults
                    prop.setMaxGuests(2);
                    prop.setBedrooms(1);
                    prop.setBathrooms(1);
                }

                loadAmenitiesForProperty(prop);
                list.add(prop);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    } // Create property (alias for saveProperty for controller compatibility)

    public boolean createProperty(Property property) {
        return saveProperty(property);
    }

    // Update existing property
    public boolean updateProperty(Property property) {
        String sqlProperty = "UPDATE Properties SET title = ?, description = ?, pricePerNight = ? WHERE propertyID = ?";
        String sqlAddress = "UPDATE Addresses SET street = ?, city = ?, country = ?, zipCode = ? WHERE addressID = ?";
        String deleteAmenities = "DELETE FROM PropertyAmenities WHERE propertyID = ?";
        String insertAmenity = "INSERT INTO PropertyAmenities (propertyID, amenityID) VALUES (?, (SELECT amenityID FROM Amenities WHERE name = ?))";

        try {
            conn.setAutoCommit(false);

            // Update property details
            try (PreparedStatement stmt = conn.prepareStatement(sqlProperty)) {
                stmt.setString(1, property.getTitle());
                stmt.setString(2, property.getDescription());
                stmt.setDouble(3, property.getPricePerNight());
                stmt.setString(4, property.getPropertyID());
                stmt.executeUpdate();
            }

            // Update address
            try (PreparedStatement stmt = conn.prepareStatement(sqlAddress)) {
                Address addr = property.getAddress();
                stmt.setString(1, addr.getStreet());
                stmt.setString(2, addr.getCity());
                stmt.setString(3, addr.getCountry());
                stmt.setString(4, addr.getZipCode());
                stmt.setString(5, addr.getAddressID());
                stmt.executeUpdate();
            }

            // Re-link amenities
            try (PreparedStatement stmt = conn.prepareStatement(deleteAmenities)) {
                stmt.setString(1, property.getPropertyID());
                stmt.executeUpdate();
            }

            try (PreparedStatement stmt = conn.prepareStatement(insertAmenity)) {
                for (Amenity amenity : property.getAmenities()) {
                    stmt.setString(1, property.getPropertyID());
                    stmt.setString(2, amenity.getName());
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.err.println("Update Property Error: " + e.getMessage());
            return false;
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Delete property (soft delete by setting isActive = 0)
    public boolean deleteProperty(String propertyID) {
        String sql = "UPDATE Properties SET isActive = 0 WHERE propertyID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, propertyID);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Update property status (available/unavailable)
    public boolean updatePropertyStatus(String propertyID, String status) {
        String sql = "UPDATE Properties SET status = ? WHERE propertyID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setString(2, propertyID);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Generate next Property ID in format P001, P002, etc.
     */
    public String generatePropertyID() {
        String sql = "SELECT TOP 1 propertyID FROM Properties WHERE propertyID LIKE 'P%' ORDER BY propertyID DESC";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                String lastID = rs.getString("propertyID");
                // Extract number from P001 -> 001
                int number = Integer.parseInt(lastID.substring(1));
                // Increment and format
                return String.format("P%03d", number + 1);
            } else {
                return "P001"; // First property
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "P001";
        }
    }

    /**
     * Generate next Address ID in format ADDR001, ADDR002, etc.
     */
    public String generateAddressID() {
        String sql = "SELECT TOP 1 addressID FROM Addresses WHERE addressID LIKE 'ADDR%' ORDER BY addressID DESC";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                String lastID = rs.getString("addressID");
                // Extract number from ADDR001 -> 001
                int number = Integer.parseInt(lastID.substring(4));
                // Increment and format
                return String.format("ADDR%03d", number + 1);
            } else {
                return "ADDR001"; // First address
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "ADDR001";
        }
    }

    /**
     * Get all available amenities from database
     */
    public List<Amenity> getAllAmenities() {
        List<Amenity> amenities = new ArrayList<>();
        String sql = "SELECT amenityID, name, iconPath FROM Amenities ORDER BY name";

        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Amenity amenity = new Amenity(
                        rs.getInt("amenityID"),
                        rs.getString("name"),
                        rs.getString("iconPath"));
                amenities.add(amenity);
            }
        } catch (SQLException e) {
            System.err.println("Error loading amenities: " + e.getMessage());
        }

        return amenities;
    }
}