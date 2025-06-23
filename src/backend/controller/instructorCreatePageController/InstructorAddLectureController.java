package backend.controller.instructorCreatePageController;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.application.Platform;
import javafx.util.Duration;
import model.course.CourseSession;
import model.lecture.Lecture;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import backend.controller.course.LectureItemController;
import backend.controller.scene.SceneManager;
import backend.service.course.CourseService;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Popup;
import model.notification.UserNotification;
import model.user.Session;
import backend.repository.notification.NotificationRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class InstructorAddLectureController implements IInstructorAddLectureController, IOnChildRemovedListener {

	@FXML
	private ScrollPane mainScrollPane;
	@FXML
	private VBox mainContainer;
	@FXML
	private HBox addLectureBar;
	@FXML
	private Label addLectureArrow;
	@FXML
	private VBox createLectureForm;
	@FXML
	private VBox lecturesContainer;
	@FXML
	private Label goBack;
	@FXML
	private TextField lectureName;
	@FXML
	private TextField videoUrl;
	@FXML
	private TextField duration;
	@FXML
	private TextArea lectureDescription;
	@FXML
	private MediaView mediaView;
	@FXML
	private Label courseLabel;
	@FXML
	private Label myCourse;
	@FXML
	private Label createCourse;
	@FXML
	private Button profileButton;
	@FXML
	private Button notificationButton;

	private MediaPlayer mediaPlayer;
	private boolean isAddLectureFormVisible = false;

	private CourseService courseService = new CourseService();
	private List<Lecture> lectures = new ArrayList<>();
	List<LectureItemController> lectureControllers = new ArrayList<>();

	private ContextMenu profileMenu;
	private ImageView notificationIcon;
	private Circle notificationBadge;
	private Popup notificationPopup;
	private VBox notificationPopupContent;
	private NotificationRepository notificationRepository = new NotificationRepository();

	@FXML
	public void initialize() throws SQLException {
		goBack.setOnMouseClicked(event -> {
			this.disposeMediaPlayer();
			SceneManager.goBack();
		});
		courseLabel.setText(CourseSession.getCurrentCourse().getCourseName());
		loadExistingLectures();
		videoUrl.textProperty().addListener((obs, oldText, newText) -> {
			if (newText != null && !newText.trim().isEmpty()) {
				Platform.runLater(() -> loadVideo(newText));
			}
		});
		myCourse.setOnMouseClicked(event -> {
			try {
				this.ReturnToInstructorMainPage();
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		createCourse.setOnMouseClicked(event -> {
			try {
				this.ToCreateCoursePage();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
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
			showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load notifications: " + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			showAlert(Alert.AlertType.ERROR, "Error", "Failed to load notifications: " + e.getMessage());
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

	private void loadExistingLectures() throws SQLException {
		lecturesContainer.getChildren().clear();
		int courseId = CourseSession.getCurrentCourse().getCourseID();
		lectures = courseService.getLectureByCourseID(courseId);
		if (lectures != null && !lectures.isEmpty()) {
			for (Lecture lecture : lectures) {
				try {
					FXMLLoader loader = new FXMLLoader(
							getClass().getResource("/frontend/view/instructorCreatePage/LectureItem.fxml"));
					VBox lectureItem = loader.load();
					LectureItemController controller = loader.getController();
					controller.setLecture(lecture);
					controller.setOnChildRemovedListener(() -> {
						try {
							loadExistingLectures();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					});
					Separator sp = new Separator();

					lectureControllers.add(controller);
					lecturesContainer.getChildren().add(sp);
					lecturesContainer.getChildren().add(lectureItem);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
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

	@FXML
	private void toggleAddLectureForm() {
		isAddLectureFormVisible = !isAddLectureFormVisible;
		createLectureForm.setVisible(isAddLectureFormVisible);
		createLectureForm.setManaged(isAddLectureFormVisible);
		addLectureArrow.setText(isAddLectureFormVisible ? "▼" : "▲");

		if (isAddLectureFormVisible) {
			clearLectureForm();
		}
	}

	@FXML
	private void browseVideo() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Chọn tệp video");
		fileChooser.getExtensionFilters()
				.addAll(new FileChooser.ExtensionFilter("Video Files", "*.mp4", "*.avi", "*.mov", "*.wmv"));

		File selectedFile = fileChooser.showOpenDialog(mainScrollPane.getScene().getWindow());
		if (selectedFile != null) {
			videoUrl.setText(selectedFile.getAbsolutePath());
		}
	}

	private void loadVideo(String url) {
		try {
			disposeMediaPlayer();

			String source;
			if (url.startsWith("http") || url.startsWith("file:/")) {
				source = url;
			} else {
				source = new File(url).toURI().toString();
			}
			Platform.runLater(() -> {
				try {
					Media media = new Media(source);
					mediaPlayer = new MediaPlayer(media);
					mediaView.setMediaPlayer(mediaPlayer);

					mediaPlayer.setOnError(() -> {
						System.err.println("Media error: " + mediaPlayer.getError().getMessage());
					});

					mediaPlayer.setOnReady(() -> {
						Duration d = media.getDuration();
						int minutes = (int) Math.ceil(d.toMinutes());
						duration.setText(String.valueOf(minutes));
					});
				} catch (Exception e) {
					System.err.println("Lỗi khi tạo MediaPlayer: " + e.getMessage());
					e.printStackTrace();
				}
			});
		} catch (Exception e) {
			System.out.println("Error loading video: " + e.getMessage());
		}
	}

	@FXML
	private void addLecture() throws SQLException, IOException {
		if (!validateForm()) {
			return;
		}

		int courseId = CourseSession.getCurrentCourse().getCourseID();
		String name = lectureName.getText().trim();
		String url = videoUrl.getText().trim();
		short durationVal = (short) Math.ceil(Short.parseShort(duration.getText().trim()));
		String description = lectureDescription.getText().trim();

		Lecture lecture = new Lecture();
		lecture.setCourseID(courseId);
		lecture.setDuration(durationVal);
		lecture.setLectureDescription(description);
		lecture.setVideoURL(url);
		lecture.setLectureName(name);

		courseService.addLecture(lecture);
		this.ToAddLecturePage();
	}

	private void ToAddLecturePage() throws IOException {
		this.dispose();
		SceneManager.switchSceneReloadWithData("Add Lecture",
				"/frontend/view/instructorCreatePage/instructorAddLecturePage.fxml", null, null);
	}

	private void ToCreateCoursePage() throws IOException {
		this.dispose();
		SceneManager.switchScene("Create Course", "/frontend/view/instructorCreatePage/instructorCreatePage.fxml");
	}

	private void disposeMediaPlayer() {
		Platform.runLater(() -> {
			if (mediaPlayer != null) {
				try {
					mediaPlayer.stop();
					mediaPlayer.dispose();
					if (mediaView != null) {
						mediaView.setMediaPlayer(null);
					}
				} catch (Exception e) {
					System.err.println("Error disposing MediaPlayer: " + e.getMessage());
				}
				mediaPlayer = null;
			}
		});
	}

	@FXML
	private void cancelAddLecture() {
		clearLectureForm();
		toggleAddLectureForm();
	}

	private void clearLectureForm() {
		lectureName.clear();
		videoUrl.clear();
		duration.clear();
		lectureDescription.clear();
	}

	private boolean validateForm() {
		if (lectureName.getText().trim().isEmpty()) {
			showAlert(Alert.AlertType.ERROR, "Validation Error", "Lecture name is required");
			return false;
		}

		if (videoUrl.getText().trim().isEmpty()) {
			showAlert(Alert.AlertType.ERROR, "Validation Error", "Video URL is required");
			return false;
		}

		try {
			if (duration.getText().trim().isEmpty() || Integer.parseInt(duration.getText().trim()) <= 0) {
				showAlert(Alert.AlertType.ERROR, "Validation Error", "Valid duration is required");
				return false;
			}
		} catch (NumberFormatException e) {
			showAlert(Alert.AlertType.ERROR, "Validation Error", "Duration must be a number");
			return false;
		}
		return true;
	}

	private void ReturnToInstructorMainPage() throws IOException, SQLException {
		this.dispose();
		SceneManager.switchSceneReloadWithData("Instructor Main Page",
				"/frontend/view/instructorMainPage/instructorMainPage.fxml", null, null);
	}

	private void showAlert(Alert.AlertType type, String title, String content) {
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(content);
		alert.showAndWait();
	}

	private void dispose() {
		for (LectureItemController l : lectureControllers) {
			l.dispose();
		}
		disposeMediaPlayer();
	}

	@Override
	public void onChildRemoved() {
		// TODO Auto-generated method stub
	}
}