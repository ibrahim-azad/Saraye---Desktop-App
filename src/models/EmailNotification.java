package models;

public class EmailNotification extends Notification {
    public EmailNotification(String id, String msg) {
        super(id, msg);
    }

    @Override
    public void send() {
        System.out.println("📧 Sending Email: " + getMessage());
        // Logic to call Email API...
    }
}
