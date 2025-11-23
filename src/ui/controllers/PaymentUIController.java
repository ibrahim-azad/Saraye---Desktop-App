package ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import controllers.PaymentController;
import controllers.BookingController;
import models.Booking;
import models.Property;
import utils.SessionManager;
import ui.utils.NavigationUtil;

import java.time.format.DateTimeFormatter;

/**
 * PaymentUIController - UI Layer for Payment Screen
 * Handles payment form and processing
 * UC5: Make Payment
 */
public class PaymentUIController {

    @FXML
    private Label bookingIdLabel;
    @FXML
    private Label propertyNameLabel;
    @FXML
    private Label checkInLabel;
    @FXML
    private Label checkOutLabel;
    @FXML
    private Label totalAmountLabel;

    @FXML
    private RadioButton creditCardRadio;
    @FXML
    private RadioButton jazzCashRadio;
    @FXML
    private RadioButton easypaisaRadio;

    @FXML
    private VBox creditCardPanel;
    @FXML
    private VBox mobileWalletPanel;

    @FXML
    private TextField cardNumberField;
    @FXML
    private TextField expiryField;
    @FXML
    private TextField cvvField;
    @FXML
    private TextField cardHolderField;

    @FXML
    private TextField mobileNumberField;
    @FXML
    private TextField accountNameField;

    private PaymentController paymentController;
    private BookingController bookingController;
    private SessionManager sessionManager;
    private ToggleGroup paymentMethodGroup;

    private Booking currentBooking;
    private Property currentProperty;

