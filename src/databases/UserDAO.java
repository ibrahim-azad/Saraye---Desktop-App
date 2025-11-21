package databases;

import models.*;
import java.sql.*;

public class UserDAO {
    private Connection conn;

    public UserDAO() {
        this.conn = DatabaseConnection.getInstance().getConnection();
    }

    // REGISTER: Save a new user
    public boolean saveUser(User user) {
        String sql = "INSERT INTO Users (userID, name, email, passwordHash, role, phoneNumber, status, createdAt) VALUES (?, ?, ?, ?, ?, ?, ?, SYSDATETIME())";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getUserId());
            stmt.setString(2, user.getName());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPassword()); // Hash this in production!
            stmt.setString(5, user.getRole());

            // Handle optional fields safely
            if (user.getPhone() != null) {
                stmt.setString(6, user.getPhone());
            } else {
                stmt.setNull(6, Types.NVARCHAR);
            }

            stmt.setString(7, "ACTIVE");
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Registration Error: " + e.getMessage());
            return false;
        }
    }

    // LOGIN: Find user by email
    public User getUserByEmail(String email) {
        String sql = "SELECT * FROM Users WHERE email = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String role = rs.getString("role");
                    String id = rs.getString("userID");
                    String name = rs.getString("name");
                    String pass = rs.getString("passwordHash");
                    String phone = rs.getString("phoneNumber");

                    // Factory logic to return specific subclass
                    if ("HOST".equalsIgnoreCase(role)) {
                        return new Host(id, name, email, pass, phone);
                    } else if ("GUEST".equalsIgnoreCase(role)) {
                        return new Guest(id, name, email, pass, phone);
                    } else if ("ADMIN".equalsIgnoreCase(role)) {
                        return new Admin(id, name, email, pass, phone);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if not found
    }
}