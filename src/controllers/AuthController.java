package controllers;

import models.*;
import databases.UserDAO;
import utils.SessionManager;
import java.sql.SQLException;

/**
 * AuthController - Business Logic Layer
 * Handles authentication (login/register)
 * UC1: Register/Login
 */
public class AuthController {
    
    // Singleton instance
    private static AuthController instance;
    
    // Dependencies
    private UserDAO userDAO;
    private SessionManager sessionManager;
    
    // Private constructor
    private AuthController() {
        this.userDAO = new UserDAO();
        this.sessionManager = SessionManager.getInstance();
    }
    
    // Get singleton instance
    public static AuthController getInstance() {
        if (instance == null) {
            instance = new AuthController();
        }
        return instance;
    }
    
    /**
     * Register a new user
     * @param name User's full name
     * @param email User's email
     * @param password User's password
     * @param phone User's phone number
     * @param role User role: "GUEST" or "HOST"
     * @return Success message or error
     */
    public String register(String name, String email, String password, String phone, String role) {
        try {
            // Input validation
            if (name == null || name.trim().isEmpty()) {
                return "Name cannot be empty";
            }
            if (email == null || !email.contains("@")) {
                return "Invalid email address";
            }
            if (password == null || password.length() < 6) {
                return "Password must be at least 6 characters";
            }
            if (phone == null || phone.trim().isEmpty()) {
                return "Phone number cannot be empty";
            }
            
            // Check if email already exists
            if (userDAO.emailExists(email)) {
                return "Email already registered";
            }
            
            // Create user based on role
            User newUser;
            if (role.equalsIgnoreCase("HOST")) {
                newUser = new Host(null, name, email, password, phone);
            } else {
                newUser = new Guest(null, name, email, password, phone);
            }
            
            // Save to database
            boolean success = userDAO.createUser(newUser);
            
            if (success) {
                return "SUCCESS";
            } else {
                return "Registration failed. Please try again.";
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            return "Database error: " + e.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
    
    /**
     * Login user
     * @param email User's email
     * @param password User's password
     * @return User object if successful, null otherwise
     */
    public User login(String email, String password) {
        try {
            // Input validation
            if (email == null || email.trim().isEmpty()) {
                return null;
            }
            if (password == null || password.trim().isEmpty()) {
                return null;
            }
            
            // Authenticate user
            User user = userDAO.authenticateUser(email, password);
            
            if (user != null) {
                // Set session
                sessionManager.setCurrentUser(user);
                return user;
            }
            
            return null;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Logout current user
     */
    public void logout() {
        sessionManager.clearSession();
    }
    
    /**
     * Get currently logged-in user
     */
    public User getCurrentUser() {
        return sessionManager.getCurrentUser();
    }
    
    /**
     * Check if user is logged in
     */
    public boolean isLoggedIn() {
        return sessionManager.isLoggedIn();
    }
}