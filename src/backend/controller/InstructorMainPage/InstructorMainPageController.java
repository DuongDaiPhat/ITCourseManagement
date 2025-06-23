package backend.controller.InstructorMainPage;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Popup;
import model.course.Courses;
import model.user.Session;
import model.user.Users;
import model.notification.UserNotification;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ScrollPane;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.control.TextField;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import backend.controller.course.CourseItemController;
import backend.controller.scene.SceneManager;
import backend.service.course.CourseService;
import backend.service.user.UserService;
import backend.repository.notification.NotificationRepository;

public class InstructorMainPageController implements IInstructorMainPageController {
	private Users currentUser;
	@FXML
	private Label usernameLabel;

	@FXML
	private VBox courseListContainer;

	@FXML
	private Label emptyCourseLabel;

	@FXML
	private Label createCourse;

	@FXML
	private Label exploreLabel;

	@FXML
	private Button profileButton;

	@FXML
	private Button notificationButton;

	@FXML
	private TextField searchField;

	@FXML
	private Button searchButton;

	private ImageView notificationIcon;
	private Circle notificationBadge;
	private Popup notificationPopup;
	private VBox notificationPopupContent;

	private Stage stage;
	private Scene scene;
	private CourseService courseService;
	private UserService userService;
	private ContextMenu profileMenu;
	private NotificationRepository notificationRepository = new NotificationRepository();

	@FXML
	public void initialize() throws SQLException {
		createCourse.setOnMouseClicked(event -> {
			try {
				ToCreateCoursePage();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

		exploreLabel.setOnMouseClicked(event -> goToExplorePage());
		courseService = new CourseService();
		userService = new UserService();
		loadUser();

		setupProfileMenu();
		profileButton.setOnAction(event -> showProfileMenu());

		setupNotificationButton();
		notificationButton.setOnAction(event -> toggleNotificationPopup());

		loadNotifications();

		searchButton.setOnAction(event -> handleSearch());
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
			NotificationRepository.NotificationResult result = new NotificationRepository()
					.getNotificationsAndCount(userId);
			List<UserNotification> notifications = result.getNotifications();
			int unreadCount = result.getUnreadCount();

			Platform.runLater(() -> {
				// Update badge visibility - only show if there are unread notifications
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
			Platform.runLater(() -> showStyledAlert("Database Error", "Failed to load notifications: " + e.getMessage(),
					Alert.AlertType.ERROR));
		} catch (Exception e) {
			e.printStackTrace();
			Platform.runLater(() -> showStyledAlert("Error", "Failed to load notifications: " + e.getMessage(),
					Alert.AlertType.ERROR));
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

		// Show exact notification time instead of relative time
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");
		String timeText = notification.getNotifiedAt() != null ? notification.getNotifiedAt().format(formatter)
				: "Unknown time";
		Label timeLabel = new Label(timeText);
		timeLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #999999; -fx-padding: 5 0 0 0;");

		container.getChildren().addAll(header, contentLabel, timeLabel);

		container.setOnMouseClicked(e -> {
			new Thread(() -> {
				try {
					new NotificationRepository().markNotificationAsRead(notification.getNotificationID(),
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

	private String formatNotificationTime(LocalDateTime notificationTime) {
		if (notificationTime == null) {
			return "Just now";
		}

		LocalDateTime now = LocalDateTime.now();
		long seconds = java.time.Duration.between(notificationTime, now).getSeconds();

		if (seconds < 60) {
			return seconds + " seconds ago";
		} else if (seconds < 3600) {
			return (seconds / 60) + " minutes ago";
		} else if (seconds < 86400) {
			return (seconds / 3600) + " hours ago";
		} else {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
			return notificationTime.format(formatter);
		}
	}

	public void loadUser() throws SQLException {
		this.currentUser = Session.getCurrentUser();
		this.loadUserInfo();
		this.loadCourses();
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

	// Methods to handle menu item actions
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

	public void CreateCoursePage(ActionEvent e) throws IOException {
		this.ToCreateCoursePage();
	}

	private void ToCreateCoursePage() throws IOException {
		SceneManager.switchSceneReloadWithData("Create Course",
				"/frontend/view/instructorCreatePage/instructorCreatePage.fxml", null, null);
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

					CourseItemController controller = loader.getController();
					controller.setCourseData(course);

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
		Label messageLabel = new Label("Are you sure you want to delete the course: " + course.getCourseName() + "?");
		messageLabel.setStyle("-fx-text-fill: #004AAD; -fx-font-size: 16px; -fx-font-weight: bold;");
		messageLabel.setAlignment(Pos.CENTER);
		messageLabel.setWrapText(true);
		messageLabel.setMaxWidth(300);

		VBox contentBox = new VBox(15, messageLabel);
		contentBox.setAlignment(Pos.CENTER);

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

			int deleteResult = courseService.deleteCourse(course.getCourseID());
			if (deleteResult > 0) {
				showSuccessAlert("Course '" + course.getCourseName() + "' was deleted successfully!");

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

	@FXML
	private void goToExplorePage() {
		// Use reload to ensure fresh course data is displayed
		SceneManager.switchSceneReloadWithData("Instructor Explore",
				"/frontend/view/instructorExplorePage/InstructorExplorePage.fxml", null, null);
	}

	@FXML
	private void handleSearch() {
		String searchKeyword = searchField.getText().trim();
		if (searchKeyword.isEmpty()) {
			// Nếu không có từ khóa, chỉ chuyển sang trang Explore
			goToExplorePage();
		} else {
			// Chuyển sang trang Explore với từ khóa tìm kiếm - luôn reload để đảm bảo
			// search hoạt động
			SceneManager.switchSceneReloadWithData("Instructor Explore",
					"/frontend/view/instructorExplorePage/InstructorExplorePage.fxml", (controller, keyword) -> {
						if (controller instanceof backend.controller.instructorExplorePage.instructorExplorePageController) {
							((backend.controller.instructorExplorePage.instructorExplorePageController) controller)
									.setSearchKeyword((String) keyword);
						}
					}, searchKeyword);
		}
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