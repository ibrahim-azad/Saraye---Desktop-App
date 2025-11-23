package ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import models.Report;
import models.User;
import controllers.ModerationController;
import ui.utils.AlertUtil;
import ui.utils.NavigationUtil;

import java.util.List;

public class AdminModerationController {

    @FXML private TableView<Report> reportsTable;
    @FXML private TableColumn<Report, String> actionColumn;
    @FXML private Label pendingCountLabel;
    @FXML private Label resolvedCountLabel;
    @FXML private Label totalCountLabel;

    private User currentUser;
    private ModerationController moderationController;
    private int resolvedTodayCount = 0;

    @FXML
    private void initialize() {
        moderationController = ModerationController.getInstance();

        // Setup action column with buttons
        actionColumn.setCellFactory(param -> new TableCell<Report, String>() {
            private final Button viewDetailsBtn = new Button("View Details");
            private final Button resolveBtn = new Button("Resolve");
            private final HBox hbox = new HBox(5, viewDetailsBtn, resolveBtn);

            {
                viewDetailsBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 11px; -fx-padding: 5 10;");
                resolveBtn.setStyle("-fx-background-color: #4caf50; -fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 11px; -fx-padding: 5 10;");

                viewDetailsBtn.setOnAction(event -> {
                    Report report = getTableView().getItems().get(getIndex());
                    handleViewDetails(report);
                });

                resolveBtn.setOnAction(event -> {
                    Report report = getTableView().getItems().get(getIndex());
                    handleResolveReport(report);
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Report report = getTableView().getItems().get(getIndex());
                    if ("OPEN".equals(report.getStatus()) || "PENDING".equals(report.getStatus())) {
                        setGraphic(hbox);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });
    }

    public void setUser(Object userData) {
        if (userData instanceof User) {
            this.currentUser = (User) userData;
            loadReports();
        }
    }

    private void loadReports() {
        List<Report> reports = moderationController.getPendingReports();

        if (reports != null) {
            reportsTable.getItems().clear();
            reportsTable.getItems().addAll(reports);

            // Update statistics
            pendingCountLabel.setText(String.valueOf(reports.size()));
            totalCountLabel.setText(String.valueOf(reports.size() + resolvedTodayCount));
        } else {
            AlertUtil.showError("Error", "Failed to load reports");
        }
    }

    @FXML
    private void handleRefresh() {
        loadReports();
    }

    @FXML
    private void handleBackToDashboard() {
        // Navigate back based on user role
        if ("ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            NavigationUtil.navigateTo("admin-dashboard.fxml", currentUser);
        } else {
            // Host accessing moderation
            NavigationUtil.navigateTo("host-dashboard.fxml", currentUser);
        }
    }

    private void handleViewDetails(Report report) {
        String details = "Report ID: " + report.getReportID() + "\n\n" +
                        "Property ID: " + report.getPropertyId() + "\n" +
                        "Reporter ID: " + report.getReporterId() + "\n\n" +
                        "Description:\n" + report.getDescription() + "\n\n" +
                        "Status: " + report.getStatus();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Report Details");
        alert.setHeaderText("Full Report Information");
        alert.setContentText(details);
        alert.getDialogPane().setPrefWidth(500);
        alert.showAndWait();
    }

    private void handleResolveReport(Report report) {
        // Confirm resolution
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Resolve Report");
        confirmAlert.setHeaderText("Are you sure you want to resolve this report?");
        confirmAlert.setContentText("Report ID: " + report.getReportID() + "\n" +
                                   "This action will mark the report as resolved.");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String result = moderationController.resolveReport(report.getReportID(), "RESOLVED");

                if ("SUCCESS".equals(result)) {
                    AlertUtil.showSuccess("Report Resolved",
                        "The report has been marked as resolved successfully.");

                    // Update statistics
                    resolvedTodayCount++;
                    resolvedCountLabel.setText(String.valueOf(resolvedTodayCount));

                    // Refresh table
                    loadReports();
                } else {
                    AlertUtil.showError("Error", "Failed to resolve report: " + result);
                }
            }
        });
    }
}
