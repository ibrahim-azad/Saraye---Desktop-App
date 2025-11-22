package controllers;

import models.Booking;
import databases.BookingDAO;
import utils.SessionManager;
import java.sql.SQLException;
import java.util.List;

/**
 * BookingController - Business Logic Layer
 * Handles booking requests, approvals, and cancellations
 * UC4: Request Booking
 * UC6: Host Approve/Decline Booking
 * UC7: Cancel Booking
 */
public class BookingController {
    
    private static BookingController instance;
    private BookingDAO bookingDAO;
    private SessionManager sessionManager;
    
    private BookingController() {
        this.bookingDAO = new BookingDAO();
        this.sessionManager = SessionManager.getInstance();
    }
    
    public static BookingController getInstance() {
        if (instance == null) {
            instance = new BookingController();
        }
        return instance;
    }
    
    /**
     * Create a new booking request
     * UC4: Request Booking
     */
    /**
     * Create a new booking request
     * UC4: Request Booking
     */
    public String createBooking(String propertyId, String checkIn, String checkOut, double totalPrice) {
        try {
            if (!sessionManager.isLoggedIn()) {
                return "You must be logged in to make a booking";
            }
            
            String guestId = sessionManager.getCurrentUserId();
            
            // Convert String dates to LocalDate
            java.time.LocalDate checkInDate = java.time.LocalDate.parse(checkIn);
            java.time.LocalDate checkOutDate = java.time.LocalDate.parse(checkOut);
            
            // Create booking object
            Booking booking = new Booking(guestId, propertyId, checkInDate, checkOutDate, totalPrice);
            
            boolean success = bookingDAO.createBooking(booking);
            return success ? "SUCCESS" : "Failed to create booking";
            
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
    
    /**
     * Get all bookings for a guest
     */
    public List<Booking> getGuestBookings(String guestId) {
        try {
            return bookingDAO.getBookingsByGuestId(guestId);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Get pending bookings for a host
     * UC6: Host Approve/Decline Booking
     */
    public List<Booking> getPendingBookingsForHost(String hostId) {
        try {
            return bookingDAO.getPendingBookingsByHostId(hostId);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Approve a booking
     * UC6: Host Approve/Decline Booking
     */
    public String approveBooking(String bookingId) {
        try {
            boolean success = bookingDAO.updateBookingStatus(bookingId, "APPROVED");
            return success ? "SUCCESS" : "Failed to approve booking";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Database error: " + e.getMessage();
        }
    }
    
    /**
     * Decline a booking
     * UC6: Host Approve/Decline Booking
     */
    public String declineBooking(String bookingId) {
        try {
            boolean success = bookingDAO.updateBookingStatus(bookingId, "DECLINED");
            return success ? "SUCCESS" : "Failed to decline booking";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Database error: " + e.getMessage();
        }
    }
    
    /**
     * Cancel a booking
     * UC7: Cancel Booking
     */
    public String cancelBooking(String bookingId) {
        try {
            boolean success = bookingDAO.updateBookingStatus(bookingId, "CANCELLED");
            return success ? "SUCCESS" : "Failed to cancel booking";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Database error: " + e.getMessage();
        }
    }
    
    /**
     * Get booking by ID
     */
    public Booking getBookingById(String bookingId) {
        try {
            return bookingDAO.getBookingById(bookingId);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}