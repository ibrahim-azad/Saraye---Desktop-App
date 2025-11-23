package controllers;

import models.Report;
import databases.ReportDAO;
import utils.SessionManager;

public class ReportController {
    private static ReportController instance;
    private ReportDAO reportDAO;
    private SessionManager sessionManager;
    
    private ReportController() {
        this.reportDAO = new ReportDAO();
        this.sessionManager = SessionManager.getInstance();
    }
    
    public static ReportController getInstance() {
        if (instance == null) {
            instance = new ReportController();
        }
        return instance;
    }
    
    public String createReport(String propertyId, String description) {
        try {
            if (!sessionManager.isLoggedIn()) {
                return "You must be logged in";
            }
            String reporterId = sessionManager.getCurrentUserId();
            Report report = new Report(propertyId, reporterId, description);
            boolean success = reportDAO.createReport(report);
            return success ? "SUCCESS" : "Failed";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}