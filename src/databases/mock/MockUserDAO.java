package databases.mock;

import models.*;
import databases.UserDAO;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of UserDAO for UI-only development without database.
 * Contains hardcoded sample users for testing UI components.
 * Extends UserDAO to allow polymorphic usage in DAOFactory.
 */
public class MockUserDAO extends UserDAO {
    private List<User> users;
    private int guestCounter = 3;
    private int hostCounter = 2;
    private int adminCounter = 1;

    public MockUserDAO() {
        super(true); // Pass true to skip database initialization
        initializeSampleData();
    }

    private void initializeSampleData() {
        users = new ArrayList<>();

        // Sample guests (userID, name, email, password, phone)
        Guest guest1 = new Guest("G001", "John Doe", "john@email.com", "password123", "1234567890");
        users.add(guest1);

        Guest guest2 = new Guest("G002", "Jane Smith", "jane@email.com", "password123", "0987654321");
        users.add(guest2);

        Guest guest3 = new Guest("G003", "Bob Wilson", "bob@email.com", "password123", "5555555555");
        users.add(guest3);

        // Sample hosts
        Host host1 = new Host("H001", "Alice Johnson", "alice@email.com", "password123", "1111111111");
        users.add(host1);

        Host host2 = new Host("H002", "Charlie Brown", "charlie@email.com", "password123", "2222222222");
        users.add(host2);

        // Sample admin
        Admin admin1 = new Admin("A001", "Admin User", "admin@email.com", "admin123", "0000000000");
        users.add(admin1);
    }

    public boolean saveUser(User user) {
        // Auto-generate ID if not set
        if (user.getUserId() == null || user.getUserId().isEmpty()) {
            user.setUserId(generateUserID(user.getRole()));
        }
        users.add(user);
        return true;
    }

    public User getUserByEmail(String email) {
        for (User user : users) {
            if (user.getEmail().equalsIgnoreCase(email)) {
                return user;
            }
        }
        return null;
    }

    public User getUserById(String userID) {
        for (User user : users) {
            if (user.getUserId().equals(userID)) {
                return user;
            }
        }
        return null;
    }

    public boolean updateUser(User user) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUserId().equals(user.getUserId())) {
                users.set(i, user);
                return true;
            }
        }
        return false;
    }

    public boolean deleteUser(String userID) {
        return users.removeIf(u -> u.getUserId().equals(userID));
    }

    public boolean emailExists(String email) {
        return getUserByEmail(email) != null;
    }

    public User validateLogin(String email, String password) {
        User user = getUserByEmail(email);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    public String generateUserID(String role) {
        switch (role.toUpperCase()) {
            case "GUEST":
                return String.format("G%03d", ++guestCounter);
            case "HOST":
                return String.format("H%03d", ++hostCounter);
            case "ADMIN":
                return String.format("A%03d", ++adminCounter);
            default:
                return String.format("U%03d", guestCounter++);
        }
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    public List<Guest> getAllGuests() {
        List<Guest> guests = new ArrayList<>();
        for (User user : users) {
            if (user instanceof Guest) {
                guests.add((Guest) user);
            }
        }
        return guests;
    }

    public List<Host> getAllHosts() {
        List<Host> hosts = new ArrayList<>();
        for (User user : users) {
            if (user instanceof Host) {
                hosts.add((Host) user);
            }
        }
        return hosts;
    }
}
