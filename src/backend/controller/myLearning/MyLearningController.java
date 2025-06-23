package backend.controller.myLearning;

import backend.controller.scene.SceneManager;
import backend.service.course.CourseService;
import backend.service.user.UserService;
import backend.service.user.MyLearningService;
import backend.util.ImageCache;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Popup;
import model.course.Courses;
import model.user.MyLearning;
import model.user.Session;
import model.user.Users;
import model.user.CourseStatus;
import backend.repository.notification.NotificationRepository;
import model.notification.UserNotification;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class MyLearningController implements Initializable {

	@FXML
	private Label homeLabel;
	@FXML
	private Label categoryLabel;
	@FXML
	private Label myLearningLabel;
	@FXML
	private TextField searchField;
	@FXML
	private Button searchButton;
	@FXML
	private Button cartButton;
	@FXML
	private Button wishlistButton;
	@FXML
	private Button notificationButton;
	@FXML
	private Button profileButton;

	// Icon ImageViews
	@FXML
	private ImageView searchIcon;
	@FXML
	private ImageView cartIcon;
	@FXML
	private ImageView wishlistIcon;
	@FXML
	private ImageView profileIcon;

	@FXML
	private ScrollPane mainScrollPane;
	@FXML
	private Label coursesCountLabel;
	@FXML
	private VBox learningCoursesContainer;

	private ImageView notificationIcon;
	private Circle notificationBadge;
	private Popup notificationPopup;
	private VBox notificationPopupContent;
	private NotificationRepository notificationRepository = new NotificationRepository();

	private CourseService courseService;
	private UserService userService;
	private MyLearningService learningService;
	private ContextMenu profileMenu;

	private List<LearningCourseData> learningCourses;
	private Users currentUser;

	private static class LearningCourseData {
		private Courses course;
		private MyLearning learningRecord;
		private VBox itemContainer;
		private Button markCompleteButton;
		private Label completedBadge;

		public LearningCourseData(Courses course, MyLearning learningRecord, VBox itemContainer,
				Button markCompleteButton, Label completedBadge) {
			this.course = course;
			this.learningRecord = learningRecord;
			this.itemContainer = itemContainer;
			this.markCompleteButton = markCompleteButton;
			this.completedBadge = completedBadge;
		}

		public Courses getCourse() {
			return course;
		}

		public MyLearning getLearningRecord() {
			return learningRecord;
		}

		public VBox getItemContainer() {
			return itemContainer;
		}

		public Button getMarkCompleteButton() {
			return markCompleteButton;
		}

		public Label getCompletedBadge() {
			return completedBadge;
		}

		public void setLearningRecord(MyLearning learningRecord) {
			this.learningRecord = learningRecord;
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initializeServices();
		setupIcons();
		setupNotificationButton();

		learningCourses = new ArrayList<>();
		currentUser = Session.getCurrentUser();

		Platform.runLater(() -> {
			setupEventHandlers();
			setupProfileMenu();
			loadLearningCourses();
			loadNotifications();
		});
	}

	private void initializeServices() {
		courseService = new CourseService();
		userService = new UserService();
		learningService = new MyLearningService();
	}

	private void setupIcons() {
		// Load icons with error handling using file: protocol - same as StudentMainPage
		loadIconSafely(searchIcon, "file:resources/images/main_page/icon/WhiteMagnifier.png");
		loadIconSafely(cartIcon, "file:resources/images/main_page/icon/MyCart.png");
		loadIconSafely(wishlistIcon, "file:resources/images/main_page/icon/MyWishList.png");
		loadIconSafely(profileIcon, "file:resources/images/main_page/icon/MyProfile.png");
	}

	private void loadIconSafely(ImageView imageView, String iconPath) {
		try {
			System.out.println("Attempting to load icon from: " + iconPath);
			Image icon = ImageCache.loadImage(iconPath);
			if (icon != null) {
				imageView.setImage(icon);
				System.out.println("Successfully loaded icon: " + iconPath);
			} else {
				System.out.println("Icon not found or error loading: " + iconPath);
			}
		} catch (Exception e) {
			System.out.println("Exception loading icon " + iconPath + ": " + e.getMessage());
		}
	}

	private void setupNotificationButton() {
		// Create notification icon
		notificationIcon = new ImageView();
		notificationIcon.setFitHeight(20);
		notificationIcon.setFitWidth(20);
		loadIconSafely(notificationIcon, "file:resources/images/main_page/icon/Notification.png");

		// Create notification badge
		notificationBadge = new Circle(5);
		notificationBadge.setFill(Color.TRANSPARENT); // Hidden by default
		notificationBadge.setStroke(Color.TRANSPARENT);
		notificationBadge.setTranslateX(8);
		notificationBadge.setTranslateY(-8);
		notificationBadge.getStyleClass().add("notification-badge");

		// Combine icon and badge
		StackPane stackPane = new StackPane();
		stackPane.getChildren().addAll(notificationIcon, notificationBadge);

		// Assign to notification button
		notificationButton.setGraphic(stackPane);
		notificationButton.getStyleClass().add("notification-button");

		// Create notification popup
		notificationPopup = new Popup();
		notificationPopup.setAutoHide(true); // Auto-hide when clicking outside

		// Popup content
		notificationPopupContent = new VBox();
		notificationPopupContent.setStyle(
				"-fx-background-color: white; -fx-border-color: #cccccc; -fx-border-width: 1px; -fx-padding: 10px;");
		notificationPopupContent.setPrefWidth(300);
		notificationPopupContent.setMaxHeight(400);

		// Add scroll pane
		ScrollPane scrollPane = new ScrollPane(notificationPopupContent);
		scrollPane.setFitToWidth(true);
		scrollPane.setStyle("-fx-background: white; -fx-background-color: white;");

		// Popup title
		Label titleLabel = new Label("Notifications");
		titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 0 0 10 0;");

		// Main container
		VBox popupContainer = new VBox(titleLabel, scrollPane);
		popupContainer.setStyle("-fx-background-color: white; -fx-padding: 10px;");
		popupContainer.setPrefWidth(300);

		notificationPopup.getContent().add(popupContainer);

		// Set action handler for notification button
		notificationButton.setOnAction(event -> toggleNotificationPopup());
	}

	private void toggleNotificationPopup() {
		if (notificationPopup.isShowing()) {
			notificationPopup.hide();
		} else {
			loadNotifications(); // Reload notifications each time popup is opened
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
				// Update badge - show if there are unread notifications
				if (unreadCount > 0) {
					notificationBadge.setFill(Color.web("#ff4757")); // Red color
					notificationBadge.setStroke(Color.web("#ff4757"));
				} else {
					notificationBadge.setFill(Color.TRANSPARENT);
					notificationBadge.setStroke(Color.TRANSPARENT);
				}

				// Clear old content
				notificationPopupContent.getChildren().clear();

				// Show "No notifications" message if list is empty
				if (notifications.isEmpty()) {
					Label emptyLabel = new Label("No new notifications");
					emptyLabel.setStyle("-fx-text-fill: #666666; -fx-padding: 10px;");
					notificationPopupContent.getChildren().add(emptyLabel);
				} else {
					// Add each notification to popup
					for (UserNotification notification : notifications) {
						VBox notificationItem = createNotificationItem(notification);
						notificationPopupContent.getChildren().add(notificationItem);
					}
				}

				// Add "View All" button
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
			Platform.runLater(() -> showAlert("Database Error", "Failed to load notifications: " + e.getMessage()));
		} catch (Exception e) {
			e.printStackTrace();
			Platform.runLater(() -> showAlert("Error", "Failed to load notifications: " + e.getMessage()));
		}
	}

	private VBox createNotificationItem(UserNotification notification) {
		VBox container = new VBox();
		container.setStyle(
				"-fx-background-color: #f9f9f9; -fx-border-color: #eeeeee; -fx-border-width: 0 0 1 0; -fx-padding: 10px;");
		container.setSpacing(5);

		// Header (icon + title + category)
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

		// Notification content
		Label contentLabel = new Label(notification.getContent());
		contentLabel.setStyle("-fx-font-size: 12px; -fx-wrap-text: true;");
		contentLabel.setMaxWidth(280);

		// Time
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");
		String timeText = notification.getNotifiedAt() != null ? notification.getNotifiedAt().format(formatter)
				: "Unknown time";
		Label timeLabel = new Label(timeText);
		timeLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #999999; -fx-padding: 5 0 0 0;");

		container.getChildren().addAll(header, contentLabel, timeLabel);

		// Handle click on notification
		container.setOnMouseClicked(e -> {
			new Thread(() -> {
				try {
					// Mark as read
					notificationRepository.markNotificationAsRead(notification.getNotificationID(),
							notification.getUserID());

					// Reload notifications
					loadNotifications();

					// Open detail page
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

	private void setupEventHandlers() {
		// Navigation handlers
		homeLabel.setOnMouseClicked(event -> navigateToMainPage());
		categoryLabel.setOnMouseClicked(event -> navigateToExplorePage());

		// My Learning is current page - no action needed

		cartButton.setOnAction(event -> navigateToCart());
		wishlistButton.setOnAction(event -> navigateToWishlist());
	}

	private void setupProfileMenu() {
		profileMenu = new ContextMenu();

		MenuItem profileItem = new MenuItem("View Profile");
		profileItem.setOnAction(event -> navigateToProfile());

		MenuItem settingsItem = new MenuItem("Settings");
		settingsItem.setOnAction(event -> navigateToSettings());

		MenuItem logoutItem = new MenuItem("Logout");
		logoutItem.setOnAction(event -> handleLogout());

		profileMenu.getItems().addAll(profileItem, settingsItem, new SeparatorMenuItem(), logoutItem);
	}

	@FXML
	private void showProfileMenu() {
		// Calculate position to show menu below the profile button
		double x = profileButton.localToScreen(profileButton.getBoundsInLocal()).getMinX();
		double y = profileButton.localToScreen(profileButton.getBoundsInLocal()).getMaxY();

		// Show menu at calculated position
		profileMenu.show(profileButton.getScene().getWindow(), x, y);
	}

	@FXML
	private void handleSearch() {
		String searchTerm = searchField.getText().trim();
		if (!searchTerm.isEmpty()) {
			navigateToSearchResults(searchTerm);
		}
	}

	private void loadLearningCourses() {
		if (currentUser == null) {
			showEmptyLearningMessage();
			return;
		}

		try {
			// Get all learning records for current user
			List<MyLearning> learningRecords = learningService.getUserLearningItems(currentUser.getUserID());

			if (learningRecords == null || learningRecords.isEmpty()) {
				showEmptyLearningMessage();
				return;
			}

			// Load course details for each learning record
			learningCourses.clear();
			learningCoursesContainer.getChildren().clear();

			List<LearningCourseData> incompleteCourses = new ArrayList<>();
			List<LearningCourseData> completedCourses = new ArrayList<>();

			for (MyLearning learningRecord : learningRecords) {
				try {
					Courses course = courseService.GetCourseByID(learningRecord.getCourseID());
					if (course != null) {
						VBox courseItem = createLearningCourseItem(course, learningRecord);
						LearningCourseData courseData = new LearningCourseData(course, learningRecord, courseItem, null,
								null);

						if (learningRecord.getCourseStatus() == CourseStatus.FINISHED) {
							completedCourses.add(courseData);
						} else {
							incompleteCourses.add(courseData);
						}

						learningCourses.add(courseData);
					}
				} catch (Exception e) {
					System.err.println("Error loading course details for ID " + learningRecord.getCourseID() + ": "
							+ e.getMessage());
				}
			}

			// Add incomplete courses first, then completed courses
			for (LearningCourseData courseData : incompleteCourses) {
				learningCoursesContainer.getChildren().add(courseData.getItemContainer());
			}
			for (LearningCourseData courseData : completedCourses) {
				learningCoursesContainer.getChildren().add(courseData.getItemContainer());
			}

			// Update courses count
			updateCoursesCount(learningRecords.size());

		} catch (Exception e) {
			System.err.println("Error loading learning courses: " + e.getMessage());
			e.printStackTrace();
			showAlert("Error", "Failed to load your learning courses. Please try again.");
		}
	}

	private VBox createLearningCourseItem(Courses course, MyLearning learningRecord) {
		VBox itemContainer = new VBox();
		itemContainer.getStyleClass().add("learning-course-item");
		if (learningRecord.getCourseStatus() == CourseStatus.FINISHED) {
			itemContainer.getStyleClass().add("learning-course-item-completed");
		}

		HBox mainContent = new HBox();
		mainContent.setSpacing(20);
		mainContent.setAlignment(Pos.CENTER_LEFT);

		// Course Image
		ImageView courseImage = new ImageView();
		courseImage.getStyleClass().add("learning-course-image");
		courseImage.setFitWidth(150);
		courseImage.setFitHeight(100);
		courseImage.setPreserveRatio(true);

		loadCourseImage(courseImage, course);

		// Course Details
		VBox courseDetails = new VBox();
		courseDetails.getStyleClass().add("learning-course-content");
		HBox.setHgrow(courseDetails, Priority.ALWAYS);
		Label titleLabel = new Label(course.getCourseName());
		titleLabel.getStyleClass().add("learning-course-title");

		Label instructorLabel = new Label("By " + getInstructorName(course.getUserID()));
		instructorLabel.getStyleClass().add("learning-course-instructor");

		Label descriptionLabel = new Label(course.getCourseDescription());
		descriptionLabel.getStyleClass().add("learning-course-description");

		// Course attributes section
		HBox attributesBox = new HBox();
		attributesBox.setSpacing(15);
		attributesBox.getStyleClass().add("learning-course-attributes");

		Label categoryLabel = new Label("Category: "
				+ (course.getCategory() != null ? course.getCategory().toString().replace("_", " ") : "N/A"));
		categoryLabel.getStyleClass().add("learning-course-attribute");

		Label levelLabel = new Label("Level: " + (course.getLevel() != null ? course.getLevel().toString() : "N/A"));
		levelLabel.getStyleClass().add("learning-course-attribute");

		Label languageLabel = new Label(
				"Language: " + (course.getLanguage() != null ? course.getLanguage().toString() : "N/A"));
		languageLabel.getStyleClass().add("learning-course-attribute");

		Label technologyLabel = new Label(
				"Technology: " + (course.getTechnology() != null ? course.getTechnology().toString() : "N/A"));
		technologyLabel.getStyleClass().add("learning-course-attribute");

		attributesBox.getChildren().addAll(categoryLabel, levelLabel, languageLabel, technologyLabel);

		courseDetails.getChildren().addAll(titleLabel, instructorLabel, descriptionLabel, attributesBox);
		// Action Section
		VBox actionSection = new VBox();
		actionSection.getStyleClass().add("learning-course-actions");

		if (learningRecord.getCourseStatus() == CourseStatus.FINISHED) {
			Label completedBadge = new Label("✓ Completed");
			completedBadge.getStyleClass().add("completed-badge");

			Button toggleCompleteButton = new Button("Mark as In Progress");
			toggleCompleteButton.getStyleClass().add("toggle-complete-button");
			toggleCompleteButton.setOnAction(event -> toggleCourseCompletion(course, learningRecord));

			actionSection.getChildren().addAll(completedBadge, toggleCompleteButton);
		} else {
			Button markCompleteButton = new Button("Mark as Completed");
			markCompleteButton.getStyleClass().add("mark-complete-button");
			markCompleteButton.setOnAction(event -> toggleCourseCompletion(course, learningRecord));
			actionSection.getChildren().add(markCompleteButton);
		}

		// Last accessed info
		if (learningRecord.getLastAccessedAt() != null) {
			Label lastAccessedLabel = new Label("Last accessed: "
					+ learningRecord.getLastAccessedAt().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
			lastAccessedLabel.getStyleClass().add("last-accessed-label");
			actionSection.getChildren().add(lastAccessedLabel);
		}

		mainContent.getChildren().addAll(courseImage, courseDetails, actionSection);
		itemContainer.getChildren().add(mainContent);

		// Add click handler for navigation to course detail
		itemContainer.setOnMouseClicked(event -> {
			if (event.getClickCount() == 1 && event.getTarget() != actionSection) {
				navigateToCourseDetail(course, true); // true = purchased
			}
		});

		return itemContainer;
	}

	private void loadCourseImage(ImageView imageView, Courses course) {
		try {
			if (course.getThumbnailURL() != null && !course.getThumbnailURL().isEmpty()) {
				String thumbnailPath = course.getThumbnailURL();

				if (!thumbnailPath.startsWith("file:") && !thumbnailPath.startsWith("http")) {
					thumbnailPath = "file:" + thumbnailPath;
				}

				Image image = ImageCache.loadImage(thumbnailPath);
				if (image != null) {
					imageView.setImage(image);
				} else {
					String altPath = "file:resources/images/"
							+ course.getThumbnailURL().replace("user_data/images/", "");
					Image altImage = ImageCache.loadImage(altPath);
					if (altImage != null) {
						imageView.setImage(altImage);
					} else {
						imageView.setImage(ImageCache.loadImage("/images/default_course.png"));
					}
				}
			} else {
				imageView.setImage(ImageCache.loadImage("/images/default_course.png"));
			}
		} catch (Exception e) {
			imageView.setImage(ImageCache.loadImage("/images/default_course.png"));
		}
	}

	private void toggleCourseCompletion(Courses course, MyLearning learningRecord) {
		try {
			// Toggle status between FINISHED and IN_PROGRESS
			CourseStatus newStatus = (learningRecord.getCourseStatus() == CourseStatus.FINISHED)
					? CourseStatus.IN_PROGRESS
					: CourseStatus.FINISHED;

			// Update learning record status
			learningRecord.setCourseStatus(newStatus);
			learningRecord.setLastAccessedAt(LocalDateTime.now());

			boolean success = learningService.updateCourseStatus(Session.getCurrentUser().getUserID(),
					learningRecord.getCourseID(), newStatus);

			if (success) {
				// Refresh the display
				loadLearningCourses();

				String message = (newStatus == CourseStatus.FINISHED) ? "Course marked as completed!"
						: "Course marked as in progress!";
				showAlert("Success", message);
			} else {
				showAlert("Error", "Failed to update course status. Please try again.");
			}
		} catch (Exception e) {
			System.err.println("Error toggling course completion: " + e.getMessage());
			e.printStackTrace();
			showAlert("Error", "An error occurred while updating course status.");
		}
	}

	private void showEmptyLearningMessage() {
		learningCoursesContainer.getChildren().clear();

		VBox emptyMessage = new VBox();
		emptyMessage.getStyleClass().add("empty-learning-message");

		Label iconLabel = new Label("📚");
		iconLabel.getStyleClass().add("empty-learning-icon");

		Label messageLabel = new Label("You haven't enrolled in any courses yet.");
		Label subMessageLabel = new Label("Start learning by browsing our course catalog!");

		Button browseCourses = new Button("Browse Courses");
		browseCourses.getStyleClass().add("browse-courses-button");
		browseCourses.setOnAction(event -> navigateToExplorePage());

		emptyMessage.getChildren().addAll(iconLabel, messageLabel, subMessageLabel, browseCourses);
		learningCoursesContainer.getChildren().add(emptyMessage);

		updateCoursesCount(0);
	}

	private void updateCoursesCount(int count) {
		String text = count == 1 ? "1 course enrolled" : count + " courses enrolled";
		coursesCountLabel.setText(text);
	}

	private String getInstructorName(int userId) {
		try {
			Users instructor = userService.GetUserByID(userId);
			return instructor != null ? instructor.getUserFirstName() + " " + instructor.getUserLastName()
					: "Unknown Instructor";
		} catch (Exception e) {
			return "Unknown Instructor";
		}
	}

	// Navigation methods
	private void navigateToMainPage() {
		SceneManager.switchScene("Student Main Page", "/frontend/view/studentMainPage/StudentMainPage.fxml");
	}

	private void navigateToExplorePage() {
		SceneManager.switchScene("Student Explore Page", "/frontend/view/studentExplorePage/StudentExplorePage.fxml");
	}

	private void navigateToCart() {
		SceneManager.switchSceneWithRefresh("Student Cart Page", "/frontend/view/studentCart/StudentCart.fxml");
	}

	private void navigateToWishlist() {
		try {
			SceneManager.switchSceneWithRefresh("My Wishlist", "/frontend/view/myWishList/MyWishList.fxml");
		} catch (Exception e) {
			System.err.println("Error navigating to wishlist: " + e.getMessage());
			showAlert("Error", "Failed to open wishlist page. Please try again.");
		}
	}

	private void navigateToCourseDetail(Courses course, boolean isPurchased) {
		try {
			// Set current course in session with purchased state
			Session.setCurrentCourse(course, isPurchased);

			SceneManager.switchSceneWithRefresh("Student Course Detail Page",
					"/frontend/view/studentCourseDetailPage/StudentCourseDetailPage.fxml");
		} catch (Exception e) {
			System.err.println("Error navigating to course detail: " + e.getMessage());
			e.printStackTrace();
			showAlert("Navigation Error", "Could not navigate to course detail page. Please try again.");
		}
	}

	private void navigateToSearchResults(String searchTerm) {
		// TODO: Implement search navigation
		showAlert("Search", "Searching for: " + searchTerm);
	}

	private void navigateToProfile() {
		try {
			SceneManager.switchScene("User Profile", "/frontend/view/userProfile/UserProfile.fxml");
		} catch (Exception e) {
			showAlert("Navigation Error", "Could not navigate to the profile page. Please try again.");
		}
	}

	private void navigateToSettings() {
		// TODO: Implement settings navigation
		showAlert("Coming Soon", "Settings page is under development.");
	}

	private void handleLogout() {
		Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
		confirmation.setTitle("Logout");
		confirmation.setHeaderText("Confirm Logout");
		confirmation.setContentText("Are you sure you want to logout?");

		Optional<ButtonType> result = confirmation.showAndWait();
		if (result.isPresent() && result.get() == ButtonType.OK) {
			Session.clearCurrentUser();
			SceneManager.switchScene("Login", "/frontend/view/login/login.fxml");
		}
	}

	private void showAlert(String title, String message) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

	/**
	 * Refresh method called by SceneManager to ensure fresh learning data
	 */
	public void refreshPageData() {
		System.out.println("Refreshing MyLearning page data...");
		Platform.runLater(() -> {
			learningCourses.clear();
			learningCoursesContainer.getChildren().clear();
			loadLearningCourses();
		});
	}
}
