package models;

import java.time.LocalDateTime;
import java.util.UUID;

public class Payment {
    private String paymentID;
    private String bookingID;
    private double amount;
    private String method; // CREDIT_CARD, JAZZCASH
    private String status; // COMPLETED, FAILED, REFUNDED
    private LocalDateTime transactionDate;

    public Payment(String bookingID, double amount, String method) {
        this.paymentID = UUID.randomUUID().toString();
        this.bookingID = bookingID;
        this.amount = amount;
        this.method = method;
        this.status = "COMPLETED"; // Assuming instant success for MVP
        this.transactionDate = LocalDateTime.now();
    }

    // Constructor for loading from database
    public Payment(String paymentID, String bookingID, double amount, String method, String status,
            LocalDateTime transactionDate) {
        this.paymentID = paymentID;
        this.bookingID = bookingID;
        this.amount = amount;
        this.method = method;
        this.status = status;
        this.transactionDate = transactionDate;
    }

    // Getters
    public String getPaymentID() {
        return paymentID;
    }

    public String getBookingID() {
        return bookingID;
    }

    public double getAmount() {
        return amount;
    }

    public String getMethod() {
        return method;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }
}