package backend.controller.course;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

import backend.controller.admin.NotificationController;
import backend.service.course.CourseService;
import backend.service.user.UserService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import model.course.Courses;
import model.user.Users;

public class CoursePendingItemController implements Initializable {

	@FXML
	private ImageView courseThumbnail;
	@FXML
	private Label courseNameLabel;
	@FXML
	private Label instructorNameLabel;
	@FXML
	private Label languageLabel;
	@FXML
	private Label technologyLabel;
	@FXML
	private Label levelLabel;
	@FXML
	private Label categoryLabel;
	@FXML
	private Label priceLabel;
	@FXML
	private Label createdDateLabel;
	@FXML
	private Label lectureCountLabel;
	@FXML
	private Button approveButton;
	@FXML
	private Button declineButton;

	private Courses course;
	private CourseService courseService = new CourseService();
	private UserService userService = new UserService();
	private Runnable refreshCallback;
	private Users instructor;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		approveButton.getStyleClass().add("action-button");
		approveButton.getStyleClass().add("approve");

		declineButton.getStyleClass().add("action-button");
		declineButton.getStyleClass().add("decline");

		approveButton.setOnAction(e -> handleApproveCourse());
		declineButton.setOnAction(e -> handleDeclineCourse());
	}

	public void setCourseData(Courses course, Runnable refreshCallback) {
		this.course = course;
		this.refreshCallback = refreshCallback;
		updateUI();
		loadInstructor();
	}

	private void updateUI() {
		if (course == null)
			return;

		courseNameLabel.setText(course.getCourseName());
		languageLabel.setText(course.getLanguage().toString());
		technologyLabel.setText(course.getTechnology().toString());
		levelLabel.setText(course.getLevel().toString());
		categoryLabel.setText(course.getCategory().toString().replace('_', ' '));
		priceLabel.setText(String.format("$%.2f", course.getPrice()));
		createdDateLabel.setText(course.getCreatedAt().toString());

		try {
			int lectureCount = courseService.getLectureByCourseID(course.getCourseID()).size();
			lectureCountLabel.setText(lectureCount + " lectures");
		} catch (SQLException e) {
			lectureCountLabel.setText("0 lecture");
			e.printStackTrace();
		}

		try {
			if (course.getThumbnailURL() != null && !course.getThumbnailURL().isEmpty()) {
				Image image = new Image(new File(course.getThumbnailURL()).toURI().toString());
				courseThumbnail.setImage(image);
			} else {
				courseThumbnail.setImage(new Image(getClass().getResourceAsStream("/images/default_image.png")));
			}
		} catch (Exception e) {
			courseThumbnail.setImage(new Image(getClass().getResourceAsStream("/images/default_image.png")));
		}
	}

	private void loadInstructor() {
		try {
			instructor = userService.GetUserByID(course.getUserID());
			if (instructor != null) {
				instructorNameLabel.setText(instructor.getUserFirstName() + " " + instructor.getUserLastName());
			} else {
				instructorNameLabel.setText("Unknown Instructor");
			}
		} catch (Exception e) {
			instructorNameLabel.setText("Unknown Instructor");
			e.printStackTrace();
		}
	}

	private void handleApproveCourse() {
		try {
			course.setApproved(true);
			courseService.updateCourse(course);
			showAlert("Success", "Course approved successfully!", Alert.AlertType.INFORMATION);
			if (refreshCallback != null) {
				refreshCallback.run();
			}
		} catch (SQLException e) {
			showAlert("Error", "Failed to approve course: " + e.getMessage(), Alert.AlertType.ERROR);
		}
	}

	private void handleDeclineCourse() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/frontend/view/admin/Notification.fxml"));
			Parent root = loader.load();

			NotificationController notificationController = loader.getController();
			notificationController.setTargetUserId(instructor.getUserID());
			notificationController.setNotificationCategory("Course Rejected");
			notificationController.setNotificationTitle("Course Rejection Notification");

			Stage stage = new Stage();
			stage.setScene(new Scene(root));
			stage.setTitle("Create Notification");
			stage.centerOnScreen();
			stage.showAndWait(); // Wait for notification to be sent before proceeding

			// After notification is sent, update course status
			course.setRejected(true);
			course.setUpdatedAt(LocalDateTime.now());
			courseService.updateCourse(course);

			showAlert("Info", "Course has been declined and notification sent.", Alert.AlertType.INFORMATION);
			if (refreshCallback != null) {
				refreshCallback.run();
			}
		} catch (IOException | SQLException e) {
			showAlert("Error", "Failed to decline course: " + e.getMessage(), Alert.AlertType.ERROR);
			e.printStackTrace();
		}
	}

	private void showAlert(String title, String message, Alert.AlertType type) {
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
}