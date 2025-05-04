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

	private AdminService adminService = new AdminService();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		try {
			setupInstructorTable();
			loadInstructors();
			loadUserInfo();
		} catch (SQLException e) {
			showAlert("Error", "Failed to load instructor data: " + e.getMessage(), Alert.AlertType.ERROR);
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
	    
	    if (selectedInstructor == null) {
	        showAlert("Warning", "Please select an instructor first", Alert.AlertType.WARNING);
	        return;
	    }

	    String currentStatus = selectedInstructor.getStatus().toString();
	    if (!currentStatus.equalsIgnoreCase("banned")) {
	        showAlert("Warning", 
	                 "This instructor is not banned (Current status: " + currentStatus + ")",
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
	        showAlert("Error", "Database error while unbanning instructor: " + e.getMessage(), 
	                 Alert.AlertType.ERROR);
	    } catch (Exception e) {
	        System.err.println("General exception in unban: " + e.getMessage());
	        showAlert("Error", "An unexpected error occurred: " + e.getMessage(), 
	                 Alert.AlertType.ERROR);
	    }
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