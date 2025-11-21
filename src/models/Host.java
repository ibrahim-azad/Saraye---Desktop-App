package models;

public class Host extends User {
    private String phoneNumber;

    public Host(String userID, String name, String email, String password) {
        super(userID, name, email, password);
    }

    @Override
    public String getRole() {
        return "HOST";
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
