package backend.controller.course;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import backend.controller.instructorCreatePageController.InstructorUpdatePageController;
import backend.service.course.CourseService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import model.course.CourseSession;
import model.course.Courses;

public class CourseItemController implements ICourseItemController{
	@FXML
	private HBox courseItemContainer;
	@FXML
	private ImageView courseThumbnail;
	@FXML
	private Label courseNameLabel;
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
	private Hyperlink ratingsLink;
	@FXML
	private Hyperlink updateLink;
	@FXML
	private Hyperlink removeLink;
	@FXML
	private Button addLectureButton;
	@FXML
	private Button publishButton;

	private Courses course;
	private CourseService courseService;
	private Stage stage;
	private Scene scene;

	@FXML
	public void initialize() {
		
	}

	public void setCourseData(Courses course) {
		if (course == null) {
			throw new IllegalArgumentException("Course cannot be null");
		}
		this.course = course;
		updateUI();
	}

	private void updateUI() {
		courseNameLabel.setText(course.getCourseName());
		languageLabel.setText(course.getLanguage().toString());
		technologyLabel.setText(course.getTechnology().toString());
		levelLabel.setText(course.getLevel().toString());
		categoryLabel.setText(course.getCategory().toString().replace('_', ' '));
		priceLabel.setText(String.format("$%.2f", course.getPrice()));
		createdDateLabel.setText(course.getCreatedAt().toString());

		if (course.getThumbnailURL() != null && !course.getThumbnailURL().isEmpty()) {
			try {
				Image image = new Image(new File(course.getThumbnailURL()).toURI().toString());
				courseThumbnail.setImage(image);
			} catch (Exception e) {
				courseThumbnail.setImage(new Image(getClass().getResourceAsStream("/images/default_image.png")));
			}
		}
		if (course.isApproved()) {
		    publishButton.setText("Approved");
		    publishButton.setDisable(true);
		} else {
		    publishButton.setDisable(false);
		    publishButton.setText(course.isPublished() ? "Publishing" : "Publish");
		}
	}

	@FXML
	public void handleUpdateCourse(ActionEvent event) {
		if (course == null) {
			System.err.println("No course data available to update");
			return;
		}

		try {
			FXMLLoader loader = new FXMLLoader(
					getClass().getResource("/frontend/view/instructorCreatePage/instructorUpdatePage.fxml"));
			Parent root = loader.load();

			InstructorUpdatePageController controller = loader.getController();
			controller.setCourseData(course);

			// Lấy stage từ nút được click
			Stage stage = (Stage) ((Hyperlink) event.getSource()).getScene().getWindow();
			stage.setScene(new Scene(root));
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Error loading update page: " + e.getMessage());
		}
	}

	@FXML
	private void handleRemoveCourse(ActionEvent event) {
		if (course == null) {
			System.err.println("No course data available to remove");
			return;
		}

		try {
			if (courseService == null) {
				courseService = new CourseService();
			}
			courseService.deleteCourse(course.getCourseID());

			if (courseItemContainer != null && courseItemContainer.getParent() != null) {
				((HBox) courseItemContainer.getParent()).getChildren().remove(courseItemContainer);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Error deleting course: " + e.getMessage());
		}
	}
	@FXML
	public void AddLecture(ActionEvent event) throws IOException {
		if (course == null) {
			System.err.println("No course data available to remove");
			return;
		}
		CourseSession.setCurrentCourse(course);	
		this.ToAddLecturePage();
	}
	private void ToAddLecturePage() throws IOException {
		Parent root = FXMLLoader
				.load(getClass().getResource("/frontend/view/instructorCreatePage/instructorAddLecturePage.fxml"));
		Rectangle2D rec = Screen.getPrimary().getVisualBounds();
		stage = (Stage) courseNameLabel.getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.setX((rec.getWidth() - stage.getWidth()) / 2);
		stage.setY((rec.getHeight() - stage.getHeight()) / 2);
		stage.show();
	}
	
	public void PublishCourse(ActionEvent event) throws SQLException {
		if (courseService == null) {
			courseService = new CourseService();
		}

		boolean newPublishStatus = !course.isPublished(); // toggle
		courseService.PublishByID(course.getCourseID(), newPublishStatus);
		refreshCourse(); 
	}
	private void refreshCourse() {
		try {
			this.course = courseService.GetCourseByID(course.getCourseID());
			updateUI(); 
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}