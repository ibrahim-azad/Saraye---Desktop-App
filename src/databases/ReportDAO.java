package databases;

import models.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReportDAO {
    private Connection conn;

    public ReportDAO() {
        this.conn = DatabaseConnection.getInstance().getConnection();
    }

    public boolean createReport(Report report) {
        // Assuming you add a 'description' column to Report table in schema if missing
        String sql = "INSERT INTO Reports (reportID, description, status) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, report.getReportID());
            stmt.setString(2, report.getDescription());
            stmt.setString(3, "OPEN");
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Report> getOpenReports() {
        List<Report> list = new ArrayList<>();
        String sql = "SELECT * FROM Reports WHERE status = 'OPEN'";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Report report = new Report(
                        rs.getString("reportID"),
                        rs.getString("description"),
                        rs.getString("status"));
                list.add(report);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}