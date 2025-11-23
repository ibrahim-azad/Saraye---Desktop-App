package databases;

import models.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReviewDAO {
    private Connection conn;

    public ReviewDAO() {
        this.conn = DatabaseConnection.getInstance().getConnection();
    }

    public boolean addReview(Review review) {
        String sql = "INSERT INTO Reviews (reviewID, bookingID, rating, comment, reviewDate) VALUES (?, ?, ?, ?, SYSDATETIME())";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, review.getReviewID());
            stmt.setString(2, review.getBookingID());
            stmt.setInt(3, review.getRating());
            stmt.setString(4, review.getComment());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Review> getReviewsForProperty(String propertyID) {
        // This requires a JOIN because Reviews are linked to Bookings, not Properties
        // directly
        String sql = "SELECT r.* FROM Reviews r JOIN Bookings b ON r.bookingID = b.bookingID WHERE b.propertyID = ?";
        List<Review> list = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, propertyID);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(new Review(
                        rs.getString("reviewID"),
                        rs.getString("bookingID"),
                        rs.getInt("rating"),
                        rs.getString("comment"),
                        rs.getTimestamp("reviewDate").toLocalDateTime()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Create review (alias for addReview for controller compatibility)
    public boolean createReview(Review review) {
        return addReview(review);
    }

    // Get reviews by property ID (alias for getReviewsForProperty for controller
    // compatibility)
    public List<Review> getReviewsByPropertyId(String propertyID) {
        return getReviewsForProperty(propertyID);
    }
}