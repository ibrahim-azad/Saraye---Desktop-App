package databases;

import models.Amenity;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AmenityDAO {
    private Connection conn;

    public AmenityDAO() {
        this.conn = DatabaseConnection.getInstance().getConnection();
    }

    public List<Amenity> getAllAmenities() {
        List<Amenity> list = new ArrayList<>();
        String sql = "SELECT * FROM Amenities";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Amenity(
                        rs.getInt("amenityID"),
                        rs.getString("name"),
                        rs.getString("iconPath")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}