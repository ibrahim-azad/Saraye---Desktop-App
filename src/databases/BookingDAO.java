package databases;

import models.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {
    private Connection conn;

    public BookingDAO() {
        this.conn = DatabaseConnection.getInstance().getConnection();
    }

    public boolean createBooking(Booking booking) {
        String sql = "INSERT INTO Bookings (bookingID, guestID, propertyID, checkInDate, checkOutDate, totalPrice, status, createdAt) VALUES (?, ?, ?, ?, ?, ?, ?, SYSDATETIME())";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, booking.getBookingID());
            stmt.setString(2, booking.getGuestID());
            stmt.setString(3, booking.getPropertyID());
            stmt.setDate(4, Date.valueOf(booking.getCheckInDate()));
            stmt.setDate(5, Date.valueOf(booking.getCheckOutDate()));
            stmt.setDouble(6, booking.getTotalPrice());
            stmt.setString(7, "PENDING");
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Booking> getBookingsByGuest(String guestID) {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT * FROM Bookings WHERE guestID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, guestID);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Booking b = new Booking(
                        rs.getString("bookingID"),
                        rs.getString("guestID"),
                        rs.getString("propertyID"),
                        rs.getDate("checkInDate").toLocalDate(),
                        rs.getDate("checkOutDate").toLocalDate(),
                        rs.getDouble("totalPrice"),
                        rs.getString("status"));
                list.add(b);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean updateStatus(String bookingID, String newStatus) {
        String sql = "UPDATE Bookings SET status = ? WHERE bookingID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newStatus);
            stmt.setString(2, bookingID);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    // ALIAS: Match controller naming
    public List<Booking> getBookingsByGuestId(String guestID) throws SQLException {
        return getBookingsByGuest(guestID);
    }
    
    // ALIAS: Match controller naming
    public boolean updateBookingStatus(String bookingID, String newStatus) throws SQLException {
        return updateStatus(bookingID, newStatus);
    }
    
    // GET BY ID: Fetch single booking
    public Booking getBookingById(String bookingID) throws SQLException {
        String sql = "SELECT * FROM Bookings WHERE bookingID = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, bookingID);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new Booking(
                    rs.getString("bookingID"),
                    rs.getString("guestID"),
                    rs.getString("propertyID"),
                    rs.getDate("checkInDate").toLocalDate(),
                    rs.getDate("checkOutDate").toLocalDate(),
                    rs.getDouble("totalPrice"),
                    rs.getString("status")
                );
            }
        }
        return null;
    }
    
    // GET PENDING FOR HOST: Fetch bookings for host's properties
    public List<Booking> getPendingBookingsByHostId(String hostID) throws SQLException {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT b.* FROM Bookings b " +
                     "JOIN Properties p ON b.propertyID = p.propertyID " +
                     "WHERE p.hostID = ? AND b.status = 'PENDING'";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, hostID);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Booking b = new Booking(
                    rs.getString("bookingID"),
                    rs.getString("guestID"),
                    rs.getString("propertyID"),
                    rs.getDate("checkInDate").toLocalDate(),
                    rs.getDate("checkOutDate").toLocalDate(),
                    rs.getDouble("totalPrice"),
                    rs.getString("status")
                );
                list.add(b);
            }
        }
        return list;
    }
}