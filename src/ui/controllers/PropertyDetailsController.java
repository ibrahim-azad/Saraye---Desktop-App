package ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import models.Property;
import models.User;
import models.Amenity;
import models.Review;
import ui.utils.NavigationUtil;
import java.util.List;
import java.util.stream.Collectors;

/**
 * PropertyDetailsController - Displays detailed property information
 * UC3: View Property Details
 * UC11: View Reviews
 *
 * GRASP Patterns Applied:
 * - Controller: Handles property details UI events
 * - Information Expert: Knows how to display property information
 * - Low Coupling: Depends only on models and utilities
 */
public class PropertyDetailsController {

    @FXML
    private Label titleLabel;

    @FXML
    private Label addressLabel;

    @FXML
    private Label priceLabel;

    @FXML
    private Label guestsLabel;

    @FXML
    private Label bedroomsLabel;

    @FXML
    private Label bathroomsLabel;

    @FXML
    private Label descriptionLabel;

    @FXML
    private Label amenitiesLabel;

    @FXML
    private Label averageRatingLabel;

    @FXML
    private Label reviewCountLabel;

    @FXML
    private VBox reviewsContainer;

    private User currentUser;
    private Property currentProperty;

    /**
     * Set user data (called by NavigationUtil)
     */
    public void setUser(Object userData) {
        if (userData instanceof User) {
            this.currentUser = (User) userData;
        }
    }

    /**
     * Set property data (called by NavigationUtil)
     */
    public void setData(Object data) {
        if (data instanceof Property) {
            this.currentProperty = (Property) data;
            displayPropertyDetails();
            loadReviews();
        }
    }

    /**
     * Display property details in UI
     */
    private void displayPropertyDetails() {
        if (currentProperty == null) {
            return;
        }

        titleLabel.setText(currentProperty.getTitle());
        addressLabel.setText(currentProperty.getAddress().getFullAddress());
        priceLabel.setText(String.format("%,.0f", currentProperty.getPricePerNight()));
        guestsLabel.setText(String.valueOf(currentProperty.getMaxGuests()));
        bedroomsLabel.setText(String.valueOf(currentProperty.getBedrooms()));
        bathroomsLabel.setText(String.valueOf(currentProperty.getBathrooms()));
        descriptionLabel.setText(currentProperty.getDescription());

        // Convert amenities list to comma-separated string
        if (currentProperty.getAmenities() != null && !currentProperty.getAmenities().isEmpty()) {
            String amenitiesStr = currentProperty.getAmenities().stream()
                    .map(Amenity::getName)
                    .collect(Collectors.joining(", "));
            amenitiesLabel.setText(amenitiesStr);
        } else {
            amenitiesLabel.setText("No amenities listed");
        }
    }

    /**
     * Handle Book Now button
     * Navigate to booking request screen
     */
    @FXML
    private void handleBookNow() {
        // Navigate to booking request screen with user and property data
        NavigationUtil.navigateWithMultipleData("booking-request.fxml", currentUser, currentProperty);
    }

    /**
     * Handle Back button
     * Return to appropriate dashboard based on user role
     */
    @FXML
    private void handleBack() {
        if (currentUser != null) {
            // Check if this is the host viewing their own property
            if (currentProperty != null && currentUser.getUserId().equals(currentProperty.getHostID())) {
                // Host viewing their own property - go back to host properties
                NavigationUtil.navigateTo("host-properties.fxml", currentUser);
            } else if ("HOST".equalsIgnoreCase(currentUser.getRole())) {
                // Host viewing another property - go to search
                NavigationUtil.navigateTo("search-properties.fxml", currentUser);
            } else {
                // Guest - go to search
                NavigationUtil.navigateTo("search-properties.fxml", currentUser);
            }
        } else {
            // No user logged in - go to login
            NavigationUtil.navigateTo("login.fxml");
        }
    }

    /**
     * Load and display reviews for the current property
     * UC11: View Reviews
     */
    private void loadReviews() {
        if (currentProperty == null) {
            return;
        }

        try {
            controllers.ReviewController reviewController = controllers.ReviewController.getInstance();
            List<Review> reviews = reviewController.getReviewsByProperty(currentProperty.getPropertyID());

            // Clear existing reviews
            reviewsContainer.getChildren().clear();

            if (reviews == null || reviews.isEmpty()) {
                Label noReviewsLabel = new Label("No reviews yet. Be the first to review this property!");
                noReviewsLabel.setStyle("-fx-text-fill: #95a5a6; -fx-font-style: italic; -fx-font-size: 14px;");
                reviewsContainer.getChildren().add(noReviewsLabel);
                averageRatingLabel.setText("⭐ N/A");
                reviewCountLabel.setText("(0 reviews)");
            } else {
                // Calculate average rating
                double avgRating = reviews.stream()
                        .mapToInt(Review::getRating)
                        .average()
                        .orElse(0.0);

                averageRatingLabel.setText(String.format("⭐ %.1f", avgRating));
                reviewCountLabel.setText("(" + reviews.size() + " review" + (reviews.size() > 1 ? "s" : "") + ")");

                // Display each review
                for (Review review : reviews) {
                    VBox reviewBox = createReviewCard(review);
                    reviewsContainer.getChildren().add(reviewBox);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            Label errorLabel = new Label("Error loading reviews");
            errorLabel.setStyle("-fx-text-fill: #e74c3c;");
            reviewsContainer.getChildren().add(errorLabel);
        }
    }

    /**
     * Create a review card UI component
     */
    private VBox createReviewCard(Review review) {
        VBox card = new VBox(8);
        card.setStyle(
                "-fx-background-color: #f8f9fa; -fx-padding: 15; -fx-background-radius: 5; -fx-border-color: #e9ecef; -fx-border-radius: 5;");

        // Rating stars
        StringBuilder stars = new StringBuilder();
        for (int i = 0; i < review.getRating(); i++) {
            stars.append("⭐");
        }
        Label ratingLabel = new Label(stars.toString() + " (" + review.getRating() + "/5)");
        ratingLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #f39c12; -fx-font-weight: bold;");

        // Review date
        Label dateLabel = new Label(review.getDate() != null ? review.getDate().toLocalDate().toString() : "");
        dateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #95a5a6;");

        // Review comment
        Label commentLabel = new Label(review.getComment());
        commentLabel.setWrapText(true);
        commentLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #2c3e50; -fx-line-spacing: 3;");

        card.getChildren().addAll(ratingLabel, dateLabel, commentLabel);
        return card;
    }
}
