package ui.utils;

import java.time.LocalDate;
import java.util.regex.Pattern;

/**
 * ValidationUtil - Utility class for input validation
 * GRASP Pattern: Information Expert - Knows validation rules
 */
public class ValidationUtil {

    // Email pattern
    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    // Phone pattern (10-15 digits, may include spaces and dashes)
    private static final Pattern PHONE_PATTERN =
        Pattern.compile("^[\\d\\s-]{10,15}$");

    /**
     * Validate email format
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Validate password strength
     * At least 6 characters
     */
    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }

    /**
     * Validate phone number format
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    /**
     * Check if string is a positive integer
     */
    public static boolean isPositiveInteger(String str) {
        if (str == null || str.trim().isEmpty()) {
            return false;
        }
        try {
            int num = Integer.parseInt(str.trim());
            return num > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Check if string is a positive double
     */
    public static boolean isPositiveDouble(String str) {
        if (str == null || str.trim().isEmpty()) {
            return false;
        }
        try {
            double num = Double.parseDouble(str.trim());
            return num > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Check if string is not empty
     */
    public static boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }

    /**
     * Validate date range (endDate must be after startDate)
     */
    public static boolean isValidDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return false;
        }
        return endDate.isAfter(startDate);
    }

    /**
     * Check if date is in the future
     */
    public static boolean isFutureDate(LocalDate date) {
        if (date == null) {
            return false;
        }
        return date.isAfter(LocalDate.now());
    }

    /**
     * Parse integer safely
     */
    public static Integer parseIntOrNull(String str) {
        if (str == null || str.trim().isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(str.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Parse double safely
     */
    public static Double parseDoubleOrNull(String str) {
        if (str == null || str.trim().isEmpty()) {
            return null;
        }
        try {
            return Double.parseDouble(str.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Get validation error message for email
     */
    public static String getEmailErrorMessage() {
        return "Please enter a valid email address (e.g., user@example.com)";
    }

    /**
     * Get validation error message for password
     */
    public static String getPasswordErrorMessage() {
        return "Password must be at least 6 characters long";
    }

    /**
     * Get validation error message for phone
     */
    public static String getPhoneErrorMessage() {
        return "Please enter a valid phone number (10-15 digits)";
    }
}
