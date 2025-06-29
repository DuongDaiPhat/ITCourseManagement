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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Screen;
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

public class InstructorUpdatePageController implements IInstructorUpdatePageController {
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
	private Label pageTitle;
	@FXML
	private Label createCourse;
	@FXML
	private Button profileButton;
	@FXML
	private Button notificationButton;

	private UserService userService;
	private CourseService courseService;
	private int userID;
	private File selectedImageFile;
	private Courses currentCourse;
	private String originalImagePath;

	private ImageView notificationIcon;
	private Circle notificationBadge;
	private Popup notificationPopup;
	private VBox notificationPopupContent;
	private NotificationRepository notificationRepository = new NotificationRepository();

	private Stage stage;
	private Scene scene;
	private ContextMenu profileMenu;

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
			try {
				ReturnToInstructorMainPage();
			} catch (IOException | SQLException e) {
				e.printStackTrace();
			}
		});

		technology.setVisibleRowCount(5);
		technology.setEditable(false);

		language.setVisibleRowCount(5);
		language.setEditable(false);

		category.setVisibleRowCount(5);
		category.setEditable(false);

		level.setEditable(false);
		userService = new UserService();
		courseService = new CourseService();
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

			javafx.application.Platform.runLater(() -> {
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
					new NotificationRepository().markNotificationAsRead(notification.getNotificationID(),
							notification.getUserID());
					loadNotifications();
					javafx.application.Platform.runLater(() -> {
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

	public void setCourseData(Courses course) {
		if (course == null) {
			throw new IllegalArgumentException("Course cannot be null");
		}
		this.currentCourse = course;
		this.originalImagePath = course.getThumbnailURL();

		courseName.setText(course.getCourseName());
		technology.setValue(course.getTechnology().toString());
		language.setValue(course.getLanguage().toString());
		category.setValue(course.getCategory().toString().replace('_', ' '));

		String levelDisplay = course.getLevel().toString();
		level.setValue(convertLevelForDisplay(levelDisplay));

		price.setText(String.valueOf(course.getPrice()));
		description.setText(course.getCourseDescription());

		if (course.getThumbnailURL() != null && !course.getThumbnailURL().isEmpty()) {
			File imageFile = new File(course.getThumbnailURL());
			if (imageFile.exists()) {
				Image image = new Image(imageFile.toURI().toString());
				thumbnail.setImage(image);
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

	private String convertLevelForDisplay(String level) {
		if ("ALLLEVEL".equals(level)) {
			return "All Level";
		}
		return level;
	}

	private String convertDisplayToLevel(String displayLevel) {
		if ("All Level".equals(displayLevel)) {
			return "ALLLEVEL";
		}
		return displayLevel.toUpperCase();
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

	public void UpdateCourse(ActionEvent e) throws IOException, SQLException {
		String str_courseName = courseName.getText().trim();
		String str_price = price.getText().trim();
		String str_description = description.getText().trim();
		String str_technology;
		String str_language;
		String str_category;
		String str_level;

		if (str_courseName.isEmpty()) {
			System.out.println("Course name is empty");
			return;
		} else if (!IsValidCourseName(str_courseName)) {
			System.out.println("Invalid Course name");
			return;
		} else if (technology.getValue() == null) {
			System.out.println("You haven't chose programming language for your course yet");
			return;
		} else if (language.getValue() == null) {
			System.out.println("You haven't chose language for your course yet");
			return;
		} else if (category.getValue() == null) {
			System.out.println("You haven't chose category for your course yet");
			return;
		} else if (level.getValue() == null) {
			System.out.println("You haven't chose level for your course yet");
			return;
		} else if (str_price.isEmpty()) {
			System.out.println("Please enter the course's price");
			return;
		} else if (str_description.isEmpty()) {
			System.out.println("Please enter the course's description");
			return;
		}

		str_technology = technology.getValue();
		str_language = language.getValue();
		str_level = convertDisplayToLevel(level.getValue());
		str_category = category.getValue();

		Float f_price = Float.parseFloat(str_price);
		String imagePath = originalImagePath;

		if (selectedImageFile != null) {
			if (originalImagePath != null && !originalImagePath.isEmpty()) {
				try {
					File oldImage = new File(originalImagePath);
					if (oldImage.exists()) {
						oldImage.delete();
					}
				} catch (Exception ex) {
					System.err.println("Error deleting old image: " + ex.getMessage());
				}
			}

			imagePath = saveImageToLocalDir(selectedImageFile);
		}

		currentCourse.setCourseName(str_courseName);
		currentCourse.setCourseDescription(str_description);
		currentCourse.setTechnology(Technology.valueOf(str_technology));
		currentCourse.setLanguage(Language.valueOf(str_language));
		currentCourse.setLevel(Level.valueOf(str_level));
		currentCourse.setCategory(Category.valueOf(str_category.replace(' ', '_')));
		currentCourse.setPrice(f_price);
		currentCourse.setThumbnailURL(imagePath);
		currentCourse.setUpdatedAt(LocalDateTime.now());

		courseService.updateCourse(currentCourse);
		this.ReturnToInstructorMainPage();
	}

	public void ReturnToMyCourse(ActionEvent e) throws IOException, SQLException {
		this.ReturnToInstructorMainPage();
	}
	private static boolean IsValidCourseName(String name) {
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

	private void ReturnToInstructorMainPage() throws IOException, SQLException {
		SceneManager.switchSceneReloadWithData("My Course", "/frontend/view/instructorMainPage/instructorMainPage.fxml",
				null, null);
	}
}