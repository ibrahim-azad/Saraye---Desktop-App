package models;

import java.time.LocalDate;

/**
 * Booking Model - Temporary placeholder for UI
 * Will be replaced by Ibrahim's business logic implementation
 */
public class Booking {
    private int bookingId;
    private int propertyId;
    private int guestId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private int numGuests;
    private double totalPrice;
    private String status; // "pending", "approved", "declined", "cancelled"

    // Additional fields for UI display
    private String guestName;
    private String propertyTitle;

    // Constructors
    public Booking() {}

    public Booking(int bookingId, int propertyId, int guestId, LocalDate checkInDate,
                   LocalDate checkOutDate, int numGuests, double totalPrice, String status) {
        this.bookingId = bookingId;
        this.propertyId = propertyId;
        this.guestId = guestId;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.numGuests = numGuests;
        this.totalPrice = totalPrice;
        this.status = status;
    }

    // Getters and Setters
    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public int getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(int propertyId) {
        this.propertyId = propertyId;
    }

    public int getGuestId() {
        return guestId;
    }

    public void setGuestId(int guestId) {
        this.guestId = guestId;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(LocalDate checkInDate) {
        this.checkInDate = checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(LocalDate checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public int getNumGuests() {
        return numGuests;
    }

    public void setNumGuests(int numGuests) {
        this.numGuests = numGuests;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public String getPropertyTitle() {
        return propertyTitle;
    }

    public void setPropertyTitle(String propertyTitle) {
        this.propertyTitle = propertyTitle;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "bookingId=" + bookingId +
                ", propertyTitle='" + propertyTitle + '\'' +
                ", status='" + status + '\'' +
                ", checkInDate=" + checkInDate +
                ", checkOutDate=" + checkOutDate +
                '}';
    }
}
