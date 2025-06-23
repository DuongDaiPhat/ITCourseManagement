package backend.controller.admin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.ResourceBundle;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import backend.service.user.AdminService;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
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
	@FXML
	private Label refreshNotificationLabel;
	@FXML
	private TextField searchField;

	private Timeline searchDebounceTimeline;
	private Timeline refreshTimeline;
	private boolean isSearching = false;
	private AdminService adminService = new AdminService();
	private Users selectedStudentForAction;
	private String pendingActionType;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		try {
			setupStudentTable();
			loadStudents();
			loadUserInfo();
			setupSearchFunctionality();
			setupAutoRefresh();
		} catch (SQLException e) {
			showAlert("Error", "Failed to load student data: " + e.getMessage(), Alert.AlertType.ERROR);
		}
	}

	private void setupAutoRefresh() {
		refreshTimeline = new Timeline(new KeyFrame(Duration.seconds(10), e -> {
			if (!isSearching) {
				try {
					loadStudents();
					showRefreshNotification();
				} catch (SQLException ex) {
					System.out.println("Auto-refresh failed: " + ex.getMessage());
				}
			}
		}));
		refreshTimeline.setCycleCount(Timeline.INDEFINITE);
		refreshTimeline.play();
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
		statusColumn.setCellValueFactory(cellData -> {
			Users user = cellData.getValue();
			if (user.getStatus() == UserStatus.banned) {
				return new SimpleStringProperty("banned");
			} else {
				return new SimpleStringProperty(user.getWarningCount() + "/5 warnings");
			}
		});
	}

	private void loadStudents() throws SQLException {
		List<Users> students = adminService.getAllStudents();
		ObservableList<Users> observableList = FXCollections.observableArrayList(students);
		studentTable.setItems(observableList);
	}

	private void loadUserInfo() {
		if (usernameLabel != null) {
			Users currentUser = Session.getCurrentUser();
			if (currentUser != null) {
				usernameLabel.setText(currentUser.getUserFirstName() + " " + currentUser.getUserLastName());
			}
		}
	}

	private void showRefreshNotification() {
		String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
		refreshNotificationLabel.setText("Last refreshed at: " + time);
		Timeline fadeOut = new Timeline(
				new KeyFrame(Duration.seconds(0), e -> refreshNotificationLabel.setOpacity(1.0)),
				new KeyFrame(Duration.seconds(3), e -> refreshNotificationLabel.setOpacity(0.0)));
		fadeOut.play();
	}

	private void setupSearchFunctionality() {
		searchField.setOnAction(event -> handleSearch());
		searchField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (searchDebounceTimeline != null)
				searchDebounceTimeline.stop();
			searchDebounceTimeline = new Timeline(new KeyFrame(Duration.millis(500), e -> handleSearch()));
			searchDebounceTimeline.play();
		});
	}

	@FXML
	private void handleSearch() {
		String keyword = searchField.getText().trim();
		try {
			if (keyword.isEmpty()) {
				isSearching = false;
				loadStudents();
			} else {
				isSearching = true;
				List<Users> searchResults = adminService.searchStudents(keyword);
				ObservableList<Users> observableList = FXCollections.observableArrayList(searchResults);
				studentTable.setItems(observableList);
			}
		} catch (SQLException e) {
			showAlert("Error", "Failed to search students: " + e.getMessage(), Alert.AlertType.ERROR);
		}
	}

	@FXML
	private void handleWarn(ActionEvent event) {
		selectedStudentForAction = studentTable.getSelectionModel().getSelectedItem();
		if (selectedStudentForAction != null) {
			if (selectedStudentForAction.getStatus() == UserStatus.banned) {
				showAlert("Warning", "This student is already banned", Alert.AlertType.WARNING);
				return;
			}

			pendingActionType = "WARN";
			try {
				switchToNotificationPage(event, "Behavior Warning", "Student Warning Notification");
			} catch (IOException e) {
				showAlert("Error", "Failed to open notification page: " + e.getMessage(), Alert.AlertType.ERROR);
			}
		} else {
			showAlert("Warning", "Please select a student first", Alert.AlertType.WARNING);
		}
	}

	@FXML
	private void handleBan(ActionEvent event) {
		selectedStudentForAction = studentTable.getSelectionModel().getSelectedItem();
		if (selectedStudentForAction != null) {
			pendingActionType = "BAN";
			try {
				switchToNotificationPage(event, "Behavior Warning", "Student Ban Notification");
			} catch (IOException e) {
				showAlert("Error", "Failed to open notification page: " + e.getMessage(), Alert.AlertType.ERROR);
			}
		} else {
			showAlert("Warning", "Please select a student first", Alert.AlertType.WARNING);
		}
	}

	@FXML
	private void handleUnban(ActionEvent event) {
		selectedStudentForAction = studentTable.getSelectionModel().getSelectedItem();

		if (selectedStudentForAction == null) {
			showAlert("Warning", "Please select a student first", Alert.AlertType.WARNING);
			return;
		}

		String currentStatus = selectedStudentForAction.getStatus().toString();
		if (!currentStatus.equalsIgnoreCase("banned")) {
			showAlert("Warning", "This student is not banned (Current status: " + currentStatus + ")",
					Alert.AlertType.WARNING);
			return;
		}

		pendingActionType = "UNBAN";
		try {
			boolean success = adminService.unbanStudent(selectedStudentForAction.getUserID());
			if (success) {
				showAlert("Success", "Student has been unbanned and warning count reset", Alert.AlertType.INFORMATION);
				loadStudents();
			} else {
				showAlert("Error", "Failed to unban student", Alert.AlertType.ERROR);
			}
		} catch (SQLException e) {
			showAlert("Error", "Failed to unban student: " + e.getMessage(), Alert.AlertType.ERROR);
		}
	}

	private void switchToNotificationPage(ActionEvent event, String category, String title) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/frontend/view/admin/Notification.fxml"));
		Parent root = loader.load();

		NotificationController notificationController = loader.getController();
		notificationController.setAdminMainController(this);
		notificationController.setTargetUserId(selectedStudentForAction.getUserID());
		notificationController.setNotificationCategory(category);
		notificationController.setNotificationTitle(title);

		Stage stage = new Stage();
		stage.setScene(new Scene(root));
		stage.setTitle("Create Notification");
		stage.centerOnScreen();
		stage.show();
	}

	public void completePendingAction() {
		if (selectedStudentForAction == null || pendingActionType == null) {
			return;
		}

		try {
			boolean success = false;
			String successMessage = "";

			switch (pendingActionType) {
			case "WARN":
				boolean wasAutoBanned = adminService.warnStudent(selectedStudentForAction.getUserID());
				if (wasAutoBanned) {
					successMessage = "Student has been automatically banned after 5 warnings";
				} else {
					int warningCount = adminService.getStudentWarningCount(selectedStudentForAction.getUserID());
					successMessage = String.format("Warning issued to student. Current warnings: %d/5", warningCount);
				}
				success = true;
				break;
			case "BAN":
				success = adminService.updateStudentStatus(selectedStudentForAction.getUserID(), "banned");
				successMessage = "Student has been banned";
				break;
			case "UNBAN":
				success = adminService.unbanStudent(selectedStudentForAction.getUserID());
				successMessage = "Student has been unbanned";
				break;
			}

			if (success) {
				showAlert("Success", successMessage, Alert.AlertType.INFORMATION);
				loadStudents();
			} else {
				showAlert("Error", "Failed to perform action", Alert.AlertType.ERROR);
			}
		} catch (SQLException e) {
			showAlert("Error", "Database error: " + e.getMessage(), Alert.AlertType.ERROR);
		} finally {
			selectedStudentForAction = null;
			pendingActionType = null;
		}
	}

	@FXML
	private void handlePrint(ActionEvent event) {
		try {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Save PDF File");
			fileChooser.setInitialFileName("Student_List_"
					+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMyyyy_HHmmss")) + ".pdf");
			fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));

			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			File file = fileChooser.showSaveDialog(stage);
			if (file == null)
				return;

			String dest = file.getAbsolutePath();
			if (!dest.toLowerCase().endsWith(".pdf"))
				dest += ".pdf";

			Document document = new Document();
			PdfWriter.getInstance(document, new FileOutputStream(dest));
			document.open();

			Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
			Paragraph titlePara = new Paragraph("STUDENT LIST", titleFont);
			titlePara.setAlignment(Element.ALIGN_CENTER);
			document.add(titlePara);

			Font dateFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
			Paragraph datePara = new Paragraph(
					"Export Date: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
					dateFont);
			datePara.setAlignment(Element.ALIGN_CENTER);
			document.add(datePara);

			document.add(Chunk.NEWLINE);

			PdfPTable pdfTable = new PdfPTable(7);
			pdfTable.setWidthPercentage(100);
			pdfTable.setWidths(new float[] { 1f, 2f, 2f, 2f, 3f, 2f, 1.5f });

			String[] headers = { "ID", "Last Name", "First Name", "Phone", "Email", "Account creation date", "Status" };
			for (String header : headers) {
				PdfPCell cell = new PdfPCell(new Phrase(header));
				cell.setBackgroundColor(new BaseColor(200, 200, 200));
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				pdfTable.addCell(cell);
			}

			DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			Font contentFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
			for (Users student : studentTable.getItems()) {
				pdfTable.addCell(new PdfPCell(new Phrase(String.valueOf(student.getUserID()), contentFont)));
				pdfTable.addCell(new Phrase(student.getUserLastName(), contentFont));
				pdfTable.addCell(new Phrase(student.getUserFirstName(), contentFont));
				pdfTable.addCell(new Phrase(student.getPhoneNumber(), contentFont));
				pdfTable.addCell(new Phrase(student.getEmail(), contentFont));
				String dateStr = student.getCreatedAt() != null ? student.getCreatedAt().format(dateFormatter) : "N/A";
				pdfTable.addCell(new Phrase(dateStr, contentFont));
				pdfTable.addCell(new Phrase(student.getStatus().toString(), contentFont));
			}

			document.add(pdfTable);
			document.close();

			showAlert("Success", "PDF file exported successfully!\nPath: " + dest, Alert.AlertType.INFORMATION);

		} catch (Exception e) {
			e.printStackTrace();
			showAlert("Error", "Failed to export file: " + e.getMessage(), Alert.AlertType.ERROR);
		}
	}

	@FXML
	private void handleNotificationButton(ActionEvent event) throws IOException {
		switchToScene("/frontend/view/admin/Notification.fxml", event);
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

	void switchToScene(String fxmlPath, ActionEvent event) throws IOException {
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