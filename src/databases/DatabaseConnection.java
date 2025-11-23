package databases;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;

    // Connection String for MS SQL Server Express with SQL Authentication
    // Using dedicated app login credentials
    private String url = "jdbc:sqlserver://DESKTOP-G97U64S\\SQLEXPRESS;databaseName=SarayeDB;user=saraye_app;password=Saraye@2025;encrypt=false;";

    private DatabaseConnection() {
        try {
            // Load the SQL Server JDBC driver explicitly
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            System.out.println("📦 JDBC Driver loaded successfully");

            connection = DriverManager.getConnection(url);
            System.out.println("✅ Database Connected Successfully!");
            System.out.println("   Connection URL: " + url);
        } catch (ClassNotFoundException e) {
            System.err.println("❌ JDBC Driver not found: " + e.getMessage());
            System.err.println("   Make sure mssql-jdbc driver is in classpath");
        } catch (SQLException e) {
            System.err.println("❌ Database Connection Failed: " + e.getMessage());
            System.err.println("   Connection URL: " + url);
            System.err.println("   SQL State: " + e.getSQLState());
            System.err.println("   Error Code: " + e.getErrorCode());
            e.printStackTrace();
        }
    }

    public static synchronized DatabaseConnection getInstance() {
        try {
            if (instance == null || instance.getConnection() == null || instance.getConnection().isClosed()) {
                instance = new DatabaseConnection();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("❌ Failed to get database instance: " + e.getMessage());
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}