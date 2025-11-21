package databases;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;
    // Connection String for MS SQL Server (Integrated Security=false usually for simple setups)
    // Ensure TCP/IP is enabled in SQL Server Configuration Manager
    private String url = "jdbc:sqlserver://localhost:1433;databaseName=SarayeDB;encrypt=true;trustServerCertificate=true;";
    private String username = "sa"; // Or your SSMS username
    private String password = "yourStrongPassword123"; // Or your SSMS password

    private DatabaseConnection() {
        try {
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("✅ Database Connected Successfully!");
        } catch (SQLException e) {
            System.err.println("❌ Database Connection Failed: " + e.getMessage());
        }
    }

    public static synchronized DatabaseConnection getInstance() {
        try {
            if (instance == null || instance.getConnection().isClosed()) {
                instance = new DatabaseConnection();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}