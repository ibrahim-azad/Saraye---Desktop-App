package databases;

import models.*;
import java.sql.*;

public class PaymentDAO {
    private Connection conn;

    public PaymentDAO() {
        this.conn = DatabaseConnection.getInstance().getConnection();
    }

    public boolean processPayment(Payment payment) {
        String sql = "INSERT INTO Payments (paymentID, bookingID, amount, paymentMethod, status, transactionDate) VALUES (?, ?, ?, ?, ?, SYSDATETIME())";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, payment.getPaymentID());
            stmt.setString(2, payment.getBookingID());
            stmt.setDouble(3, payment.getAmount());
            stmt.setString(4, payment.getMethod());
            stmt.setString(5, payment.getStatus()); // Use actual status from payment object
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}