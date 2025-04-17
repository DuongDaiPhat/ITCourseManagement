package backend.controller.InstructorMainPage;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import model.course.Courses;
import model.user.Session;
import model.user.Users;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ButtonBar;
import javafx.scene.layout.VBox;
import javafx.scene.control.DialogPane;
import javafx.geometry.Pos;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import backend.controller.course.CourseItemController;
import backend.service.course.CourseService;
import backend.service.user.UserService;

public class InstructorMainPageController {
	private Users currentUser;
	@FXML
	private Label usernameLabel;

	@FXML
	private VBox courseListContainer;

	@FXML
	private Label emptyCourseLabel;

	@FXML
	private Label createCourse;

	private Stage stage;
	private Scene scene;
	private CourseService courseService;
	private UserService userService;

	@FXML
	public void initialize() throws SQLException {
		createCourse.setOnMouseClicked(event -> {
			try {
				ToCreateCoursePage();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		courseService = new CourseService();
		userService = new UserService();
		loadUser();
	}

	public void loadUser() throws SQLException {
		this.currentUser = Session.getCurrentUser();
		this.loadUserInfo();
		this.loadCourses();
	}

	public void CreateCoursePage(ActionEvent e) throws IOException {
		this.ToCreateCoursePage();
	}

	private void ToCreateCoursePage() throws IOException {
		Parent root = FXMLLoader
				.load(getClass().getResource("/frontend/view/instructorCreatePage/instructorCreatePage.fxml"));
		Rectangle2D rec = Screen.getPrimary().getVisualBounds();
		stage = (Stage) usernameLabel.getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.setX((rec.getWidth() - stage.getWidth()) / 2);
		stage.setY((rec.getHeight() - stage.getHeight()) / 2);
		stage.show();
	}

	private void loadUserInfo() {
		if (currentUser != null) {
			usernameLabel.setText(currentUser.getUserFirstName() + " " + currentUser.getUserLastName());
		}
	}

	private void loadCourses() throws SQLException {
		courseListContainer.getChildren().clear();
		List<Courses> courses = courseService.GetCourseByUserID(currentUser.getUserID());

		if (courses == null || courses.isEmpty()) {
			emptyCourseLabel.setVisible(true);
			courseListContainer.getChildren().add(emptyCourseLabel);
		} else {
			emptyCourseLabel.setVisible(false);

			for (Courses course : courses) {
				try {
					FXMLLoader loader = new FXMLLoader(
							getClass().getResource("/frontend/view/instructorMainPage/CourseItem.fxml"));
					HBox courseItem = loader.load();
					Separator sp = new Separator();

					// Get controller and set course data
					CourseItemController controller = loader.getController();
					controller.setCourseData(course);

					// Get the remove link and set its action
					Hyperlink removeLink = (Hyperlink) findNodeById(courseItem, "removeLink");
					if (removeLink != null) {
						removeLink.setOnAction(event -> {
							try {
								handleRemoveCourse(course);
							} catch (SQLException e) {
								e.printStackTrace();
								showStyledAlert("Error", "Failed to delete course: " + e.getMessage(),
										Alert.AlertType.ERROR);
							}
						});
					}

					// Get the update link and set its action
					Hyperlink updateLink = (Hyperlink) findNodeById(courseItem, "updateLink");
					if (updateLink != null) {
						updateLink.setOnAction(event -> {
							controller.handleUpdateCourse(event);
						});
					}

					courseListContainer.getChildren().add(sp);
					courseListContainer.getChildren().add(courseItem);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void handleRemoveCourse(Courses course) throws SQLException {
		// Create custom alert content
		Label messageLabel = new Label("Are you sure you want to delete the course: " + course.getCourseName() + "?");
		messageLabel.setStyle("-fx-text-fill: #004AAD; -fx-font-size: 16px; -fx-font-weight: bold;");
		messageLabel.setAlignment(Pos.CENTER);
		messageLabel.setWrapText(true);
		messageLabel.setMaxWidth(300);

		VBox contentBox = new VBox(15, messageLabel);
		contentBox.setAlignment(Pos.CENTER);

		// Create alert
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Confirm Deletion");
		alert.setHeaderText(null);

		DialogPane dialogPane = alert.getDialogPane();
		dialogPane.setContent(contentBox);
		dialogPane.getStylesheets().add(
				getClass().getResource("/frontend/view/instructorMainPage/instructorMainPage.css").toExternalForm());
		dialogPane.getStyleClass().add("confirmation-alert");
		dialogPane.setPrefWidth(400);

		// Custom buttons
		ButtonType deleteButton = new ButtonType("Delete", ButtonBar.ButtonData.OK_DONE);
		ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
		alert.getButtonTypes().setAll(deleteButton, cancelButton);

		Optional<ButtonType> result = alert.showAndWait();

		if (result.isPresent() && result.get() == deleteButton) {
			// Delete the course image file if it exists
			if (course.getThumbnailURL() != null && !course.getThumbnailURL().isEmpty()) {
				try {
					File imageFile = new File(course.getThumbnailURL());
					if (imageFile.exists()) {
						if (!imageFile.delete()) {
							System.err.println("Failed to delete course image file");
						}
					}
				} catch (Exception e) {
					System.err.println("Error deleting course image: " + e.getMessage());
				}
			}

			// Delete the course from database
			int deleteResult = courseService.deleteCourse(course.getCourseID());
			if (deleteResult > 0) {
				showSuccessAlert("Course '" + course.getCourseName() + "' was deleted successfully!");
				// Refresh the course list
				loadCourses();
			} else {
				showStyledAlert("Error", "Failed to delete course from database. Please try again.",
						Alert.AlertType.ERROR);
			}
		}
	}

	private void showSuccessAlert(String message) {
		try {
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("Success message");
			alert.setHeaderText(null);

			Label messageLabel = new Label(message);
			messageLabel.setStyle("-fx-text-fill: #004AAD; -fx-font-size: 16px; -fx-font-weight: bold;");
			messageLabel.setAlignment(Pos.CENTER);
			messageLabel.setWrapText(true);
			messageLabel.setMaxWidth(300);

			VBox contentBox = new VBox(15, messageLabel);
			contentBox.setAlignment(Pos.CENTER);

			DialogPane dialogPane = alert.getDialogPane();
			dialogPane.getStylesheets().add(getClass()
					.getResource("/frontend/view/instructorMainPage/instructorMainPage.css").toExternalForm());
			dialogPane.getStyleClass().add("success-alert");
			dialogPane.setContent(contentBox);
			dialogPane.setPrefWidth(400);

			alert.showAndWait();
		} catch (Exception e) {
			e.printStackTrace();
			Alert simpleAlert = new Alert(Alert.AlertType.INFORMATION, message);
			simpleAlert.showAndWait();
		}
	}

	private Optional<ButtonType> showStyledAlert(String title, String message, Alert.AlertType type) {
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);

		// Apply custom style based on alert type
		String stylesheet = getClass().getResource("/frontend/view/instructorMainPage/instructorMainPage.css")
				.toExternalForm();
		alert.getDialogPane().getStylesheets().add(stylesheet);

		switch (type) {
		case CONFIRMATION:
			alert.getDialogPane().getStyleClass().add("confirmation-alert");
			break;
		case ERROR:
			alert.getDialogPane().getStyleClass().add("error-alert");
			break;
		case INFORMATION:
			alert.getDialogPane().getStyleClass().add("success-alert");
			break;
		default:
			alert.getDialogPane().getStyleClass().add("dialog-pane");
		}

		// Customize buttons
		if (type == Alert.AlertType.CONFIRMATION) {
			ButtonType okButton = new ButtonType("Delete", ButtonBar.ButtonData.OK_DONE);
			ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
			alert.getButtonTypes().setAll(okButton, cancelButton);
		} else {
			ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
			alert.getButtonTypes().setAll(okButton);
		}

		return alert.showAndWait();
	}

	private Node findNodeById(Node parent, String id) {
		if (parent.getId() != null && parent.getId().equals(id)) {
			return parent;
		}

		if (parent instanceof javafx.scene.Parent) {
			for (Node child : ((javafx.scene.Parent) parent).getChildrenUnmodifiable()) {
				Node result = findNodeById(child, id);
				if (result != null) {
					return result;
				}
			}
		}
		return null;
	}
}