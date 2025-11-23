package controllers;

import models.User;
import databases.UserDAO;
import databases.DAOFactory;
import utils.SessionManager;

public class AccountController {
    private static AccountController instance;
    private UserDAO userDAO;
    private SessionManager sessionManager;

    private AccountController() {
        this.userDAO = DAOFactory.getUserDAO();
        this.sessionManager = SessionManager.getInstance();
    }

    public static AccountController getInstance() {
        if (instance == null) {
            instance = new AccountController();
        }
        return instance;
    }

    public User getCurrentUserProfile() {
        return sessionManager.getCurrentUser();
    }

    public String updateProfile(String name, String phone) {
        try {
            User user = sessionManager.getCurrentUser();
            if (user == null)
                return "Not logged in";

            // Validate inputs
            if (name == null || name.trim().isEmpty()) {
                return "Name cannot be empty";
            }
            if (phone == null || phone.trim().isEmpty()) {
                return "Phone number cannot be empty";
            }

            user.setName(name);
            user.setPhone(phone);

            // Save to database
            boolean success = userDAO.updateUser(user);

            if (success) {
                // Update session with modified user
                sessionManager.setCurrentUser(user);
                return "SUCCESS";
            } else {
                return "Failed to update profile";
            }
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    public String changePassword(String currentPassword, String newPassword) {
        try {
            User user = sessionManager.getCurrentUser();
            if (user == null)
                return "Not logged in";

            // Verify current password
            if (!user.getPassword().equals(currentPassword)) {
                return "Current password is incorrect";
            }

            // Validate new password
            if (newPassword == null || newPassword.length() < 6) {
                return "New password must be at least 6 characters";
            }

            // Update password
            user.setPassword(newPassword);
            boolean success = userDAO.updateUser(user);

            if (success) {
                sessionManager.setCurrentUser(user);
                return "SUCCESS";
            } else {
                return "Failed to change password";
            }
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}