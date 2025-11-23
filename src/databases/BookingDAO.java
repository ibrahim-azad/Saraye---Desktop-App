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

    // Protected constructor for mock DAOs (skips database initialization)
    protected BookingDAO(boolean skipInit) {
        this.conn = null;
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
        String sql = "SELECT b.*, p.title as propertyTitle " +
                "FROM Bookings b " +
                "JOIN Properties p ON b.propertyID = p.propertyID " +
                "WHERE b.guestID = ?";
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
                b.setPropertyTitle(rs.getString("propertyTitle"));
                list.add(b);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Booking> getBookingsByHost(String hostID) {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT b.*, u.name as guestName, p.title as propertyTitle " +
                "FROM Bookings b " +
                "JOIN Properties p ON b.propertyID = p.propertyID " +
                "JOIN Users u ON b.guestID = u.userID " +
                "WHERE p.hostID = ?";
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
                        rs.getString("status"));
                b.setGuestName(rs.getString("guestName"));
                b.setPropertyTitle(rs.getString("propertyTitle"));
                // numGuests column doesn't exist in Bookings table
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

    // Get bookings by guest ID (alias for getBookingsByGuest for controller
    // compatibility)
    public List<Booking> getBookingsByGuestId(String guestID) {
        return getBookingsByGuest(guestID);
    }

    // Get pending bookings for a host
    public List<Booking> getPendingBookingsByHostId(String hostID) {
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
                        rs.getString("status"));
                list.add(b);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Update booking status (alias for updateStatus for controller compatibility)
    public boolean updateBookingStatus(String bookingID, String status) {
        return updateStatus(bookingID, status);
    }

    // Get single booking by ID
    public Booking getBookingById(String bookingID) {
        String sql = "SELECT b.*, p.title as propertyTitle " +
                "FROM Bookings b " +
                "JOIN Properties p ON b.propertyID = p.propertyID " +
                "WHERE b.bookingID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, bookingID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Booking b = new Booking(
                        rs.getString("bookingID"),
                        rs.getString("guestID"),
                        rs.getString("propertyID"),
                        rs.getDate("checkInDate").toLocalDate(),
                        rs.getDate("checkOutDate").toLocalDate(),
                        rs.getDouble("totalPrice"),
                        rs.getString("status"));
                b.setPropertyTitle(rs.getString("propertyTitle"));
                return b;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Generate next Booking ID in format B001, B002, etc.
     */
    public String generateBookingID() {
        String sql = "SELECT TOP 1 bookingID FROM Bookings WHERE bookingID LIKE 'B%' ORDER BY bookingID DESC";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                String lastID = rs.getString("bookingID");
                // Extract number from B001 -> 001
                int number = Integer.parseInt(lastID.substring(1));
                // Increment and format
                return String.format("B%03d", number + 1);
            } else {
                return "B001"; // First booking
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "B001";
        }
    }

    public List<Booking> getAllBookings() {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM Bookings";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Booking b = new Booking(
                        rs.getString("bookingID"),
                        rs.getString("guestID"),
                        rs.getString("propertyID"),
                        rs.getDate("checkInDate").toLocalDate(),
                        rs.getDate("checkOutDate").toLocalDate(),
                        rs.getDouble("totalPrice"),
                        rs.getString("status"));
                bookings.add(b);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }
}