package databases;

import models.Message;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageDAO {
    private Connection conn;

    public MessageDAO() {
        this.conn = DatabaseConnection.getInstance().getConnection();
    }

    public void sendMessage(String bookingID, String senderID, String content) {
        String sql = "INSERT INTO Messages (bookingID, senderID, content, sentAt) VALUES (?, ?, ?, SYSDATETIME())";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, bookingID);
            stmt.setString(2, senderID);
            stmt.setString(3, content);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Message> getMessagesForBooking(String bookingID) {
        List<Message> list = new ArrayList<>();
        String sql = "SELECT * FROM Messages WHERE bookingID = ? ORDER BY sentAt ASC";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, bookingID);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(new Message(
                        rs.getInt("messageID"),
                        rs.getString("bookingID"),
                        rs.getString("senderID"),
                        rs.getString("content"),
                        rs.getTimestamp("sentAt").toLocalDateTime()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}