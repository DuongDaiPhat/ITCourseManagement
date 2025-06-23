package backend.controller.course;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import backend.controller.instructorCreatePageController.InstructorUpdatePageController;
import backend.controller.scene.SceneManager;
import backend.service.course.CourseService;
import backend.service.course.CourseReviewService;
import backend.service.user.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.ScrollPane;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import model.course.CourseSession;
import model.course.Courses;
import model.course.CourseReview;
import model.user.Users;

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
	private CourseReviewService courseReviewService;
	private Stage stage;
	private Scene scene;
	@FXML
	public void initialize() {
		courseReviewService = new CourseReviewService();
		
		courseNameLabel.setOnMouseClicked(event->{
			CourseSession.setCurrentCourse(course);
			try {
				this.ToAddLecturePage();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		
		// Set up ratings link click handler
		ratingsLink.setOnAction(event -> {
			if (course != null) {
				showCourseRatingDialog();
			}
		});
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

		// Update ratings link with actual rating data
		if (courseReviewService != null) {
			try {
				double avgRating = courseReviewService.getCourseAverageRating(course.getCourseID());
				int reviewCount = courseReviewService.getCourseReviewCount(course.getCourseID());
				
				if (reviewCount > 0) {
					ratingsLink.setText(String.format("%.1f ★ (%d reviews)", avgRating, reviewCount));
				} else {
					ratingsLink.setText("No reviews yet");
				}
			} catch (Exception e) {
				ratingsLink.setText("View Ratings");
			}
		} else {
			ratingsLink.setText("View Ratings");
		}

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

		SceneManager.switchSceneWithData(
			    "Update Course",
			    "/frontend/view/instructorCreatePage/instructorUpdatePage.fxml",
			    (controller, data) -> ((InstructorUpdatePageController) controller).setCourseData(data),
			    course
		);
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
		SceneManager.switchSceneReloadWithData("Add Lecture to Course", "/frontend/view/instructorCreatePage/instructorAddLecturePage.fxml", null, null);
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
	
	/**
	 * Show rating dialog for the course
	 */
	private void showCourseRatingDialog() {
		try {
			// Get course reviews and rating data
			ArrayList<CourseReview> reviews = courseReviewService.getCourseReviews(course.getCourseID());
			double averageRating = courseReviewService.getCourseAverageRating(course.getCourseID());
			int reviewCount = courseReviewService.getCourseReviewCount(course.getCourseID());
			
			// Create dialog
			Alert dialog = new Alert(Alert.AlertType.INFORMATION);
			dialog.setTitle("Course Ratings & Reviews");
			dialog.setHeaderText(course.getCourseName());
			
			// Create custom content
			VBox content = new VBox(10);
			content.setPadding(new Insets(10));
			
			// Rating summary
			HBox ratingHeader = new HBox(10);
			ratingHeader.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
			
			Label avgRatingLabel = new Label(String.format("%.1f", averageRating));
			avgRatingLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
			
			VBox starContainer = new VBox(5);
			HBox starsBox = createStarDisplay(averageRating);
			Label reviewCountLabel = new Label(reviewCount + " review" + (reviewCount != 1 ? "s" : ""));
			reviewCountLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 12px;");
			starContainer.getChildren().addAll(starsBox, reviewCountLabel);
			
			ratingHeader.getChildren().addAll(avgRatingLabel, starContainer);
			content.getChildren().add(ratingHeader);
			
			// Separator
			javafx.scene.control.Separator separator = new javafx.scene.control.Separator();
			content.getChildren().add(separator);
			
			// Reviews list
			if (reviews.isEmpty()) {
				Label noReviewsLabel = new Label("No reviews yet for this course.");
				noReviewsLabel.setStyle("-fx-text-fill: #666; -fx-font-style: italic;");
				content.getChildren().add(noReviewsLabel);
			} else {
				// Create scrollable reviews container
				VBox reviewsContainer = new VBox(10);
				
				for (CourseReview review : reviews) {
					VBox reviewCard = createReviewCard(review);
					reviewsContainer.getChildren().add(reviewCard);
				}
				
				ScrollPane scrollPane = new ScrollPane(reviewsContainer);
				scrollPane.setFitToWidth(true);
				scrollPane.setPrefHeight(300);
				scrollPane.setStyle("-fx-background-color: transparent;");
				
				content.getChildren().add(scrollPane);
			}
			
			dialog.getDialogPane().setContent(content);
			dialog.getDialogPane().setPrefWidth(500);
			dialog.showAndWait();
			
		} catch (Exception e) {
			System.err.println("Error showing course rating dialog: " + e.getMessage());
			e.printStackTrace();
			showAlert("Error", "Failed to load course ratings.");
		}
	}
	
	/**
	 * Create star display for rating
	 */
	private HBox createStarDisplay(double rating) {
		HBox starsBox = new HBox(2);
		starsBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
		
		for (int i = 1; i <= 5; i++) {
			Label star = new Label();
			star.setStyle("-fx-font-size: 16px;");
			
			if (i <= rating) {
				star.setText("★"); // Filled star
				star.setStyle("-fx-text-fill: #ffd700; -fx-font-size: 16px;");
			} else if (i - 0.5 <= rating) {
				star.setText("☆"); // Half star (simplified as empty star)
				star.setStyle("-fx-text-fill: #ffd700; -fx-font-size: 16px;");
			} else {
				star.setText("☆"); // Empty star
				star.setStyle("-fx-text-fill: #ddd; -fx-font-size: 16px;");
			}
			
			starsBox.getChildren().add(star);
		}
		
		return starsBox;
	}
	
	/**
	 * Create a review card for displaying individual reviews
	 */
	private VBox createReviewCard(CourseReview review) {
		VBox reviewCard = new VBox(5);
		reviewCard.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 10; -fx-background-radius: 5;");
		
		// Header with user info and rating
		HBox header = new HBox(10);
		header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
		
		// Get user name
		String userName = "Anonymous";
		try {
			UserService userService = new UserService();
			Users user = userService.GetUserByID(review.getUserId());
			if (user != null) {
				userName = user.getUserFirstName() + " " + user.getUserLastName();
			}
		} catch (Exception e) {
			System.err.println("Error getting user name: " + e.getMessage());
		}
		
		Label userNameLabel = new Label(userName);
		userNameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
		
		HBox userStars = createStarDisplay(review.getRating());
		
		Label dateLabel = new Label(review.getCreatedAt().toLocalDate().toString());
		dateLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 12px;");
		
		header.getChildren().addAll(userNameLabel, userStars, dateLabel);
		reviewCard.getChildren().add(header);
		
		// Comment
		if (review.getComment() != null && !review.getComment().trim().isEmpty()) {
			Label commentLabel = new Label(review.getComment());
			commentLabel.setWrapText(true);
			commentLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #333;");
			reviewCard.getChildren().add(commentLabel);
		}
		
		return reviewCard;
	}
	
	/**
	 * Show alert dialog
	 */
	private void showAlert(String title, String message) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
}