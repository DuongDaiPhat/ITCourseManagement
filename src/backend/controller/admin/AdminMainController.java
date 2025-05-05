package backend.controller.admin;

<<<<<<< Updated upstream
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

=======
import java.io.File;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import javafx.util.Duration;

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
<<<<<<< Updated upstream
import javafx.geometry.Pos;
=======
>>>>>>> Stashed changes
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
<<<<<<< Updated upstream
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
=======
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Screen;
import javafx.stage.Stage;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;

>>>>>>> Stashed changes
import model.user.Session;
import model.user.Users;
import model.user.UserStatus;

public class AdminMainController implements Initializable {
	@FXML
	private Label usernameLabel;
	@FXML
	private TableView<Users> studentTable;
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
	private TableColumn<Users, String> createdDateColumn;
	@FXML
	private TableColumn<Users, String> statusColumn;
<<<<<<< Updated upstream
=======
	@FXML
	private Label refreshNotificationLabel;

	@FXML
	private Button warnButton;
	@FXML
	private Button banButton;
	@FXML
	private Button unbanButton;
>>>>>>> Stashed changes

	private AdminService adminService = new AdminService();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		try {
			setupStudentTable();
			loadStudents();
<<<<<<< Updated upstream
			loadUserInfo(); // Đảm bảo các thành phần UI đã được khởi tạo trước khi sử dụng
=======
			loadUserInfo();

			// Bắt đầu tự động refresh bảng mỗi 10 giây
			Timeline refreshTimeline = new Timeline(new KeyFrame(Duration.seconds(10), e -> {
				try {
					loadStudents();
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
			showAlert("Error", "Failed to load student data: " + e.getMessage(), Alert.AlertType.ERROR);
		}
	}

<<<<<<< Updated upstream
	private void loadUserInfo() {
		if (usernameLabel != null) { // Kiểm tra null trước khi sử dụng
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

	private void loadUserInfo() {
		if (usernameLabel != null) {
>>>>>>> Stashed changes
			Users currentUser = Session.getCurrentUser();
			if (currentUser != null) {
				usernameLabel.setText(currentUser.getUserFirstName() + " " + currentUser.getUserLastName());
			}
		}
	}

	private void setupStudentTable() {
		idColumn.setCellValueFactory(new PropertyValueFactory<>("userID"));
		firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("userFirstName"));
		lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("userLastName"));
		phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
		emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

<<<<<<< Updated upstream
		// For createdDateColumn - using SimpleStringProperty
=======
>>>>>>> Stashed changes
		createdDateColumn.setCellValueFactory(cellData -> {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String dateString = cellData.getValue().getCreatedAt() != null
					? cellData.getValue().getCreatedAt().format(formatter)
					: "";
			return new SimpleStringProperty(dateString);
		});

<<<<<<< Updated upstream
		// For statusColumn - using SimpleStringProperty
=======
>>>>>>> Stashed changes
		statusColumn
				.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus().toString()));
	}

	private void loadStudents() throws SQLException {
		List<Users> students = adminService.getAllStudents();
		ObservableList<Users> observableList = FXCollections.observableArrayList(students);
		studentTable.setItems(observableList);
	}

	@FXML
<<<<<<< Updated upstream
	private void handleWarn(ActionEvent event) {
		Users selectedStudent = studentTable.getSelectionModel().getSelectedItem();
		if (selectedStudent != null) {
			try {
=======
	public void handleWarn(ActionEvent event) {
		Users selectedStudent = studentTable.getSelectionModel().getSelectedItem();
		if (selectedStudent != null) {
			try {
				System.out.println("Attempting to warn student with ID: " + selectedStudent.getUserID());
>>>>>>> Stashed changes
				boolean success = adminService.updateStudentStatus(selectedStudent.getUserID(), "offline");
				if (success) {
					showAlert("Success", "Student has been warned", Alert.AlertType.INFORMATION);
					loadStudents();
				}
			} catch (SQLException e) {
				showAlert("Error", "Failed to warn student: " + e.getMessage(), Alert.AlertType.ERROR);
			}
		} else {
			showAlert("Warning", "Please select a student first", Alert.AlertType.WARNING);
		}
	}

	@FXML
<<<<<<< Updated upstream
	private void handleBan(ActionEvent event) {
		Users selectedStudent = studentTable.getSelectionModel().getSelectedItem();
		if (selectedStudent != null) {
			try {
=======
	public void handleBan(ActionEvent event) {
		Users selectedStudent = studentTable.getSelectionModel().getSelectedItem();
		if (selectedStudent != null) {
			try {
				System.out.println("Attempting to ban student with ID: " + selectedStudent.getUserID());
>>>>>>> Stashed changes
				boolean success = adminService.updateStudentStatus(selectedStudent.getUserID(), "banned");
				if (success) {
					showAlert("Success", "Student has been banned", Alert.AlertType.INFORMATION);
					loadStudents();
				}
			} catch (SQLException e) {
				showAlert("Error", "Failed to ban student: " + e.getMessage(), Alert.AlertType.ERROR);
			}
		} else {
			showAlert("Warning", "Please select a student first", Alert.AlertType.WARNING);
		}
	}

	@FXML
<<<<<<< Updated upstream
	private void handleUnban(ActionEvent event) {
		Users selectedStudent = studentTable.getSelectionModel().getSelectedItem();
		if (selectedStudent != null) {
			try {
				boolean success = adminService.updateStudentStatus(selectedStudent.getUserID(), "online");
				if (success) {
					showAlert("Success", "Student has been unbanned", Alert.AlertType.INFORMATION);
					loadStudents();
				}
			} catch (SQLException e) {
				showAlert("Error", "Failed to unban student: " + e.getMessage(), Alert.AlertType.ERROR);
=======
	public void handleUnban(ActionEvent event) {
		Users selectedStudent = studentTable.getSelectionModel().getSelectedItem();
		if (selectedStudent != null) {
			System.out.println(
					"Selected student: " + selectedStudent.getUserID() + ", Status: " + selectedStudent.getStatus());
			String currentStatus = selectedStudent.getStatus().toString();
			if (currentStatus.equalsIgnoreCase("banned")) {
				try {
					System.out.println("Attempting to unban student");
					boolean success = adminService.updateStudentStatus(selectedStudent.getUserID(), "online");
					if (success) {
						showAlert("Success", "Student has been unbanned", Alert.AlertType.INFORMATION);
						loadStudents();
					} else {
						System.out.println("Failed to unban student - service returned false");
					}
				} catch (SQLException e) {
					System.out.println("SQLException in unban: " + e.getMessage());
					showAlert("Error", "Failed to unban student: " + e.getMessage(), Alert.AlertType.ERROR);
				} catch (Exception e) {
					System.out.println("General exception in unban: " + e.getMessage());
					showAlert("Error", "Unexpected error: " + e.getMessage(), Alert.AlertType.ERROR);
				}
			} else {
				showAlert("Warning", "This student is not banned (Current status: " + currentStatus + ")",
						Alert.AlertType.WARNING);
>>>>>>> Stashed changes
			}
		} else {
			showAlert("Warning", "Please select a student first", Alert.AlertType.WARNING);
		}
	}

	@FXML
<<<<<<< Updated upstream
=======
	private void handlePrint(ActionEvent event) {
		
	}

	@FXML
>>>>>>> Stashed changes
	private void handleViewPendingCourses(ActionEvent event) throws IOException {
		switchToScene("/frontend/view/admin/PendingCourses.fxml", event);
	}

	@FXML
	private void handleViewAllCourses(ActionEvent event) throws IOException {
		switchToScene("/frontend/view/admin/AllCourses.fxml", event);
	}

	@FXML
	private void handleViewRevenue(ActionEvent event) throws IOException {
		switchToScene("/frontend/view/admin/Revenue.fxml", event);
	}

	@FXML
	private void handleViewInstructors(ActionEvent event) throws IOException {
		switchToScene("/frontend/view/admin/Instructors.fxml", event);
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