package models;

import java.time.LocalDate;
import java.util.UUID;

public class Booking {
    private String bookingID;
    private String guestID;
    private String propertyID;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private double totalPrice;
    private String status; // PENDING, CONFIRMED, CANCELLED

    // Constructor
    public Booking(String guestID, String propertyID, LocalDate checkIn, LocalDate checkOut, double price) {
        this.bookingID = UUID.randomUUID().toString(); // Generate ID automatically
        this.guestID = guestID;
        this.propertyID = propertyID;
        this.checkInDate = checkIn;
        this.checkOutDate = checkOut;
        this.totalPrice = price;
        this.status = "PENDING"; // Default status
    }

    // Constructor for loading from database
    public Booking(String bookingID, String guestID, String propertyID, LocalDate checkIn, LocalDate checkOut,
            double price, String status) {
        this.bookingID = bookingID;
        this.guestID = guestID;
        this.propertyID = propertyID;
        this.checkInDate = checkIn;
        this.checkOutDate = checkOut;
        this.totalPrice = price;
        this.status = status;
    }

    // Getters and Setters
    public String getBookingID() {
        return bookingID;
    }

    public String getGuestID() {
        return guestID;
    }

    public String getPropertyID() {
        return propertyID;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setBookingID(String bookingID) {
        this.bookingID = bookingID;
    }
}