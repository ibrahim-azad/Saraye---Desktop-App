package models;

import java.util.UUID;

public class Report {
    private String reportID;
    private String description;
    private String status; // OPEN, RESOLVED

    public Report(String description) {
        this.reportID = UUID.randomUUID().toString();
        this.description = description;
        this.status = "OPEN";
    }

    // Constructor for loading from database
    public Report(String reportID, String description, String status) {
        this.reportID = reportID;
        this.description = description;
        this.status = status;
    }

    // Getters
    public String getReportID() {
        return reportID;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}