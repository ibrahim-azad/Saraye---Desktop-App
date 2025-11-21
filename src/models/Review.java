package models;

import java.time.LocalDateTime;
import java.util.UUID;

public class Review {
    private String reviewID;
    private String bookingID;
    private int rating; // 1-5
    private String comment;
    private LocalDateTime date;

    public Review(String bookingID, int rating, String comment) {
        this.reviewID = UUID.randomUUID().toString();
        this.bookingID = bookingID;
        this.rating = rating;
        this.comment = comment;
        this.date = LocalDateTime.now();
    }

    // Constructor for loading from database
    public Review(String reviewID, String bookingID, int rating, String comment, LocalDateTime date) {
        this.reviewID = reviewID;
        this.bookingID = bookingID;
        this.rating = rating;
        this.comment = comment;
        this.date = date;
    }

    // Getters
    public String getReviewID() {
        return reviewID;
    }

    public String getBookingID() {
        return bookingID;
    }

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public LocalDateTime getDate() {
        return date;
    }
}