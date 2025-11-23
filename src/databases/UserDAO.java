package databases;

import models.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private Connection conn;

    public UserDAO() {
        this.conn = DatabaseConnection.getInstance().getConnection();
    }

    // Protected constructor for mock DAOs (skips database initialization)
    protected UserDAO(boolean skipInit) {
        this.conn = null;
    }

    // REGISTER: Save a new user
    public boolean saveUser(User user) {
        // Generate user ID if not provided
        if (user.getUserId() == null || user.getUserId().isEmpty()) {
            String generatedID = generateUserID(user.getRole());
            user.setUserId(generatedID);
        }

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

    // Check if email exists in database
    public boolean emailExists(String email) {
        String sql = "SELECT COUNT(*) FROM Users WHERE email = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Create user (alias for saveUser for business logic compatibility)
    public boolean createUser(User user) {
        return saveUser(user);
    }

    // Authenticate user with email and password
    public User authenticateUser(String email, String password) {
        User user = getUserByEmail(email);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    // Update user profile (name, phone, password)
    public boolean updateUser(User user) {
        String sql = "UPDATE Users SET name = ?, phoneNumber = ?, passwordHash = ? WHERE userID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getPhone());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getUserId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Update User Error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Generate user ID based on role (G001, G002 for guests, H001, H002 for hosts)
     */
    private String generateUserID(String role) {
        String prefix = role.equalsIgnoreCase("GUEST") ? "G" : "H";
        String sql = "SELECT TOP 1 userID FROM Users WHERE userID LIKE ? ORDER BY userID DESC";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, prefix + "%");
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String lastID = rs.getString("userID");
                // Extract number from G001 -> 001
                int number = Integer.parseInt(lastID.substring(1));
                // Increment and format
                return String.format(prefix + "%03d", number + 1);
            } else {
                return prefix + "001"; // First user of this type
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return prefix + "001";
        }
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM Users";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String userID = rs.getString("userID");
                String name = rs.getString("name");
                String email = rs.getString("email");
                String password = rs.getString("password");
                String phone = rs.getString("phone");
                String role = rs.getString("role");

                User user = null;
                switch (role.toUpperCase()) {
                    case "GUEST":
                        user = new Guest(userID, name, email, password, phone);
                        break;
                    case "HOST":
                        user = new Host(userID, name, email, password, phone);
                        break;
                    case "ADMIN":
                        user = new Admin(userID, name, email, password, phone);
                        break;
                }
                if (user != null) {
                    users.add(user);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
}