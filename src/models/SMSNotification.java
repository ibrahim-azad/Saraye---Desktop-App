package models;

public class SMSNotification extends Notification {
    public SMSNotification(String id, String msg) {
        super(id, msg);
    }

    @Override
    public void send() {
        System.out.println("📱 Sending SMS: " + getMessage());
    }
}
