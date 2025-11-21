package models;

// Abstract Base Class
public abstract class User {
    protected String userID;
    protected String name;
    protected String email;
    protected String password;
    protected String phone;

    public User(String userID, String name, String email, String password, String phone) {
        this.userID = userID;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
    }

    // Getters and Setters
    public String getUserId() {
        return userID;
    }

    public void setUserId(String userID) {
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setRole(String role) {
        // Default implementation - can be overridden
    }

    // Abstract method forcing polymorphism if needed
    public abstract String getRole();
}