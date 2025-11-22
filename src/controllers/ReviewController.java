package controllers;

import models.Review;
import databases.ReviewDAO;
import utils.SessionManager;
import java.sql.SQLException;
import java.util.List;

/**
 * ReviewController - Business Logic Layer
 * UC10: Leave Review/Rating
 */
public class ReviewController {
    
    private static ReviewController instance;
    private ReviewDAO reviewDAO;
    private SessionManager sessionManager;
    
    private ReviewController() {
        this.reviewDAO = new ReviewDAO();
        this.sessionManager = SessionManager.getInstance();
    }
    
    public static ReviewController getInstance() {
        if (instance == null) {
            instance = new ReviewController();
        }
        return instance;
    }
    
    public String createReview(String bookingId, int rating, String comment) {
        try {
            if (!sessionManager.isLoggedIn()) {
                return "You must be logged in to leave a review";
            }
            
            Review review = new Review(bookingId, rating, comment);
            
            boolean success = reviewDAO.createReview(review);
            return success ? "SUCCESS" : "Failed to create review";
            
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
    
    public List<Review> getReviewsByProperty(String propertyId) {
        try {
            return reviewDAO.getReviewsByPropertyId(propertyId);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}