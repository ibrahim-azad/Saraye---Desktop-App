package databases;

import models.*;
import java.sql.*;

public class NotificationDAO {
    private Connection conn;

    public NotificationDAO() {
        this.conn = DatabaseConnection.getInstance().getConnection();
    }

    public boolean saveNotification(Notification notif, String userID) {
        String sql = "INSERT INTO Notifications (notificationID, userID, message, type, isRead, createdAt) VALUES (?, ?, ?, ?, 0, SYSDATETIME())";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, notif.getNotificationID());
            stmt.setString(2, userID);
            stmt.setString(3, notif.getMessage());

            // Store the type string for Polymorphism (EMAIL, SMS)
            if (notif instanceof EmailNotification)
                stmt.setString(4, "EMAIL");
            else if (notif instanceof SMSNotification)
                stmt.setString(4, "SMS");
            else
                stmt.setString(4, "IN_APP");

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}