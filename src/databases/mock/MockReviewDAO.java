package databases.mock;

import models.*;
import databases.ReviewDAO;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of ReviewDAO for UI-only development without database.
 * Contains hardcoded sample reviews for testing UI components.
 * Extends ReviewDAO to allow polymorphic usage in DAOFactory.
 */
public class MockReviewDAO extends ReviewDAO {
    private List<Review> reviews;
    private int reviewCounter = 4;

    public MockReviewDAO() {
        super(true); // Pass true to skip database initialization
        initializeSampleData();
    }

    private void initializeSampleData() {
        reviews = new ArrayList<>();

        // Sample reviews (reviewID, bookingID, rating, comment, date)
        reviews.add(new Review(
                "R001", "B003",
                5, "Amazing property! Very clean and comfortable.",
                LocalDateTime.of(2025, 11, 26, 10, 30)));

        reviews.add(new Review(
                "R002", "B003",
                4, "Great location and friendly host. Would recommend!",
                LocalDateTime.of(2025, 11, 27, 14, 15)));

        reviews.add(new Review(
                "R003", "B005",
                3, "Good overall but could use better amenities.",
                LocalDateTime.of(2025, 11, 11, 9, 45)));
    }

    public boolean saveReview(Review review) {
        // Check if review already exists for this booking
        for (Review r : reviews) {
            if (r.getBookingID().equals(review.getBookingID())) {
                return false; // Already reviewed
            }
        }
        reviews.add(review);
        return true;
    }

    public boolean addReview(Review review) {
        return saveReview(review);
    }

    public boolean createReview(Review review) {
        return saveReview(review);
    }

    public List<Review> getReviewsByProperty(String propertyID) {
        // In mock, we need to get bookings for property first
        MockBookingDAO bookingDAO = new MockBookingDAO();
        List<Review> propertyReviews = new ArrayList<>();

        for (Review review : reviews) {
            Booking booking = bookingDAO.getBookingById(review.getBookingID());
            if (booking != null && booking.getPropertyID().equals(propertyID)) {
                propertyReviews.add(review);
            }
        }
        return propertyReviews;
    }

    public List<Review> getReviewsForProperty(String propertyID) {
        return getReviewsByProperty(propertyID);
    }

    public List<Review> getReviewsByPropertyId(String propertyID) {
        return getReviewsByProperty(propertyID);
    }

    public Review getReviewByBooking(String bookingID) {
        for (Review review : reviews) {
            if (review.getBookingID().equals(bookingID)) {
                return review;
            }
        }
        return null;
    }

    public List<Review> getReviewsByGuest(String guestID) {
        // Need to check via bookings
        MockBookingDAO bookingDAO = new MockBookingDAO();
        List<Review> guestReviews = new ArrayList<>();

        for (Review review : reviews) {
            Booking booking = bookingDAO.getBookingById(review.getBookingID());
            if (booking != null && booking.getGuestID().equals(guestID)) {
                guestReviews.add(review);
            }
        }
        return guestReviews;
    }

    public boolean hasGuestReviewedBooking(String bookingID) {
        return getReviewByBooking(bookingID) != null;
    }

    public double getAverageRatingForProperty(String propertyID) {
        List<Review> propertyReviews = getReviewsByProperty(propertyID);
        if (propertyReviews.isEmpty()) {
            return 0.0;
        }

        int totalRating = 0;
        for (Review review : propertyReviews) {
            totalRating += review.getRating();
        }

        return (double) totalRating / propertyReviews.size();
    }

    public boolean updateReview(Review review) {
        for (int i = 0; i < reviews.size(); i++) {
            if (reviews.get(i).getReviewID().equals(review.getReviewID())) {
                reviews.set(i, review);
                return true;
            }
        }
        return false;
    }

    public boolean deleteReview(String reviewID) {
        return reviews.removeIf(r -> r.getReviewID().equals(reviewID));
    }

    public String generateReviewID() {
        return String.format("R%03d", ++reviewCounter);
    }

    public List<Review> getAllReviews() {
        return new ArrayList<>(reviews);
    }
}
