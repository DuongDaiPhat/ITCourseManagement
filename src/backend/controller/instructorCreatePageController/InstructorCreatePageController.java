package backend.controller.instructorCreatePageController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.UnaryOperator;

import backend.controller.InstructorMainPage.InstructorMainPageController;
import backend.controller.scene.SceneManager;
import backend.service.course.CourseService;
import backend.service.user.UserService;
import javafx.application.Platform;
import backend.service.course.CourseReviewService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.HBox;
import javafx.scene.control.ScrollPane;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;
import model.course.Category;
import model.course.Courses;
import model.course.Language;
import model.course.Level;
import model.course.Technology;
import model.user.Session;
import model.user.Users;
import model.notification.UserNotification;
import backend.repository.notification.NotificationRepository;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Priority;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.course.CourseReview;

public class InstructorCreatePageController implements IInstructorCreatePageController {
	@FXML
	private TextField courseName;
	@FXML
	private ComboBox<String> technology;
	@FXML
	private ComboBox<String> language;
	@FXML
	private ComboBox<String> category;
	@FXML
	private ComboBox<String> level;
	@FXML
	private TextField price;
	@FXML
	private TextArea description;
	@FXML
	private ImageView thumbnail;
	@FXML
	private Label myCourse;
	@FXML
	private TextField searchField;
	@FXML
	private Button searchButton;
	@FXML
	private Label exploreLabel;
	@FXML
	private Label createCourse;
	@FXML
	private Button profileButton;
	@FXML
	private Button notificationButton;

	private UserService userService;
	private CourseService courseService;
	private CourseReviewService courseReviewService;
	private int userID;
	private File selectedImageFile;

	private ContextMenu profileMenu;
	private ImageView notificationIcon;
	private Circle notificationBadge;
	private Popup notificationPopup;
	private VBox notificationPopupContent;
	private NotificationRepository notificationRepository = new NotificationRepository();

	@FXML
	public void initialize() {
		UnaryOperator<TextFormatter.Change> filter = change -> {
			String newText = change.getControlNewText();
			if (newText.matches("\\d*(\\.\\d{0,2})?")) {
				return change;
			} else {
				return null;
			}
		};
		TextFormatter<Double> formatter = new TextFormatter<>(new DoubleStringConverter(), 0.0, filter);
		price.setTextFormatter(formatter);

		if (category.getItems().isEmpty()) {
			category.getItems().addAll(getCategoryDisplayNames());
		}

		if (level.getItems().isEmpty()) {
			level.getItems().addAll("Beginner", "Intermediate", "Advanced", "All Level");
		}

		myCourse.setOnMouseClicked(event -> {
			SceneManager.switchSceneReloadWithData("My Course",
					"/frontend/view/instructorMainPage/instructorMainPage.fxml", null, null);
		});

		// Setup navigation events
		exploreLabel.setOnMouseClicked(event -> goToExplorePage());

		technology.setVisibleRowCount(5);
		technology.setEditable(false);

		language.setVisibleRowCount(5);
		language.setEditable(false);

		category.setVisibleRowCount(5);
		category.setEditable(false);

		level.setEditable(false);
		userService = new UserService();
		courseService = new CourseService();
		courseReviewService = new CourseReviewService();
		userID = Session.getCurrentUser().getUserID();

		setupProfileMenu();
		profileButton.setOnAction(event -> showProfileMenu());

		setupNotificationButton();
		notificationButton.setOnAction(event -> toggleNotificationPopup());
		loadNotifications();
	}

