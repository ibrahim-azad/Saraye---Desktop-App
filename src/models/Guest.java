package models;

public class Guest extends User {
    public Guest(String userID, String name, String email, String password) {
        super(userID, name, email, password);
    }

    @Override
    public String getRole() {
        return "GUEST";
    }
}
