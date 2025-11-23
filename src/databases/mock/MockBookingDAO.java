package databases.mock;

import models.*;
import databases.BookingDAO;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of BookingDAO for UI-only development without database.
 * Contains hardcoded sample bookings for testing UI components.
 * Extends BookingDAO to allow polymorphic usage in DAOFactory.
 */
public class MockBookingDAO extends BookingDAO {
    private List<Booking> bookings;
    private int bookingCounter = 5;

    public MockBookingDAO() {
        super(true); // Pass true to skip database initialization
        initializeSampleData();
    }

    private void initializeSampleData() {
        bookings = new ArrayList<>();

        // Sample bookings with different statuses
        bookings.add(new Booking(
                "B001", "G001", "P001",
                LocalDate.of(2025, 12, 1), LocalDate.of(2025, 12, 5),
                600.0, "PENDING"));

        bookings.add(new Booking(
                "B002", "G001", "P002",
                LocalDate.of(2025, 12, 10), LocalDate.of(2025, 12, 15),
                1000.0, "APPROVED"));

        bookings.add(new Booking(
                "B003", "G002", "P001",
                LocalDate.of(2025, 11, 20), LocalDate.of(2025, 11, 25),
                750.0, "COMPLETED"));

        bookings.add(new Booking(
                "B004", "G002", "P003",
                LocalDate.of(2025, 12, 20), LocalDate.of(2025, 12, 27),
                1260.0, "APPROVED"));

        bookings.add(new Booking(
                "B005", "G003", "P002",
                LocalDate.of(2025, 11, 5), LocalDate.of(2025, 11, 10),
                1000.0, "DECLINED"));
    }

    public boolean saveBooking(Booking booking) {
        bookings.add(booking);
        return true;
    }

    public List<Booking> getBookingsByGuest(String guestID) {
        List<Booking> guestBookings = new ArrayList<>();
        for (Booking booking : bookings) {
            if (booking.getGuestID().equals(guestID)) {
                guestBookings.add(booking);
            }
        }
        return guestBookings;
    }

    public List<Booking> getBookingsByHost(String hostID) {
        // Mock: Filter by properties belonging to this host
        // In real DAO, this requires JOIN with Properties table
        List<Booking> hostBookings = new ArrayList<>();
        MockPropertyDAO propDAO = new MockPropertyDAO();
        List<Property> hostProps = propDAO.getPropertiesByHost(hostID);

        for (Booking booking : bookings) {
            for (Property prop : hostProps) {
                if (booking.getPropertyID().equals(prop.getPropertyID())) {
                    hostBookings.add(booking);
                    break;
                }
            }
        }
        return hostBookings;
    }

    public Booking getBookingById(String bookingID) {
        for (Booking booking : bookings) {
            if (booking.getBookingID().equals(bookingID)) {
                return booking;
            }
        }
        return null;
    }

    public boolean updateBookingStatus(String bookingID, String newStatus) {
        for (Booking booking : bookings) {
            if (booking.getBookingID().equals(bookingID)) {
                booking.setStatus(newStatus);
                return true;
            }
        }
        return false;
    }

    public boolean deleteBooking(String bookingID) {
        return bookings.removeIf(b -> b.getBookingID().equals(bookingID));
    }

    public String generateBookingID() {
        return String.format("B%03d", ++bookingCounter);
    }

    public List<Booking> getCompletedBookingsForGuest(String guestID) {
        List<Booking> completed = new ArrayList<>();
        for (Booking booking : bookings) {
            if (booking.getGuestID().equals(guestID) &&
                    "COMPLETED".equalsIgnoreCase(booking.getStatus())) {
                completed.add(booking);
            }
        }
        return completed;
    }

    public boolean hasGuestStayedAtProperty(String guestID, String propertyID) {
        for (Booking booking : bookings) {
            if (booking.getGuestID().equals(guestID) &&
                    booking.getPropertyID().equals(propertyID) &&
                    "COMPLETED".equalsIgnoreCase(booking.getStatus())) {
                return true;
            }
        }
        return false;
    }

    public List<Booking> getAllBookings() {
        return new ArrayList<>(bookings);
    }
}
