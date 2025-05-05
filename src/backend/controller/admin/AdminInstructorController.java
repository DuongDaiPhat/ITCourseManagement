package backend.controller.admin;

import java.io.IOException;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

import java.io.File;
import java.io.FileOutputStream;
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
import javafx.stage.FileChooser;

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
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

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
	@FXML
	private Label refreshNotificationLabel;

	private AdminService adminService = new AdminService();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		try {
			setupInstructorTable();
			loadInstructors();
			loadUserInfo();

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

		} catch (SQLException e) {
			showAlert("Error", "Failed to load instructor data: " + e.getMessage(), Alert.AlertType.ERROR);
		}
	}

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
		try {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Save PDF File");
			fileChooser.setInitialFileName("Instructor_List_"
					+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMyyyy_HHmmss")) + ".pdf");
			fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));

			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			File file = fileChooser.showSaveDialog(stage);

			if (file == null) {
				return; 
			}

			String dest = file.getAbsolutePath();
			if (!dest.toLowerCase().endsWith(".pdf")) {
				dest += ".pdf";
			}

			// Tạo pdf
			Document document = new Document();
			PdfWriter.getInstance(document, new FileOutputStream(dest));
			document.open();

			Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
			Paragraph titlePara = new Paragraph("INSTRUCTOR LIST", titleFont);
			titlePara.setAlignment(Element.ALIGN_CENTER);
			document.add(titlePara);

			Font dateFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
			Paragraph datePara = new Paragraph(
					"Export Date: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
					dateFont);
			datePara.setAlignment(Element.ALIGN_CENTER);
			document.add(datePara);

			document.add(Chunk.NEWLINE);

			PdfPTable pdfTable = new PdfPTable(8);
			pdfTable.setWidthPercentage(100);
			pdfTable.setWidths(new float[] { 1f, 2f, 2f, 2f, 3f, 2f, 2f, 1.5f }); // Column ratios

			String[] headers = { "ID", "Last Name", "First Name", "Phone", "Email", "Course Count",
					"Account Creation Date", "Status" };
			for (String header : headers) {
				PdfPCell cell = new PdfPCell(new Phrase(header));
				cell.setBackgroundColor(new BaseColor(200, 200, 200)); // Light gray
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				pdfTable.addCell(cell);
			}

			DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			Font contentFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

			for (Users instructor : instructorTable.getItems()) {
				PdfPCell idCell = new PdfPCell(new Phrase(String.valueOf(instructor.getUserID()), contentFont));
				idCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				pdfTable.addCell(idCell);

				pdfTable.addCell(new Phrase(instructor.getUserLastName(), contentFont));

				pdfTable.addCell(new Phrase(instructor.getUserFirstName(), contentFont));

				PdfPCell phoneCell = new PdfPCell(new Phrase(instructor.getPhoneNumber(), contentFont));
				phoneCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				pdfTable.addCell(phoneCell);

				pdfTable.addCell(new Phrase(instructor.getEmail(), contentFont));

				PdfPCell courseCountCell = new PdfPCell(
						new Phrase(String.valueOf(instructor.getCourseCount()), contentFont));
				courseCountCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				pdfTable.addCell(courseCountCell);

				String dateString = instructor.getCreatedAt() != null ? instructor.getCreatedAt().format(dateFormatter)
						: "N/A";
				PdfPCell dateCell = new PdfPCell(new Phrase(dateString, contentFont));
				dateCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				pdfTable.addCell(dateCell);

				PdfPCell statusCell = new PdfPCell(new Phrase(instructor.getStatus().toString(), contentFont));
				statusCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				pdfTable.addCell(statusCell);
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