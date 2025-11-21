package models;

public class Admin extends User implements Notifiable {
    public Admin(String userID, String name, String email, String password) {
        super(userID, name, email, password);
    }

    @Override
    public String getRole() {
        return "ADMIN";
    }

    @Override
    public void sendNotification(String message) {
        System.out.println("Admin Alert to " + email + ": " + message);
    }
}