	private void setupNotificationButton() {
		notificationIcon = new ImageView(
				new Image(getClass().getResourceAsStream("/images/main_page/icon/Notification.png")));
		notificationIcon.setFitHeight(20);
		notificationIcon.setFitWidth(20);

		notificationBadge = new Circle(5);
		notificationBadge.setFill(Color.TRANSPARENT);
		notificationBadge.setStroke(Color.TRANSPARENT);
		notificationBadge.setTranslateX(8);
		notificationBadge.setTranslateY(-8);
		notificationBadge.getStyleClass().add("notification-badge");

		StackPane stackPane = new StackPane();
		stackPane.getChildren().addAll(notificationIcon, notificationBadge);

		notificationButton.setGraphic(stackPane);
		notificationButton.getStyleClass().add("notification-button");

		notificationPopup = new Popup();
		notificationPopup.setAutoHide(true);

		notificationPopupContent = new VBox();
		notificationPopupContent.setStyle(
				"-fx-background-color: white; -fx-border-color: #cccccc; -fx-border-width: 1px; -fx-padding: 10px;");
		notificationPopupContent.setPrefWidth(300);
		notificationPopupContent.setMaxHeight(400);

		ScrollPane scrollPane = new ScrollPane(notificationPopupContent);
		scrollPane.setFitToWidth(true);
		scrollPane.setStyle("-fx-background: white; -fx-background-color: white;");

		Label titleLabel = new Label("Notifications");
		titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 0 0 10 0;");

		VBox popupContainer = new VBox(titleLabel, scrollPane);
		popupContainer.setStyle("-fx-background-color: white; -fx-padding: 10px;");
		popupContainer.setPrefWidth(300);

		notificationPopup.getContent().add(popupContainer);
	}

	private void toggleNotificationPopup() {
		if (notificationPopup.isShowing()) {
			notificationPopup.hide();
		} else {
			loadNotifications();
			notificationPopup.show(notificationButton, notificationButton.localToScreen(0, 0).getX() - 250,
					notificationButton.localToScreen(0, 0).getY() + notificationButton.getHeight() + 5);
		}
	}

	private void loadNotifications() {
		try {
			int userId = Session.getCurrentUser().getUserID();
			NotificationRepository.NotificationResult result = notificationRepository.getNotificationsAndCount(userId);
			List<UserNotification> notifications = result.getNotifications();
			int unreadCount = result.getUnreadCount();

			Platform.runLater(() -> {
				if (unreadCount > 0) {
					notificationBadge.setFill(Color.web("#ff4757"));
					notificationBadge.setStroke(Color.web("#ff4757"));
				} else {
					notificationBadge.setFill(Color.TRANSPARENT);
					notificationBadge.setStroke(Color.TRANSPARENT);
				}

				notificationPopupContent.getChildren().clear();

				if (notifications.isEmpty()) {
					Label emptyLabel = new Label("No new notifications");
					emptyLabel.setStyle("-fx-text-fill: #666666; -fx-padding: 10px;");
					notificationPopupContent.getChildren().add(emptyLabel);
				} else {
					for (UserNotification notification : notifications) {
						VBox notificationItem = createNotificationItem(notification);
						notificationPopupContent.getChildren().add(notificationItem);
					}
				}

				Button viewAllButton = new Button("View All Notifications");
				viewAllButton.setStyle("-fx-background-color: #25b6aa; -fx-text-fill: white; -fx-padding: 5px;");
				viewAllButton.setOnAction(e -> {
					notificationPopup.hide();
					SceneManager.switchScene("Notifications", "/frontend/view/notifications/NotificationView.fxml");
				});
				notificationPopupContent.getChildren().add(viewAllButton);
			});
		} catch (SQLException e) {
			e.printStackTrace();
			showAlert("Database Error", "Failed to load notifications: " + e.getMessage(), Alert.AlertType.ERROR);
		} catch (Exception e) {
			e.printStackTrace();
			showAlert("Error", "Failed to load notifications: " + e.getMessage(), Alert.AlertType.ERROR);
		}
	}

