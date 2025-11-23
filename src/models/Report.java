package models;

import java.util.UUID;

public class Report {
    private String reportID;
    private String propertyId;
    private String reporterId;
    private String description;
    private String status; // OPEN, RESOLVED

    // Constructor for creating new report
    public Report(String propertyId, String reporterId, String description) {
        this.reportID = UUID.randomUUID().toString();
        this.propertyId = propertyId;
        this.reporterId = reporterId;
        this.description = description;
        this.status = "OPEN";
    }

    // Constructor for loading from database
    public Report(String reportID, String propertyId, String reporterId, String description, String status) {
        this.reportID = reportID;
        this.propertyId = propertyId;
        this.reporterId = reporterId;
        this.description = description;
        this.status = status;
    }

    // Getters
    public String getReportID() {
        return reportID;
    }

    public String getPropertyId() {
        return propertyId;
    }

    public String getReporterId() {
        return reporterId;
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