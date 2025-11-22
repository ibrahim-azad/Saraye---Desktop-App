package utils;

import models.User;

/**
 * SessionManager - Singleton pattern
 * Manages the currently logged-in user session
 * Used by all controllers to track authentication state
 */
public class SessionManager {
    
    private static SessionManager instance;
    private User currentUser;
    
    // Private constructor for singleton
    private SessionManager() {
        this.currentUser = null;
    }
    
    // Get singleton instance
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }
    
    // Set current logged-in user
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
    
    // Get current logged-in user
    public User getCurrentUser() {
        return currentUser;
    }
    
    // Check if user is logged in
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    // Clear session (logout)
    public void clearSession() {
        this.currentUser = null;
    }
    
    // Get user ID (convenience method)
    public String getCurrentUserId() {
        return (currentUser != null) ? currentUser.getUserId() : null;
    }
    
    // Get user role (convenience method)
    public String getCurrentUserRole() {
        if (currentUser == null) return null;
        
        if (currentUser instanceof models.Guest) return "GUEST";
        if (currentUser instanceof models.Host) return "HOST";
        if (currentUser instanceof models.Admin) return "ADMIN";
        
        return null;
    }
}