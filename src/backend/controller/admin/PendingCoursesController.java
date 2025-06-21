package backend.controller.admin;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import backend.controller.course.CoursePendingItemController;
import backend.service.course.CourseService;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import model.course.Courses;

public class PendingCoursesController implements Initializable {

	@FXML
	private VBox coursesContainer;
	@FXML
	private Button approveButton;
	@FXML
	private Button declineButton;

	private CourseService courseService = new CourseService();
	private Courses selectedCourse;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		loadPendingCourses();
	}

	private void loadPendingCourses() {
		coursesContainer.getChildren().clear();
		try {
			List<Courses> pendingCourses = courseService.getAllCourses().stream()
					.filter(course -> !course.isApproved() && !course.isRejected()) // Thêm điều kiện
																					// !course.isRejected()
					.sorted((c1, c2) -> c2.getCreatedAt().compareTo(c1.getCreatedAt())).collect(Collectors.toList());

			if (pendingCourses.isEmpty()) {
				Label emptyLabel = new Label("No pending courses available");
				emptyLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #666;");
				coursesContainer.getChildren().add(emptyLabel);
			} else {
				for (Courses course : pendingCourses) {
					HBox courseCard = createCourseCard(course);
					coursesContainer.getChildren().add(courseCard);
				}
			}
		} catch (SQLException e) {
			showAlert("Error", "Failed to load pending courses: " + e.getMessage(), Alert.AlertType.ERROR);
		}
	}

	private HBox createCourseCard(Courses course) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/frontend/view/admin/CoursePendingItem.fxml"));
			HBox courseCard = loader.load();

			CoursePendingItemController controller = loader.getController();
			controller.setCourseData(course, this::loadPendingCourses);

			return courseCard;
		} catch (IOException e) {
			e.printStackTrace();
			// Fallback to old implementation if error occurs
			HBox fallbackCard = new HBox(new Label("Error loading course card"));
			fallbackCard.getStyleClass().add("course-card");
			return fallbackCard;
		}
	}

	private Label createMetaLabel(String text, String color) {
		Label label = new Label(text);
		label.setStyle("-fx-font-size: 13px; " + "-fx-text-fill: white; " + "-fx-padding: 5px 10px; "
				+ "-fx-background-color: " + color + "; " + "-fx-background-radius: 4px;");
		return label;
	}

	@FXML
	private void handleApproveCourse() {
		if (selectedCourse != null) {
			try {
				selectedCourse.setApproved(true);
				courseService.updateCourse(selectedCourse);
				showAlert("Success", "Course approved successfully!", Alert.AlertType.INFORMATION);
				loadPendingCourses();
				approveButton.setDisable(true);
				declineButton.setDisable(true);
			} catch (SQLException e) {
				showAlert("Error", "Failed to approve course: " + e.getMessage(), Alert.AlertType.ERROR);
			}
		} else {
			showAlert("Warning", "Please select a course first", Alert.AlertType.WARNING);
		}
	}

	@FXML
	private void handleDeclineCourse() {
		if (selectedCourse != null) {
			try {
				// Đánh dấu khóa học đã bị từ chối
				selectedCourse.setRejected(true);
				courseService.updateCourse(selectedCourse); // Lưu vào database

				showAlert("Info", "Course has been declined. Instructor has been notified.",
						Alert.AlertType.INFORMATION);
				loadPendingCourses(); // Load lại danh sách
				approveButton.setDisable(true);
				declineButton.setDisable(true);
			} catch (SQLException e) {
				showAlert("Error", "Failed to decline course: " + e.getMessage(), Alert.AlertType.ERROR);
			}
		} else {
			showAlert("Warning", "Please select a course first", Alert.AlertType.WARNING);
		}
	}

	@FXML
	private void handleViewStudents(ActionEvent event) throws IOException {
		switchToScene("/frontend/view/admin/AdminMainPage.fxml", event);
	}

	@FXML
	private void handleViewInstructors(ActionEvent event) throws IOException {
		switchToScene("/frontend/view/admin/Instructors.fxml", event);
	}

	@FXML
	private void handleViewPendingCourses(ActionEvent event) throws IOException {
	}

	@FXML
	private void handleViewAllCourses(ActionEvent event) throws IOException {
		switchToScene("/frontend/view/admin/AllCourses.fxml", event);
	}

	@FXML
	private void handleViewRevenue(ActionEvent event) throws IOException {
		switchToScene("/frontend/view/admin/Revenue.fxml", event);
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