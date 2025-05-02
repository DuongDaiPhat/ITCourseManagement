package backend.controller.register;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.DialogPane;
import javafx.geometry.Pos;
import javafx.application.Platform;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import backend.service.user.RegisterService;
import backend.controller.scene.SceneManager;
import model.user.Users;

public class SelectRoleController implements Initializable, ISelectRoleController {
    private Users user;
    @FXML
    private Label userFullName;
    @FXML
    private CheckBox instructorCheckbox;
    @FXML
    private CheckBox studentCheckbox;
    @FXML
    private Button nextButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        nextButton.setDisable(true);

        instructorCheckbox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                studentCheckbox.setSelected(false);
                nextButton.setDisable(false);
            } else if (!studentCheckbox.isSelected()) {
                nextButton.setDisable(true);
            }
        });

        studentCheckbox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                instructorCheckbox.setSelected(false);
                nextButton.setDisable(false);
            } else if (!instructorCheckbox.isSelected()) {
                nextButton.setDisable(true);
            }
        });
    }

    @Override
    public void SelectRoleForUser(Users user) {
        this.user = user;
        userFullName.setText(this.user.getUserFirstName() + " " + this.user.getUserLastName());
    }

    @Override
    public void Next(ActionEvent e) throws SQLException {
        if (instructorCheckbox.isSelected()) {
            this.user.setRoleID(1);
        } else if (studentCheckbox.isSelected()) {
            this.user.setRoleID(2);
        }

        // Gọi RegisterService để mã hóa và lưu
        if (RegisterService.getInstance().registerUser(this.user)) {
            Platform.runLater(() -> {
                showSuccessAlert("Account created successfully! Please log in.");
                // SceneManager.switchScene đã xử lý IOException nội bộ
                SceneManager.switchScene("Login", "/frontend/view/login/Login.fxml");
            });
        } else {
            Platform.runLater(() -> {
                showErrorAlert("Registration failed. Please try again.");
            });
        }
    }

    private void showSuccessAlert(String message) {
        try {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Success message");
            alert.setHeaderText(null);

            Label messageLabel = new Label(message);
            messageLabel.setStyle("-fx-text-fill: #004AAD; -fx-font-size: 16px; -fx-font-weight: bold;");

            VBox contentBox = new VBox(15, messageLabel);
            contentBox.setAlignment(Pos.CENTER);

            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStylesheets().add(getClass().getResource("/frontend/view/register/selectRole.css").toExternalForm());
            dialogPane.getStyleClass().add("success-alert");
            dialogPane.setContent(contentBox);
            dialogPane.setPrefWidth(400);

            alert.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            Alert simpleAlert = new Alert(AlertType.INFORMATION, "Account created successfully!");
            simpleAlert.showAndWait();
        }
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}