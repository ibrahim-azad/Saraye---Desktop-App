package ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import models.Booking;
import models.User;
import ui.utils.AlertUtil;
import ui.utils.NavigationUtil;

/**
 * LeaveReviewController - Handles leaving reviews for completed bookings
 * UC10: Leave Review/Rating
 */
public class LeaveReviewController {

    @FXML
    private Label propertyNameLabel;

    @FXML
    private Label bookingIdLabel;

    @FXML
    private Label stayPeriodLabel;

    @FXML
    private Button star1, star2, star3, star4, star5;

    @FXML
    private Label ratingLabel;

    @FXML
    private TextArea reviewTextArea;

    private User currentUser;
    private Booking currentBooking;
    private int selectedRating = 0;

    /**
     * Set user data
     */
    public void setUser(Object userData) {
        if (userData instanceof User) {
            this.currentUser = (User) userData;
        }
    }

    /**
     * Set booking data
     */
    public void setData(Object data) {
        if (data instanceof Booking) {
            this.currentBooking = (Booking) data;
            displayBookingDetails();
        }
    }

    /**
     * Initialize star ratings
     */
    @FXML
    private void initialize() {
        updateStarDisplay();
    }

    /**
     * Display booking details
     */
    private void displayBookingDetails() {
        if (currentBooking != null) {
            propertyNameLabel.setText(currentBooking.getPropertyTitle() != null ? currentBooking.getPropertyTitle()
                    : "Property ID: " + currentBooking.getPropertyID());
            bookingIdLabel.setText(currentBooking.getBookingID());
            stayPeriodLabel.setText(currentBooking.getCheckInDate() + " to " + currentBooking.getCheckOutDate());
        }
    }

    /**
     * Handle star rating selections
     */
    @FXML
    private void handleRating1() {
        setRating(1);
    }

    @FXML
    private void handleRating2() {
        setRating(2);
    }

    @FXML
    private void handleRating3() {
        setRating(3);
    }

    @FXML
    private void handleRating4() {
        setRating(4);
    }

    @FXML
    private void handleRating5() {
        setRating(5);
    }

    /**
     * Set the rating and update display
     */
    private void setRating(int rating) {
        selectedRating = rating;
        updateStarDisplay();

        String[] ratingTexts = {
                "Select a rating",
                "⭐ Poor",
                "⭐⭐ Fair",
                "⭐⭐⭐ Good",
                "⭐⭐⭐⭐ Very Good",
                "⭐⭐⭐⭐⭐ Excellent"
        };

        ratingLabel.setText(ratingTexts[rating]);
        ratingLabel.setStyle("-fx-text-fill: #f39c12; -fx-font-weight: bold; -fx-font-size: 16px;");
    }

    /**
     * Update star button colors based on selected rating
     */
    private void updateStarDisplay() {
        Button[] stars = { star1, star2, star3, star4, star5 };

        for (int i = 0; i < stars.length; i++) {
            if (i < selectedRating) {
                stars[i].setStyle(
                        "-fx-font-size: 40px; -fx-background-color: transparent; -fx-cursor: hand; -fx-text-fill: #f39c12;");
            } else {
                stars[i].setStyle(
                        "-fx-font-size: 40px; -fx-background-color: transparent; -fx-cursor: hand; -fx-text-fill: #bdc3c7;");
            }
        }
    }

    /**
     * Handle submit review button
     */
    @FXML
    private void handleSubmitReview() {
        // Validate rating
        if (selectedRating == 0) {
            AlertUtil.showError("Validation Error", "Please select a rating (1-5 stars)!");
            return;
        }

        // Validate review text
        String reviewText = reviewTextArea.getText().trim();
        if (reviewText.isEmpty()) {
            AlertUtil.showError("Validation Error", "Please write a review!");
            reviewTextArea.requestFocus();
            return;
        }

        if (reviewText.length() < 20) {
            AlertUtil.showError("Validation Error", "Review must be at least 20 characters long!");
            reviewTextArea.requestFocus();
            return;
        }

        if (reviewText.length() > 1000) {
            AlertUtil.showError("Validation Error", "Review must not exceed 1000 characters!");
            reviewTextArea.requestFocus();
            return;
        }

        // Submit review via business logic
        try {
            controllers.ReviewController reviewController = controllers.ReviewController.getInstance();
            String result = reviewController.createReview(
                    currentBooking.getBookingID(),
                    selectedRating,
                    reviewText);

            if ("SUCCESS".equals(result)) {
                AlertUtil.showSuccess(
                        "Review Submitted!",
                        "Thank you for your review!\n\n" +
                                "Rating: " + selectedRating + " stars\n" +
                                "Your feedback helps other guests make informed decisions.");
                handleBack();
            } else {
                AlertUtil.showError("Submission Failed", result);
            }

        } catch (Exception e) {
            e.printStackTrace();
            AlertUtil.showError("Error", "Failed to submit review: " + e.getMessage());
        }
    }

    /**
     * Handle back button
     */
    @FXML
    private void handleBack() {
        NavigationUtil.navigateTo("guest-bookings.fxml", currentUser);
    }
}
