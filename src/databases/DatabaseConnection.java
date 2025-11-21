package databases;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;

    // Connection String for MS SQL Server with Windows Authentication
    private String url = "jdbc:sqlserver://localhost:1433;databaseName=SarayeDB;integratedSecurity=true;encrypt=true;trustServerCertificate=true;";

    private DatabaseConnection() {
        try {
            connection = DriverManager.getConnection(url);
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