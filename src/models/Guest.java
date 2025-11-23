package models;

public class Guest extends User {
    public Guest(String userID, String name, String email, String password, String phone) {
        super(userID, name, email, password, phone);
    }

    @Override
    public String getRole() {
        return "GUEST";
    }
}
