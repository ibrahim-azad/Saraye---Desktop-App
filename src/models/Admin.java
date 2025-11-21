package models;

public class Admin extends User implements Notifiable {
    public Admin(String userID, String name, String email, String password, String phone) {
        super(userID, name, email, password, phone);
    }

    @Override
    public String getRole() {
        return "admin";
    }

    @Override
    public void sendNotification(String message) {
        System.out.println("Admin Alert to " + email + ": " + message);
    }
}