package models;

import java.time.LocalDateTime;

public class Message {
    private int messageID;
    private String bookingID;
    private String senderID;
    private String content;
    private LocalDateTime sentAt;

    public Message(int messageID, String bookingID, String senderID, String content, LocalDateTime sentAt) {
        this.messageID = messageID;
        this.bookingID = bookingID;
        this.senderID = senderID;
        this.content = content;
        this.sentAt = sentAt;
    }

    // Getters
    public int getMessageID() {
        return messageID;
    }

    public String getBookingID() {
        return bookingID;
    }

    public String getSenderID() {
        return senderID;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }
}