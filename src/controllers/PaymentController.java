package controllers;

import models.Payment;
import databases.PaymentDAO;
import utils.SessionManager;
import java.sql.SQLException;

/**
 * PaymentController - Business Logic Layer
 * Handles payment processing
 * UC5: Make Payment
 */
public class PaymentController {
    
    private static PaymentController instance;
    private PaymentDAO paymentDAO;
    private SessionManager sessionManager;
    
    private PaymentController() {
        this.paymentDAO = new PaymentDAO();
        this.sessionManager = SessionManager.getInstance();
    }
    
    public static PaymentController getInstance() {
        if (instance == null) {
            instance = new PaymentController();
        }
        return instance;
    }
    
    /**
     * Process payment for a booking
     * UC5: Make Payment
     */
    /**
     * Process payment for a booking
     * UC5: Make Payment
     */
    public String processPayment(String bookingId, double amount, String paymentMethod) {
        try {
            if (!sessionManager.isLoggedIn()) {
                return "You must be logged in to make a payment";
            }
            
            // Create payment object (status auto-set to COMPLETED)
            Payment payment = new Payment(bookingId, amount, paymentMethod);
            
            boolean success = paymentDAO.createPayment(payment);
            
            if (success) {
                return "SUCCESS";
            } else {
                return "Payment processing failed";
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
    
    /**
     * Get payment by booking ID
     */
    public Payment getPaymentByBookingId(String bookingId) {
        try {
            return paymentDAO.getPaymentByBookingId(bookingId);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}