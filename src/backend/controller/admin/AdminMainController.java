package backend.controller.admin;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

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

	// Add explicit button references
	@FXML
	private Button warnButton;
	@FXML
	private Button banButton;
	@FXML
	private Button unbanButton;

	private AdminService adminService = new AdminService();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		try {
			setupStudentTable();
			loadStudents();
			loadUserInfo();

		} catch (SQLException e) {
			showAlert("Error", "Failed to load student data: " + e.getMessage(), Alert.AlertType.ERROR);
		}
	}

	private void loadUserInfo() {
		if (usernameLabel != null) {
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

	private void loadStudents() throws SQLException {
		List<Users> students = adminService.getAllStudents();
		ObservableList<Users> observableList = FXCollections.observableArrayList(students);
		studentTable.setItems(observableList);
	}

	@FXML
	public void handleWarn(ActionEvent event) {
		Users selectedStudent = studentTable.getSelectionModel().getSelectedItem();
		if (selectedStudent != null) {
			try {
				System.out.println("Attempting to warn student with ID: " + selectedStudent.getUserID());
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
	public void handleBan(ActionEvent event) {
		Users selectedStudent = studentTable.getSelectionModel().getSelectedItem();
		if (selectedStudent != null) {
			try {
				System.out.println("Attempting to ban student with ID: " + selectedStudent.getUserID());
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
	public void handleUnban(ActionEvent event) {
		Users selectedStudent = studentTable.getSelectionModel().getSelectedItem();
		if (selectedStudent != null) {
			System.out.println(
					"Selected student: " + selectedStudent.getUserID() + ", Status: " + selectedStudent.getStatus());
			// Use string comparison for status checking
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
			}
		} else {
			showAlert("Warning", "Please select a student first", Alert.AlertType.WARNING);
		}
	}

	@FXML
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