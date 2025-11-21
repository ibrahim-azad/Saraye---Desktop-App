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

    public boolean saveProperty(Property property) {
        String sqlAddress = "INSERT INTO Addresses (addressID, street, city, country, zipCode) VALUES (?, ?, ?, ?, ?)";
        String sqlProperty = "INSERT INTO Properties (propertyID, hostID, addressID, title, description, pricePerNight, isActive) VALUES (?, ?, ?, ?, ?, ?, 1)";
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
}