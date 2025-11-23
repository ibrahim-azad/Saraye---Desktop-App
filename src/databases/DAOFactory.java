package databases;

import databases.mock.*;

/**
 * Factory class to provide DAO instances.
 * Switch between real database DAOs and mock DAOs for UI-only development.
 * 
 * To enable UI-only mode without database:
 * Set UI_ONLY_MODE = true
 * 
 * To use real database:
 * Set UI_ONLY_MODE = false
 */
public class DAOFactory {

    /**
     * Toggle this flag to switch between mock and real database implementations.
     * true = Use mock DAOs (no database required)
     * false = Use real DAOs (requires MS SQL Server connection)
     */
    public static final boolean UI_ONLY_MODE = false;

    // Singleton instances for mock DAOs (to maintain state across the app in
    // UI-only mode)
    private static MockPropertyDAO mockPropertyDAO;
    private static MockBookingDAO mockBookingDAO;
    private static MockUserDAO mockUserDAO;
    private static MockReviewDAO mockReviewDAO;

    /**
     * Get PropertyDAO instance (real or mock based on UI_ONLY_MODE).
     */
    public static PropertyDAO getPropertyDAO() {
        if (UI_ONLY_MODE) {
            if (mockPropertyDAO == null) {
                mockPropertyDAO = new MockPropertyDAO();
            }
            return mockPropertyDAO;
        } else {
            return new PropertyDAO();
        }
    }

    /**
     * Get BookingDAO instance (real or mock based on UI_ONLY_MODE).
     */
    public static BookingDAO getBookingDAO() {
        if (UI_ONLY_MODE) {
            if (mockBookingDAO == null) {
                mockBookingDAO = new MockBookingDAO();
            }
            return mockBookingDAO;
        } else {
            return new BookingDAO();
        }
    }

    /**
     * Get UserDAO instance (real or mock based on UI_ONLY_MODE).
     */
    public static UserDAO getUserDAO() {
        if (UI_ONLY_MODE) {
            if (mockUserDAO == null) {
                mockUserDAO = new MockUserDAO();
            }
            return mockUserDAO;
        } else {
            return new UserDAO();
        }
    }

    /**
     * Get ReviewDAO instance (real or mock based on UI_ONLY_MODE).
     */
    public static ReviewDAO getReviewDAO() {
        if (UI_ONLY_MODE) {
            if (mockReviewDAO == null) {
                mockReviewDAO = new MockReviewDAO();
            }
            return mockReviewDAO;
        } else {
            return new ReviewDAO();
        }
    }

    /**
     * Reset all mock DAO singletons (useful for testing).
     */
    public static void resetMocks() {
        mockPropertyDAO = null;
        mockBookingDAO = null;
        mockUserDAO = null;
        mockReviewDAO = null;
    }
}
