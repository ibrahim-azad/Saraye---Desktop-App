package models;

public class Host extends User {
    public Host(String userID, String name, String email, String password, String phone) {
        super(userID, name, email, password, phone);
    }

    @Override
    public String getRole() {
        return "HOST";
    }
}
