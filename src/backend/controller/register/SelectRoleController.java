package backend.controller.register;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.user.UserStatus;
import model.user.Users;
import backend.controller.scene.SceneManager;
import backend.repository.user.UsersRepository;
import backend.service.user.RegisterService;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class SelectRoleController implements Initializable{
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
		// Thiết lập nút NEXT ở trạng thái vô hiệu hóa ban đầu
		nextButton.setDisable(true);

		// Với CheckBox
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

	public void SelectRoleForUser(Users user) {
		this.user = user;
		userFullName.setText(this.user.getUserFirstName() + " " + this.user.getUserLastName());
	}

	public void Next(ActionEvent e) throws SQLException {
		if (instructorCheckbox.isSelected()) {
			this.user.setRoleID(1);
		} else if (studentCheckbox.isSelected()) {
			this.user.setRoleID(2);
		}
		 if (RegisterService.getInstance().registerUser(this.user)) {
	            Platform.runLater(() -> {
	                showSuccessAlert();
	                // SceneManager.switchScene đã xử lý IOException nội bộ
	                SceneManager.switchSceneReloadWithData("Login", "/frontend/view/login/Login.fxml", null, null);
	            });
	        } else {
	            Platform.runLater(() -> {
	                showErrorAlert("Registration failed. Please try again.");
	            });
	        }

		Platform.runLater(() -> {
			showSuccessAlert();
			try {
				SceneManager.goBack();
				SceneManager.goBack();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});
	}

	private void showSuccessAlert() {
		try {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Success message");
			alert.setHeaderText(null);

			Label messageLabel = new Label("Account created successfully!\nPlease sign in to continue");
			messageLabel.setStyle("-fx-text-fill: #004AAD; -fx-font-size: 16px; -fx-font-weight: bold;");

			VBox contentBox = new VBox(15, messageLabel);
			contentBox.setAlignment(Pos.CENTER);

			DialogPane dialogPane = alert.getDialogPane();
			dialogPane.getStylesheets()
					.add(getClass().getResource("/frontend/view/register/selectRole.css").toExternalForm());
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