	private VBox createNotificationItem(UserNotification notification) {
		VBox container = new VBox();
		container.setStyle(
				"-fx-background-color: #f9f9f9; -fx-border-color: #eeeeee; -fx-border-width: 0 0 1 0; -fx-padding: 10px;");
		container.setSpacing(5);

		HBox header = new HBox();
		header.setAlignment(Pos.CENTER_LEFT);
		header.setSpacing(5);

		Label iconLabel = new Label(notification.getIcon());
		iconLabel.setStyle("-fx-font-size: 16px;");

		Label titleLabel = new Label(notification.getNotificationName());
		titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");

		Label categoryLabel = new Label("(" + notification.getCategory() + ")");
		categoryLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666666;");

		header.getChildren().addAll(iconLabel, titleLabel, categoryLabel);

		Label contentLabel = new Label(notification.getContent());
		contentLabel.setStyle("-fx-font-size: 12px; -fx-wrap-text: true;");
		contentLabel.setMaxWidth(280);

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");
		String timeText = notification.getNotifiedAt() != null ? notification.getNotifiedAt().format(formatter)
				: "Unknown time";
		Label timeLabel = new Label(timeText);
		timeLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #999999; -fx-padding: 5 0 0 0;");

		container.getChildren().addAll(header, contentLabel, timeLabel);

		container.setOnMouseClicked(e -> {
			new Thread(() -> {
				try {
					notificationRepository.markNotificationAsRead(notification.getNotificationID(),
							notification.getUserID());
					loadNotifications();
					Platform.runLater(() -> {
						SceneManager.switchScene("Notification Detail",
								"/frontend/view/notifications/NotificationDetailView.fxml",
								notification.getNotificationID());
					});
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}).start();
		});

		return container;
	}

	private void setupProfileMenu() {
		profileMenu = new ContextMenu();

		MenuItem profileInfoItem = new MenuItem("My information");
		MenuItem paymentMethodItem = new MenuItem("Payment");
		MenuItem logoutItem = new MenuItem("Log out");

		profileInfoItem.getStyleClass().add("menu-item");
		paymentMethodItem.getStyleClass().add("menu-item");
		logoutItem.getStyleClass().add("menu-item");
		profileInfoItem.setOnAction(event -> showProfileInfo());
		paymentMethodItem.setOnAction(event -> showPaymentMethods());
		logoutItem.setOnAction(event -> logout());

		profileMenu.getItems().addAll(profileInfoItem, paymentMethodItem, logoutItem);
		profileMenu.getStyleClass().add("ProfileMenu.css");
	}

	public void showProfileMenu() {
		profileMenu.show(profileButton, profileButton.localToScreen(0, profileButton.getHeight()).getX(),
				profileButton.localToScreen(0, profileButton.getHeight()).getY());
	}

	private void showProfileInfo() {
		SceneManager.switchScene("My Information", "/frontend/view/UserProfile/UserProfile.fxml");
	}

	private void showPaymentMethods() {
		SceneManager.switchScene("Payment", "/frontend/view/payment/paymentMethod.fxml");
	}

	private void logout() {
		SceneManager.clearSceneCache();
		SceneManager.switchScene("Login", "/frontend/view/login/Login.fxml");
	}

	private String convertDisplayToLevel(String displayLevel) {
		if ("All Level".equals(displayLevel)) {
			return "ALLLEVEL";
		}
		return displayLevel.toUpperCase();
	}

	private void showAlert(String title, String message, Alert.AlertType type) {
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

	public void SelectImage(ActionEvent e) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Chọn ảnh");
		fileChooser.getExtensionFilters()
				.addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));