    @FXML
    public void initialize() {
        paymentController = PaymentController.getInstance();
        bookingController = BookingController.getInstance();
        sessionManager = SessionManager.getInstance();

        // Setup payment method toggle group
        paymentMethodGroup = new ToggleGroup();
        creditCardRadio.setToggleGroup(paymentMethodGroup);
        jazzCashRadio.setToggleGroup(paymentMethodGroup);
        easypaisaRadio.setToggleGroup(paymentMethodGroup);

        // Add listeners to show/hide payment panels
        paymentMethodGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == creditCardRadio) {
                creditCardPanel.setVisible(true);
                creditCardPanel.setManaged(true);
                mobileWalletPanel.setVisible(false);
                mobileWalletPanel.setManaged(false);
            } else {
                creditCardPanel.setVisible(false);
                creditCardPanel.setManaged(false);
                mobileWalletPanel.setVisible(true);
                mobileWalletPanel.setManaged(true);
            }
        });

        // Add input validation listeners
        setupInputValidation();
    }

    /**
     * Set the booking data for payment
     * Called from navigation with booking and property objects
     */
    public void setData(Object data) {
        if (data instanceof Object[]) {
            Object[] dataArray = (Object[]) data;
            if (dataArray.length >= 2) {
                this.currentBooking = (Booking) dataArray[0];
                this.currentProperty = (Property) dataArray[1];
                loadBookingDetails();
            }
        }
    }

    private void loadBookingDetails() {
        if (currentBooking != null && currentProperty != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");

            bookingIdLabel.setText(currentBooking.getBookingID());
            propertyNameLabel.setText(currentProperty.getTitle());
            checkInLabel.setText(currentBooking.getCheckInDate().format(formatter));
            checkOutLabel.setText(currentBooking.getCheckOutDate().format(formatter));
            totalAmountLabel.setText("PKR " + String.format("%.2f", currentBooking.getTotalPrice()));
        }
    }

    private void setupInputValidation() {
        // Card number validation (16 digits)
        cardNumberField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                cardNumberField.setText(oldVal);
            } else if (newVal.length() > 16) {
                cardNumberField.setText(oldVal);
            }
        });

        // CVV validation (3-4 digits)
        cvvField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                cvvField.setText(oldVal);
            } else if (newVal.length() > 4) {
                cvvField.setText(oldVal);
            }
        });

        // Expiry date validation (MM/YY format)
        expiryField.textProperty().addListener((obs, oldVal, newVal) -> {
            String cleaned = newVal.replaceAll("[^0-9]", "");
            if (cleaned.length() > 4) {
                cleaned = cleaned.substring(0, 4);
            }

            StringBuilder formatted = new StringBuilder();
            for (int i = 0; i < cleaned.length(); i++) {
                if (i == 2) {
                    formatted.append("/");
                }
                formatted.append(cleaned.charAt(i));
            }

            if (!formatted.toString().equals(newVal)) {
                expiryField.setText(formatted.toString());
            }
        });

        // Mobile number validation (Pakistani format)
        mobileNumberField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("[0-9-]*")) {
                mobileNumberField.setText(oldVal);
            } else if (newVal.replaceAll("-", "").length() > 11) {
                mobileNumberField.setText(oldVal);
            }
        });
    }

    @FXML
    private void handlePayment() {
        // Validate all required fields
        String validationError = validatePaymentForm();
        if (validationError != null) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", validationError);
            return;
        }

        // Determine payment method
        String paymentMethod = getSelectedPaymentMethod();

        // Show confirmation dialog
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Payment");
        confirmAlert.setHeaderText("Confirm Payment of PKR " + String.format("%.2f", currentBooking.getTotalPrice()));
        confirmAlert.setContentText("Are you sure you want to proceed with the payment?");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                processPayment(paymentMethod);
            }
        });
    }

    private void processPayment(String paymentMethod) {
        String result = paymentController.processPayment(
                currentBooking.getBookingID(),
                currentBooking.getTotalPrice(),
                paymentMethod);

        if ("SUCCESS".equals(result)) {
            // Update booking status to CONFIRMED after successful payment
            boolean updated = bookingController.updateBookingStatus(
                    currentBooking.getBookingID(),
                    "CONFIRMED");

            if (updated) {
                showAlert(Alert.AlertType.INFORMATION,
                        "Payment Successful",
                        "Your payment has been processed successfully!\nBooking ID: " + currentBooking.getBookingID());

                // Navigate back to guest bookings
                NavigationUtil.navigateTo("guest-bookings.fxml", sessionManager.getCurrentUser());
            } else {
                showAlert(Alert.AlertType.WARNING,
                        "Payment Processed",
                        "Payment was successful but booking status update failed. Please contact support.");
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Payment Failed", result);
        }
    }

    private String validatePaymentForm() {
        RadioButton selected = (RadioButton) paymentMethodGroup.getSelectedToggle();

        if (selected == creditCardRadio) {
            // Validate credit card fields
            if (cardNumberField.getText().trim().isEmpty()) {
                return "Card number is required";
            }
            if (cardNumberField.getText().trim().length() != 16) {
                return "Card number must be 16 digits";
            }
            if (expiryField.getText().trim().isEmpty()) {
                return "Expiry date is required";
            }
            if (!expiryField.getText().matches("\\d{2}/\\d{2}")) {
                return "Expiry date must be in MM/YY format";
            }
            if (cvvField.getText().trim().isEmpty()) {
                return "CVV is required";
            }
            if (cvvField.getText().trim().length() < 3) {
                return "CVV must be 3-4 digits";
            }
            if (cardHolderField.getText().trim().isEmpty()) {
                return "Cardholder name is required";
            }
        } else {
            // Validate mobile wallet fields
            if (mobileNumberField.getText().trim().isEmpty()) {
                return "Mobile number is required";
            }
            String cleaned = mobileNumberField.getText().replaceAll("-", "");
            if (cleaned.length() != 11) {
                return "Mobile number must be 11 digits";
            }
            if (!cleaned.startsWith("03")) {
                return "Mobile number must start with 03";
            }
            if (accountNameField.getText().trim().isEmpty()) {
                return "Account name is required";
            }
        }

        return null; // No errors
    }

    private String getSelectedPaymentMethod() {
        RadioButton selected = (RadioButton) paymentMethodGroup.getSelectedToggle();

        if (selected == creditCardRadio) {
            return "Credit Card";
        } else if (selected == jazzCashRadio) {
            return "JazzCash";
        } else if (selected == easypaisaRadio) {
            return "Easypaisa";
        }

        return "Credit Card"; // Default
    }

    @FXML
    private void handleBack() {
        NavigationUtil.navigateTo("guest-bookings.fxml", sessionManager.getCurrentUser());
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
