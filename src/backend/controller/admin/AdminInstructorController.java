package backend.controller.admin;

import java.io.IOException;
<<<<<<< Updated upstream
import java.net.URL;
import java.sql.SQLException;
=======

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
>>>>>>> Stashed changes
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

<<<<<<< Updated upstream
=======
import java.io.File;
import java.io.FileOutputStream;
import javafx.stage.FileChooser;

>>>>>>> Stashed changes
import backend.service.user.AdminService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Screen;
import javafx.stage.Stage;
import model.user.Session;
import model.user.Users;
import model.user.UserStatus;
<<<<<<< Updated upstream
=======
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
>>>>>>> Stashed changes

public class AdminInstructorController implements Initializable {
	@FXML
	private Label usernameLabel;
	@FXML
	private TableView<Users> instructorTable;
	@FXML
	private TableColumn<Users, Integer> idColumn;
	@FXML
	private TableColumn<Users, String> firstNameColumn;
	@FXML
	private TableColumn<Users, String> lastNameColumn;
	@FXML
	private TableColumn<Users, String> phoneColumn;
	@FXML
	private TableColumn<Users, String> emailColumn;
	@FXML
	private TableColumn<Users, Integer> courseCountColumn;
	@FXML
	private TableColumn<Users, String> createdDateColumn;
	@FXML
	private TableColumn<Users, String> statusColumn;
<<<<<<< Updated upstream
=======
	@FXML
	private Label refreshNotificationLabel;
>>>>>>> Stashed changes

	private AdminService adminService = new AdminService();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		try {
			setupInstructorTable();
			loadInstructors();
			loadUserInfo();
<<<<<<< Updated upstream
=======

			// Bắt đầu tự động refresh bảng mỗi 10 giây
			Timeline refreshTimeline = new Timeline(new KeyFrame(Duration.seconds(10), e -> {
				try {
					loadInstructors();
					// Hiển thị thông báo refresh
					showRefreshNotification();
				} catch (SQLException ex) {
					System.out.println("Auto-refresh failed: " + ex.getMessage());
				}
			}));
			refreshTimeline.setCycleCount(Timeline.INDEFINITE);
			refreshTimeline.play();

>>>>>>> Stashed changes
		} catch (SQLException e) {
			showAlert("Error", "Failed to load instructor data: " + e.getMessage(), Alert.AlertType.ERROR);
		}
	}

<<<<<<< Updated upstream
=======
	private void showRefreshNotification() {
		String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
		refreshNotificationLabel.setText("Last refreshed at: " + time);

		// Tạo hiệu ứng fade out sau 3 giây
		Timeline fadeOut = new Timeline(
				new KeyFrame(Duration.seconds(0), e -> refreshNotificationLabel.setOpacity(1.0)),
				new KeyFrame(Duration.seconds(3), e -> refreshNotificationLabel.setOpacity(0.0)));
		fadeOut.play();
	}

