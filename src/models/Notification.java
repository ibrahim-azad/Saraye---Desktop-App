package models;

import java.time.LocalDateTime;

public abstract class Notification {
    protected String notificationID;
    protected String message;
    protected LocalDateTime sentAt;

    public Notification(String notificationID, String message) {
        this.notificationID = notificationID;
        this.message = message;
        this.sentAt = LocalDateTime.now();
    }

    // Getters
    public String getNotificationID() {
        return notificationID;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    // Polymorphic Method
    public abstract void send();
}