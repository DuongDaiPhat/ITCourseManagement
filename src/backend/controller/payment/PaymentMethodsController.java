package backend.controller.payment;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import model.payment.Payment;
import model.payment.UserPayment;
import backend.service.user.PaymentService;
import model.user.Session;

import java.net.URL;
import java.text.NumberFormat;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class PaymentMethodsController implements Initializable {

    @FXML
    private ComboBox<Payment> paymentMethodComboBox;
    
    @FXML
    private Button addPaymentMethodButton;
    
    @FXML
    private ListView<UserPayment> userPaymentsListView;
    
    @FXML
    private Label balanceLabel;
    
    @FXML
    private TextField amountTextField;
    
    @FXML
    private Button depositButton;
    
    @FXML
    private Button withdrawButton;
    
    private PaymentService paymentService;
    private int currentUserId;
    private ObservableList<UserPayment> userPayments;
    private NumberFormat currencyFormat;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        paymentService = new PaymentService();
        currentUserId = Session.getCurrentUser().getUserID();
        userPayments = FXCollections.observableArrayList();
        currencyFormat = NumberFormat.getCurrencyInstance();
        
        // Configure ComboBox
        configurePaymentMethodComboBox();
        
        // Configure ListView
        configureUserPaymentsListView();
        
        // Set up amount text field for numeric input only
        amountTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                amountTextField.setText(oldValue);
            }
        });
        
        // Load data
        loadPaymentMethods();
        loadUserPaymentMethods();
        
        // Setup button actions
        setupButtonActions();
    }
    
    private void configurePaymentMethodComboBox() {
        paymentMethodComboBox.setConverter(new StringConverter<Payment>() {
            @Override
            public String toString(Payment payment) {
                return payment != null ? payment.getPaymentName() : "";
            }
            
            @Override
            public Payment fromString(String string) {
                return null; // Not needed for ComboBox
            }
        });
    }
    
    private void configureUserPaymentsListView() {
        userPaymentsListView.setItems(userPayments);
        userPaymentsListView.setCellFactory(param -> new ListCell<UserPayment>() {
            @Override
            protected void updateItem(UserPayment item, boolean empty) {
                super.updateItem(item, empty);
                
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    VBox content = new VBox(5);
                    Label nameLabel = new Label(item.getPaymentName());
                    nameLabel.setStyle("-fx-font-weight: bold;");
                    
                    Label balanceLabel = new Label("Balance: " + currencyFormat.format(item.getBalance()));
                    
                    Button deleteButton = new Button("Remove");
                    deleteButton.setOnAction(event -> removePaymentMethod(item));
                    
                    content.getChildren().addAll(nameLabel, balanceLabel, deleteButton);
                    setGraphic(content);
                }
            }
        });
        
        userPaymentsListView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        updateBalanceLabel(newValue);
                    }
                });
    }
    
    private void loadPaymentMethods() {
        List<Payment> availablePayments = paymentService.getAllPaymentMethods();
        paymentMethodComboBox.setItems(FXCollections.observableArrayList(availablePayments));
        
        if (!availablePayments.isEmpty()) {
            paymentMethodComboBox.getSelectionModel().selectFirst();
        }
    }
    
    private void loadUserPaymentMethods() {
        userPayments.clear();
        userPayments.addAll(paymentService.getUserPaymentMethods(currentUserId));
        
        if (!userPayments.isEmpty()) {
            userPaymentsListView.getSelectionModel().selectFirst();
        }
    }
    
    private void setupButtonActions() {
        addPaymentMethodButton.setOnAction(event -> addPaymentMethod());
        depositButton.setOnAction(event -> deposit());
        withdrawButton.setOnAction(event -> withdraw());
    }
    
    @FXML
    private void addPaymentMethod() {
        Payment selectedPayment = paymentMethodComboBox.getSelectionModel().getSelectedItem();
        
        if (selectedPayment == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a payment method.");
            return;
        }
        
        boolean success = paymentService.addPaymentMethod(currentUserId, selectedPayment.getPaymentID());
        
        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Payment method added successfully.");
            loadUserPaymentMethods();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add payment method. It may already exist.");
        }
    }
    
    private void removePaymentMethod(UserPayment userPayment) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Removal");
        confirmAlert.setHeaderText("Remove Payment Method");
        confirmAlert.setContentText("Are you sure you want to remove " + userPayment.getPaymentName() + "?");
        
        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = paymentService.removePaymentMethod(currentUserId, userPayment.getPaymentID());
            
            if (success) {
                userPayments.remove(userPayment);
                balanceLabel.setText("Balance: " + currencyFormat.format(0));
                
                if (!userPayments.isEmpty()) {
                    userPaymentsListView.getSelectionModel().selectFirst();
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to remove payment method.");
            }
        }
    }
    
    @FXML
    private void deposit() {
        UserPayment selectedPayment = userPaymentsListView.getSelectionModel().getSelectedItem();
        
        if (selectedPayment == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a payment method.");
            return;
        }
        
        try {
            float amount = Float.parseFloat(amountTextField.getText());
            
            if (amount <= 0) {
                showAlert(Alert.AlertType.WARNING, "Warning", "Please enter a valid amount greater than 0.");
                return;
            }
            
            boolean success = paymentService.deposit(currentUserId, selectedPayment.getPaymentID(), amount);
            
            if (success) {
                selectedPayment.setBalance(selectedPayment.getBalance() + amount);
                updateBalanceLabel(selectedPayment);
                userPaymentsListView.refresh();
                amountTextField.clear();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Deposit successful.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to deposit money.");
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please enter a valid amount.");
        }
    }
    
    @FXML
    private void withdraw() {
        UserPayment selectedPayment = userPaymentsListView.getSelectionModel().getSelectedItem();
        
        if (selectedPayment == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a payment method.");
            return;
        }
        
        try {
            float amount = Float.parseFloat(amountTextField.getText());
            
            if (amount <= 0) {
                showAlert(Alert.AlertType.WARNING, "Warning", "Please enter a valid amount greater than 0.");
                return;
            }
            
            if (amount > selectedPayment.getBalance()) {
                showAlert(Alert.AlertType.WARNING, "Warning", "Insufficient balance.");
                return;
            }
            
            boolean success = paymentService.withdraw(currentUserId, selectedPayment.getPaymentID(), amount);
            
            if (success) {
                selectedPayment.setBalance(selectedPayment.getBalance() - amount);
                updateBalanceLabel(selectedPayment);
                userPaymentsListView.refresh();
                amountTextField.clear();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Withdrawal successful.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to withdraw money.");
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please enter a valid amount.");
        }
    }
    
    private void updateBalanceLabel(UserPayment userPayment) {
        balanceLabel.setText("Balance: " + currencyFormat.format(userPayment.getBalance()));
    }
    
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}