>>>>>>> Stashed changes
	private void loadUserInfo() {
		if (usernameLabel != null) {
			Users currentUser = Session.getCurrentUser();
			if (currentUser != null) {
				usernameLabel.setText(currentUser.getUserFirstName() + " " + currentUser.getUserLastName());
			}
		}
	}

	private void setupInstructorTable() {
		idColumn.setCellValueFactory(new PropertyValueFactory<>("userID"));
		firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("userFirstName"));
		lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("userLastName"));
		phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
		emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
		courseCountColumn.setCellValueFactory(new PropertyValueFactory<>("courseCount"));

		createdDateColumn.setCellValueFactory(cellData -> {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String dateString = cellData.getValue().getCreatedAt() != null
					? cellData.getValue().getCreatedAt().format(formatter)
					: "";
			return new SimpleStringProperty(dateString);
		});

		statusColumn
				.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus().toString()));
	}

	private void loadInstructors() throws SQLException {
		List<Users> instructors = adminService.getAllInstructors();
		ObservableList<Users> observableList = FXCollections.observableArrayList(instructors);
		instructorTable.setItems(observableList);
	}

	@FXML
	private void handleWarn(ActionEvent event) {
		Users selectedInstructor = instructorTable.getSelectionModel().getSelectedItem();
		if (selectedInstructor != null) {
			try {
				boolean success = adminService.updateStudentStatus(selectedInstructor.getUserID(), "offline");
				if (success) {
					showAlert("Success", "Instructor has been warned", Alert.AlertType.INFORMATION);
					loadInstructors();
				}
			} catch (SQLException e) {
				showAlert("Error", "Failed to warn instructor: " + e.getMessage(), Alert.AlertType.ERROR);
			}
		} else {
			showAlert("Warning", "Please select an instructor first", Alert.AlertType.WARNING);
		}
	}

	@FXML
	private void handleBan(ActionEvent event) {
		Users selectedInstructor = instructorTable.getSelectionModel().getSelectedItem();
		if (selectedInstructor != null) {
			try {
				boolean success = adminService.updateStudentStatus(selectedInstructor.getUserID(), "banned");
				if (success) {
					showAlert("Success", "Instructor has been banned", Alert.AlertType.INFORMATION);
					loadInstructors();
				}
			} catch (SQLException e) {
				showAlert("Error", "Failed to ban instructor: " + e.getMessage(), Alert.AlertType.ERROR);
			}
		} else {
			showAlert("Warning", "Please select an instructor first", Alert.AlertType.WARNING);
		}
	}

	@FXML
	private void handleUnban(ActionEvent event) {
		Users selectedInstructor = instructorTable.getSelectionModel().getSelectedItem();
<<<<<<< Updated upstream
		if (selectedInstructor != null) {
			try {
				boolean success = adminService.updateStudentStatus(selectedInstructor.getUserID(), "online");
				if (success) {
					showAlert("Success", "Instructor has been unbanned", Alert.AlertType.INFORMATION);
					loadInstructors();
				}
			} catch (SQLException e) {
				showAlert("Error", "Failed to unban instructor: " + e.getMessage(), Alert.AlertType.ERROR);
			}
		} else {
			showAlert("Warning", "Please select an instructor first", Alert.AlertType.WARNING);
		}
=======

		if (selectedInstructor == null) {
			showAlert("Warning", "Please select an instructor first", Alert.AlertType.WARNING);
			return;
		}

		String currentStatus = selectedInstructor.getStatus().toString();
		if (!currentStatus.equalsIgnoreCase("banned")) {
			showAlert("Warning", "This instructor is not banned (Current status: " + currentStatus + ")",
					Alert.AlertType.WARNING);
			return;
		}

		try {
			boolean success = adminService.updateInstructorStatus(selectedInstructor.getUserID(), "online");
			if (success) {
				showAlert("Success", "Instructor has been unbanned", Alert.AlertType.INFORMATION);
				loadInstructors();
			} else {
				showAlert("Error", "Failed to unban instructor", Alert.AlertType.ERROR);
			}
		} catch (SQLException e) {
			System.err.println("SQLException in unban: " + e.getMessage());
			showAlert("Error", "Database error while unbanning instructor: " + e.getMessage(), Alert.AlertType.ERROR);
		} catch (Exception e) {
			System.err.println("General exception in unban: " + e.getMessage());
			showAlert("Error", "An unexpected error occurred: " + e.getMessage(), Alert.AlertType.ERROR);
		}
	}

	@FXML
	private void handlePrint(ActionEvent event) {
>>>>>>> Stashed changes
	}

	@FXML
	private void handleBackToMain(ActionEvent event) throws IOException {
		switchToScene("/frontend/view/admin/AdminMainPage.fxml", event);
	}

	@FXML
	public void handleViewPendingCourses(ActionEvent event) throws IOException {
		switchToScene("/frontend/view/admin/PendingCourses.fxml", event);
	}

	@FXML
	private void handleViewAllCourses(ActionEvent event) throws IOException {
		switchToScene("/frontend/view/admin/AllCourses.fxml", event);
	}

	private void switchToScene(String fxmlPath, ActionEvent event) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
		Rectangle2D rec = Screen.getPrimary().getVisualBounds();
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.setX((rec.getWidth() - stage.getWidth()) / 2);
		stage.setY((rec.getHeight() - stage.getHeight()) / 2);
		stage.show();
	}

	private void showAlert(String title, String message, Alert.AlertType type) {
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
}