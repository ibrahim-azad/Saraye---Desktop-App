package databases;

import models.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReportDAO {
    protected Connection conn;

    public ReportDAO() {
        this.conn = DatabaseConnection.getInstance().getConnection();
    }

    // Constructor for mock subclasses to skip database connection
    public ReportDAO(boolean skipConnection) {
        if (!skipConnection) {
            this.conn = DatabaseConnection.getInstance().getConnection();
        }
    }

    public boolean createReport(Report report) {
        String sql = "INSERT INTO Reports (reportID, propertyID, reporterID, description, status) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, report.getReportID());
            stmt.setString(2, report.getPropertyId());
            stmt.setString(3, report.getReporterId());
            stmt.setString(4, report.getDescription());
            stmt.setString(5, "OPEN");
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
                        rs.getString("propertyID"),
                        rs.getString("reporterID"),
                        rs.getString("description"),
                        rs.getString("status"));
                list.add(report);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Get pending reports (alias for getOpenReports for controller compatibility)
    public List<Report> getPendingReports() {
        List<Report> list = new ArrayList<>();
        String sql = "SELECT * FROM Reports WHERE status IN ('OPEN', 'PENDING')";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Report report = new Report(
                        rs.getString("reportID"),
                        rs.getString("propertyID"),
                        rs.getString("reporterID"),
                        rs.getString("description"),
                        rs.getString("status"));
                list.add(report);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Update report status
    public boolean updateReportStatus(String reportID, String status) {
        String sql = "UPDATE Reports SET status = ? WHERE reportID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setString(2, reportID);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}