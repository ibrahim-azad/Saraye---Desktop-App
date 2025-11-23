package databases.mock;

import models.Report;
import databases.ReportDAO;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mock implementation of ReportDAO for UI-only development without database.
 * Contains sample reports for testing moderation features.
 */
public class MockReportDAO extends ReportDAO {
    private List<Report> reports;
    private int reportCounter = 1;

    public MockReportDAO() {
        super(true); // Pass true to skip database initialization
        initializeSampleData();
    }

    private void initializeSampleData() {
        reports = new ArrayList<>();

        // Sample reports
        Report report1 = new Report("P001", "G001", "[Safety Concerns] The property has exposed electrical wiring in the bedroom. This is a serious safety hazard.");
        reports.add(report1);

        Report report2 = new Report("P002", "G002", "[Misleading Information] The property listing shows 3 bedrooms but actually has only 2.");
        reports.add(report2);

        Report report3 = new Report("P001", "G003", "[Property Condition] The property is not as clean as shown in photos. Needs better maintenance.");
        // Mark this one as resolved for demo
        report3.setStatus("RESOLVED");
        reports.add(report3);
    }

    @Override
    public boolean createReport(Report report) {
        try {
            reports.add(report);
            System.out.println("Mock: Report created - " + report.getDescription());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Report> getOpenReports() {
        return reports.stream()
                .filter(r -> "OPEN".equals(r.getStatus()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Report> getPendingReports() {
        return reports.stream()
                .filter(r -> "OPEN".equals(r.getStatus()) || "PENDING".equals(r.getStatus()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean updateReportStatus(String reportID, String status) {
        try {
            for (Report report : reports) {
                if (report.getReportID().equals(reportID)) {
                    report.setStatus(status);
                    System.out.println("Mock: Report " + reportID + " status updated to " + status);
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Helper method to get all reports (for testing)
    public List<Report> getAllReports() {
        return new ArrayList<>(reports);
    }
}