		File file = fileChooser.showOpenDialog(null);
		if (file != null) {
			selectedImageFile = file;
			Image image = new Image(file.toURI().toString());
			thumbnail.setImage(image);
		}
	}

	public void CreateCourse(ActionEvent e) throws IOException, SQLException {
		Users currentUser = Session.getCurrentUser();
		if (currentUser.getStatus().toString().equalsIgnoreCase("banned")) {
			showAlert("Failed to create the course!", "Your account is banned. You cannot create courses.",
					Alert.AlertType.ERROR);
			return;
		}
		String str_courseName = courseName.getText().trim();
		String str_price = price.getText().trim();
		String str_description = description.getText().trim();
		String str_technology;
		String str_language;
		String str_category;
		String str_level;
		if (str_courseName.isEmpty()) {
			showAlert("Validation Error", "Course name cannot be empty.", Alert.AlertType.WARNING);
			return;
		} else if (!IsValidCourseName(str_courseName)) {
			showAlert("Validation Error", "Course name contains invalid characters. Only letters, numbers, spaces, and common symbols (+, #, ., -, _, (), [], :, &, /) are allowed.", Alert.AlertType.WARNING);
			return;
		} else if (technology.getValue() == null) {
			showAlert("Validation Error", "Please select a programming language for your course.", Alert.AlertType.WARNING);
			return;
		} else if (language.getValue() == null) {
			showAlert("Validation Error", "Please select a language for your course.", Alert.AlertType.WARNING);
			return;
		} else if (category.getValue() == null) {
			showAlert("Validation Error", "Please select a category for your course.", Alert.AlertType.WARNING);
			return;
		} else if (level.getValue() == null) {
			showAlert("Validation Error", "Please select a level for your course.", Alert.AlertType.WARNING);
			return;
		} else if (str_price.isEmpty()) {
			showAlert("Validation Error", "Please enter the course's price.", Alert.AlertType.WARNING);
			return;
		} else if (str_description.isEmpty()) {
			showAlert("Validation Error", "Please enter the course's description.", Alert.AlertType.WARNING);
			return;
		} else if (selectedImageFile == null) {
			showAlert("Validation Error", "Please choose a thumbnail image for your course.", Alert.AlertType.WARNING);
			return;
		}

		str_technology = technology.getValue();
		str_language = language.getValue();
		str_level = convertDisplayToLevel(level.getValue());
		str_category = category.getValue();

		Float f_price = Float.parseFloat(str_price);
		String imagePath = saveImageToLocalDir(selectedImageFile);
		Courses course = new Courses();
		course.setCourseName(str_courseName);
		course.setCourseDescription(str_description);
		course.setTechnology(Technology.valueOf(str_technology));
		course.setLanguage(Language.valueOf(str_language));
		course.setLevel(Level.valueOf(str_level));
		course.setCategory(Category.valueOf(str_category.replace(' ', '_')));
		course.setUserID(userID);
		course.setPrice(f_price);
		course.setThumbnailURL(imagePath);
		course.setCreatedAt(LocalDateTime.now());
		course.setUpdatedAt(LocalDateTime.now());
		course.setApproved(false);

		courseService.AddCourse(course);
		SceneManager.switchSceneReloadWithData("My Course", "/frontend/view/instructorMainPage/instructorMainPage.fxml",
				null, null);
	}

	public void ReturnToMyCourse(ActionEvent e) throws IOException, SQLException {
		SceneManager.switchSceneReloadWithData("My Course", "/frontend/view/instructorMainPage/instructorMainPage.fxml", null, null);	}

	private static boolean IsValidCourseName(String name) {
		// Allow letters, numbers, spaces, and common programming-related special characters
		// This includes: +, #, ., -, _, (), [], :, and other commonly used characters in course names
		return name.matches("[\\p{L}\\p{N}\\p{Zs}+#.\\-_()\\[\\]:&/]+");
	}

	private String saveImageToLocalDir(File sourceFile) throws IOException {
		String fileName = UUID.randomUUID().toString() + "_" + sourceFile.getName();
		File destDir = new File("user_data/images");
		if (!destDir.exists()) {
			destDir.mkdirs();
		}

		Path destPath = Paths.get(destDir.getAbsolutePath(), fileName);
		Files.copy(sourceFile.toPath(), destPath, StandardCopyOption.REPLACE_EXISTING);

		return "user_data/images/" + fileName;
	}

	private List<String> getCategoryDisplayNames() {
		List<String> displayNames = new ArrayList<>();
		for (Category cat : Category.values()) {
			displayNames.add(cat.name().replace('_', ' '));
		}
		return displayNames;
	}

	@FXML
	private void handleSearch() {
		String searchKeyword = searchField.getText().trim();
		if (searchKeyword.isEmpty()) {
			goToExplorePage();
		} else {
			SceneManager.switchSceneReloadWithData("Instructor Explore",
					"/frontend/view/instructorExplorePage/InstructorExplorePage.fxml", (controller, keyword) -> {
						if (controller instanceof backend.controller.instructorExplorePage.instructorExplorePageController) {
							((backend.controller.instructorExplorePage.instructorExplorePageController) controller)
									.setSearchKeyword((String) keyword);
						}
					}, searchKeyword);
		}
	}

	@FXML
	private void goToExplorePage() {
		SceneManager.switchSceneReloadWithData("Instructor Explore",
				"/frontend/view/instructorExplorePage/InstructorExplorePage.fxml", null, null);
	}
	
	/**
	 * Show rating dialog for a specific course
	 */
	public void showCourseRatingDialog(int courseId, String courseName) {
		try {
			// Get course reviews and rating data
			ArrayList<CourseReview> reviews = courseReviewService.getCourseReviews(courseId);
			double averageRating = courseReviewService.getCourseAverageRating(courseId);
			int reviewCount = courseReviewService.getCourseReviewCount(courseId);
			
			// Create dialog
			Alert dialog = new Alert(Alert.AlertType.INFORMATION);
			dialog.setTitle("Course Ratings & Reviews");
			dialog.setHeaderText(courseName);
			
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
			showAlert("Error", "Failed to load course ratings.", Alert.AlertType.ERROR);
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
}