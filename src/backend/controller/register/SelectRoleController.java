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
import backend.repository.user.UsersRepository;
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

public class SelectRoleController implements Initializable {
	private Users user;
	@FXML
	private Label userFullName;
	@FXML
	private CheckBox instructorCheckbox;
	@FXML
	private CheckBox studentCheckbox;
	@FXML
	private Button nextButton;

	// Nếu bạn sử dụng ToggleButton thay thế
	// @FXML
	// private ToggleGroup roleToggleGroup;
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
			this.user.setRoleID(3);
		} else if (studentCheckbox.isSelected()) {
			this.user.setRoleID(2);
		}
		this.user.setCreatedAt(LocalDate.now());
		this.user.setStatus(UserStatus.online);
		this.user.setDescription("No bio yet");
		UsersRepository.getInstance().Insert(user);

		Platform.runLater(() -> {
			showSuccessAlert();
			try {
				BackToLogin();
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

	public void BackToLogin() throws Exception {
	    FXMLLoader loader = new FXMLLoader(getClass().getResource("/frontend/view/login/Login.fxml"));
	    Parent root = loader.load();
	    Stage stage = (Stage) nextButton.getScene().getWindow();
	    stage.setScene(new Scene(root));
	    
	    Rectangle2D rec = Screen.getPrimary().getVisualBounds();
	    stage.setX((rec.getWidth() - stage.getWidth())/2);
	    stage.setY((rec.getHeight() - stage.getHeight())/2);
	    
	    stage.show();
	}
}