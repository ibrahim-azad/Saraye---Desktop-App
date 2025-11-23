package controllers;

import models.Report;
import databases.ReportDAO;
import databases.DAOFactory;
import utils.SessionManager;
import java.util.List;

public class ModerationController {
    private static ModerationController instance;
    private ReportDAO reportDAO;
    private SessionManager sessionManager;

    private ModerationController() {
        this.reportDAO = DAOFactory.getReportDAO();
        this.sessionManager = SessionManager.getInstance();
    }
    
    public static ModerationController getInstance() {
        if (instance == null) {
            instance = new ModerationController();
        }
        return instance;
    }
    
    public List<Report> getPendingReports() {
        try {
            return reportDAO.getPendingReports();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public String resolveReport(String reportId, String action) {
        try {
            boolean success = reportDAO.updateReportStatus(reportId, "RESOLVED");
            return success ? "SUCCESS" : "Failed";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}