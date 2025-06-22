package backend.controller.payment;

import backend.service.payment.PaymentService;
import backend.service.payment.PaymentService.PaymentResult;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.course.Courses;
import model.user.Users;

import java.net.URL;
import java.util.ResourceBundle;

public class PaymentDialogController implements Initializable {
    
    @FXML private Label courseNameLabel;
    @FXML private Label coursePriceLabel;
    @FXML private ComboBox<String> paymentMethodCombo;
    @FXML private VBox cardDetailsContainer;
    @FXML private TextField cardNumberField;
    @FXML private TextField cvvField;
    @FXML private TextField expiryDateField;
    @FXML private TextField cardHolderNameField;
    @FXML private VBox paypalContainer;
    @FXML private TextField paypalEmailField;
    @FXML private Button proceedButton;
    @FXML private Button cancelButton;
    @FXML private ProgressIndicator progressIndicator;
    @FXML private Label statusLabel;
    
    private Courses course;
    private Users user;
    private PaymentResult paymentResult;
    private boolean paymentCompleted = false;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupPaymentMethods();
        setupFormValidation();
        progressIndicator.setVisible(false);
        statusLabel.setText("");
    }
    
    public void setPaymentData(Users user, Courses course) {
        this.user = user;
        this.course = course;
        
        courseNameLabel.setText(course.getCourseName());
        coursePriceLabel.setText(String.format("$%.2f", course.getPrice()));
    }
      private void setupPaymentMethods() {
        paymentMethodCombo.getItems().addAll("Credit Card", "PayPal");
        paymentMethodCombo.setValue("Credit Card");
        
        paymentMethodCombo.setOnAction(e -> {
            String selected = paymentMethodCombo.getValue();
            cardDetailsContainer.setVisible("Credit Card".equals(selected));
            paypalContainer.setVisible("PayPal".equals(selected));
        });
    }
    
    private void setupFormValidation() {
        // Add some basic validation
        cardNumberField.setPromptText("1234 5678 9012 3456");
        cvvField.setPromptText("123");
        expiryDateField.setPromptText("MM/YY");
        cardHolderNameField.setPromptText("John Doe");
        paypalEmailField.setPromptText("user@example.com");
    }
    
    @FXML
    public void handleProceedPayment() {
        if (!validateForm()) {
            showAlert("Validation Error", "Please fill in all required fields correctly.");
            return;
        }
        
        processPayment();
    }
    
    @FXML
    public void handleCancel() {
        closeDialog();
    }
      private boolean validateForm() {
        String paymentMethod = paymentMethodCombo.getValue();
        
        if ("Credit Card".equals(paymentMethod)) {
            return !cardNumberField.getText().trim().isEmpty() &&
                   !cvvField.getText().trim().isEmpty() &&
                   !expiryDateField.getText().trim().isEmpty() &&
                   !cardHolderNameField.getText().trim().isEmpty();
        } else if ("PayPal".equals(paymentMethod)) {
            return !paypalEmailField.getText().trim().isEmpty();
        }
        
        return false; // No valid payment method selected
    }
    
    private void processPayment() {
        proceedButton.setDisable(true);
        progressIndicator.setVisible(true);
        statusLabel.setText("Processing payment...");
        
        Task<PaymentResult> paymentTask = new Task<PaymentResult>() {
            @Override
            protected PaymentResult call() throws Exception {
                String paymentMethod = paymentMethodCombo.getValue();
                String cardNumber = cardNumberField.getText();
                String cvv = cvvField.getText();
                String expiry = expiryDateField.getText();
                
                return PaymentService.getInstance().initiatePayment(
                    user, course, paymentMethod, cardNumber, cvv, expiry
                );
            }
            
            @Override
            protected void succeeded() {
                PaymentResult result = getValue();
                progressIndicator.setVisible(false);
                
                if (result.isSuccess()) {
                    paymentResult = result;
                    paymentCompleted = true;
                    statusLabel.setText("Payment successful! Transaction ID: " + result.getTransactionId());
                    
                    // Show success and close after delay
                    showSuccessDialog(result);
                } else {
                    statusLabel.setText("Payment failed: " + result.getMessage());
                    proceedButton.setDisable(false);
                }
            }
            
            @Override
            protected void failed() {
                progressIndicator.setVisible(false);
                statusLabel.setText("Payment error occurred");
                proceedButton.setDisable(false);
            }
        };
        
        new Thread(paymentTask).start();
    }
    
    private void showSuccessDialog(PaymentResult result) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Payment Successful");
        alert.setHeaderText("Course Purchase Complete!");
        alert.setContentText(
            "Course: " + course.getCourseName() + "\n" +
            "Amount: $" + String.format("%.2f", result.getAmount()) + "\n" +
            "Payment Method: " + result.getPaymentMethod() + "\n" +
            "Transaction ID: " + result.getTransactionId() + "\n\n" +
            "You now have access to this course!"
        );
        
        alert.setOnHidden(e -> closeDialog());
        alert.show();
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void closeDialog() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
    
    public boolean isPaymentCompleted() {
        return paymentCompleted;
    }
    
    public PaymentResult getPaymentResult() {
        return paymentResult;
    }
}
