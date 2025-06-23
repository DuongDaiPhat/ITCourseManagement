package backend.controller.admin;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import backend.controller.course.CourseApproveItemController;
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
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import model.course.Courses;

public class AllCoursesController implements Initializable {

	@FXML
	private VBox coursesContainer;

	private CourseService courseService = new CourseService();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		loadApprovedCourses();
	}

	private void loadApprovedCourses() {
		coursesContainer.getChildren().clear();
		try {
			List<Courses> approvedCourses = courseService.getAllCourses().stream().filter(course -> course.isApproved())
					.collect(Collectors.toList());

			for (Courses course : approvedCourses) {
				HBox courseCard = createCourseCard(course);
				coursesContainer.getChildren().add(courseCard);
			}
		} catch (SQLException e) {
			showAlert("Error", "Failed to load approved courses: " + e.getMessage(), Alert.AlertType.ERROR);
		}
	}

	private HBox createCourseCard(Courses course) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/frontend/view/admin/CourseApproveItem.fxml"));
			HBox courseCard = loader.load();

			CourseApproveItemController controller = loader.getController();
			controller.setCourseData(course, this::loadApprovedCourses);

			return courseCard;
		} catch (IOException e) {
			e.printStackTrace();
			HBox fallbackCard = new HBox(new Label("Error loading course card"));
			fallbackCard.getStyleClass().add("course-card");
			return fallbackCard;
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
		switchToScene("/frontend/view/admin/PendingCourses.fxml", event);
	}

	@FXML
	private void handleViewAllCourses(ActionEvent event) throws IOException {
	}

	@FXML
	private void handleViewRevenue(ActionEvent event) throws IOException {
		switchToScene("/frontend/view/admin/Revenue.fxml", event);
	}

	@FXML
	private void handleNotificationButton(ActionEvent event) throws IOException {
		switchToScene("/frontend/view/admin/Notification.fxml", event);
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