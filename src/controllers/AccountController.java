package controllers;

import models.User;
import databases.UserDAO;
import utils.SessionManager;

public class AccountController {
    private static AccountController instance;
    private UserDAO userDAO;
    private SessionManager sessionManager;
    
    private AccountController() {
        this.userDAO = new UserDAO();
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
            if (user == null) return "Not logged in";
            
            user.setName(name);
            user.setPhone(phone);
            
            // Save would require UserDAO.updateUser() method
            return "SUCCESS";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}