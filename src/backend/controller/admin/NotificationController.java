package backend.controller.admin;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

import backend.repository.notification.NotificationRepository;
import backend.repository.notification.NotificationTemplateRepository;
import backend.service.user.AdminService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import model.notification.NotificationTemplate;
import model.user.Session;
import model.user.Users;

public class NotificationController implements Initializable {

	@FXML
	private TextField notificationTitleField;

	@FXML
	private TextArea notificationContentArea;

	@FXML
	private ComboBox<String> notificationTypeComboBox;

	@FXML
	private ComboBox<String> targetRoleComboBox;

	@FXML
	private ComboBox<String> notificationCategoryComboBox;

	@FXML
	private ComboBox<String> templateComboBox;

	@FXML
	private TextField userIdField;

	@FXML
	private Button sendButton;

	@FXML
	private Button backButton;

	@FXML
	private Button saveTemplateButton;

	@FXML
	private Button deleteTemplateButton;

	@FXML
	private Button historyButton;

	private NotificationRepository notificationRepository = new NotificationRepository();
	private NotificationTemplateRepository templateRepository = new NotificationTemplateRepository();
	private AdminService adminService = new AdminService();
	private Map<String, NotificationTemplate> templateMap = new HashMap<>();
	private AdminInstructorController adminInstructorController;
	private AdminMainController adminMainController;
	private Integer targetUserId;
	private Stage currentStage;
	private String sourceScreen;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ObservableList<String> types = FXCollections.observableArrayList("SYSTEM", "STUDENT", "INSTRUCTOR");
		notificationTypeComboBox.setItems(types);
		notificationTypeComboBox.getSelectionModel().selectFirst();

		ObservableList<String> roles = FXCollections.observableArrayList("All Users", "Students Only",
				"Instructors Only");
		targetRoleComboBox.setItems(roles);
		targetRoleComboBox.getSelectionModel().selectFirst();

		ObservableList<String> categories = FXCollections.observableArrayList("System Maintenance",
				"Event/Campaign Announcement", "Course Promotion", "New Course Notification", "Behavior Warning",
				"Policy/System Update", "Course Approved", "Course Rejected", "System Upgrade Notice");
		notificationCategoryComboBox.setItems(categories);
		notificationCategoryComboBox.getSelectionModel().selectFirst();

		loadTemplates();

		notificationTypeComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
			updateCategoriesForType(newVal);
			updateTemplatesForType(newVal);
		});

		templateComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
			if (newVal != null && !newVal.isEmpty()) {
				NotificationTemplate template = templateMap.get(newVal);
				if (template != null) {
					notificationTitleField.setText(template.getTitle());
					notificationContentArea.setText(template.getContent());
					notificationTypeComboBox.setValue(template.getNotificationType());
					notificationCategoryComboBox.setValue(template.getCategory());
				}
			}
		});
	}

	public void setAdminInstructorController(AdminInstructorController controller) {
		this.adminInstructorController = controller;
		this.sourceScreen = "instructor";
	}

	public void setAdminMainController(AdminMainController controller) {
		this.adminMainController = controller;
		this.sourceScreen = "main";
	}

	public void setCurrentStage(Stage stage) {
		this.currentStage = stage;
	}

	public void setTargetUserId(int userId) {
		this.targetUserId = userId;
		this.userIdField.setText(String.valueOf(userId));
		this.targetRoleComboBox.setValue("Students Only");
		this.notificationTypeComboBox.setValue("STUDENT");
	}

	public void setNotificationCategory(String category) {
		this.notificationCategoryComboBox.setValue(category);
	}

	public void setNotificationTitle(String title) {
		this.notificationTitleField.setText(title);
	}

	private void loadTemplates() {
		try {
			templateMap.clear();
			ObservableList<String> templateNames = FXCollections.observableArrayList();
			templateNames.add("-- Select Template --");

			for (NotificationTemplate template : templateRepository.getAllTemplates()) {
				String displayName = template.getTitle() + " (" + template.getNotificationType() + ")";
				templateMap.put(displayName, template);
				templateNames.add(displayName);
			}

			templateComboBox.setItems(templateNames);
			templateComboBox.getSelectionModel().selectFirst();
		} catch (SQLException e) {
			showAlert("Error", "Failed to load templates: " + e.getMessage(), Alert.AlertType.ERROR);
		}
	}

	private void updateTemplatesForType(String type) {
		try {
			templateMap.clear();
			ObservableList<String> templateNames = FXCollections.observableArrayList();
			templateNames.add("-- Select Template --");

			for (NotificationTemplate template : templateRepository.getTemplatesByType(type)) {
				String displayName = template.getTitle() + " (" + template.getCategory() + ")";
				templateMap.put(displayName, template);
				templateNames.add(displayName);
			}

			templateComboBox.setItems(templateNames);
			templateComboBox.getSelectionModel().selectFirst();
		} catch (SQLException e) {
			showAlert("Error", "Failed to load templates: " + e.getMessage(), Alert.AlertType.ERROR);
		}
	}

	private void updateCategoriesForType(String type) {
		ObservableList<String> categories = FXCollections.observableArrayList();

		if ("SYSTEM".equals(type)) {
			categories.addAll("System Maintenance", "Event/Campaign Announcement");
		} else if ("STUDENT".equals(type)) {
			categories.addAll("Course Promotion", "New Course Notification", "Behavior Warning",
					"Policy/System Update");
		} else if ("INSTRUCTOR".equals(type)) {
			categories.addAll("Course Approved", "Course Rejected", "Behavior Warning", "System Upgrade Notice");
		}

		notificationCategoryComboBox.setItems(categories);
		notificationCategoryComboBox.getSelectionModel().selectFirst();
	}

	@FXML
	private void handleSendNotification(ActionEvent event) {
		String title = notificationTitleField.getText().trim();
		String content = notificationContentArea.getText().trim();
		String type = notificationTypeComboBox.getValue();
		String targetRole = targetRoleComboBox.getValue();
		String category = notificationCategoryComboBox.getValue();
		String userIdStr = userIdField.getText().trim();

		if (title.isEmpty() || content.isEmpty()) {
			showAlert("Error", "Please fill in all fields", Alert.AlertType.ERROR);
			return;
		}

		try {
			Users currentUser = Session.getCurrentUser();
			if (currentUser != null) {
				String icon = getIconForNotificationCategory(category);
				int targetRoleId = getTargetRoleId(targetRole);
				Integer userId = userIdStr.isEmpty() ? null : Integer.parseInt(userIdStr);

				boolean success = notificationRepository.createNotification(title, content, currentUser.getUserID(),
						type, targetRoleId, userId, icon, category);

				if (success) {
					showAlert("Success", "Notification sent successfully", Alert.AlertType.INFORMATION);

					notificationTitleField.clear();
					notificationContentArea.clear();
					userIdField.clear();

					if ("instructor".equals(sourceScreen) && adminInstructorController != null) {
						adminInstructorController.completePendingAction();
					} else if ("main".equals(sourceScreen) && adminMainController != null) {
						adminMainController.completePendingAction();
					}

					if (currentStage != null) {
						currentStage.close();
					} else {
						Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
						stage.close();
					}
				} else {
					showAlert("Error", "Failed to send notification", Alert.AlertType.ERROR);
				}
			}
		} catch (NumberFormatException e) {
			showAlert("Error", "User ID must be a number", Alert.AlertType.ERROR);
		} catch (Exception e) {
			showAlert("Error", "This ID does not exist", Alert.AlertType.ERROR);
			e.printStackTrace();
		}
	}

	@FXML
	private void handleSaveTemplate(ActionEvent event) {
		String title = notificationTitleField.getText().trim();
		String content = notificationContentArea.getText().trim();
		String type = notificationTypeComboBox.getValue();
		String category = notificationCategoryComboBox.getValue();

		if (title.isEmpty() || content.isEmpty()) {
			showAlert("Error", "Title and content cannot be empty", Alert.AlertType.ERROR);
			return;
		}

		try {
			NotificationTemplate template = new NotificationTemplate();
			template.setTitle(title);
			template.setContent(content);
			template.setNotificationType(type);
			template.setCategory(category);

			boolean success = templateRepository.saveTemplate(template);
			if (success) {
				showAlert("Success", "Template saved successfully", Alert.AlertType.INFORMATION);
				loadTemplates();
			} else {
				showAlert("Error", "Failed to save template", Alert.AlertType.ERROR);
			}
		} catch (SQLException e) {
			showAlert("Error", "Error saving template: " + e.getMessage(), Alert.AlertType.ERROR);
			e.printStackTrace();
		}
	}

	@FXML
	private void handleDeleteTemplate(ActionEvent event) {
		String selectedTemplate = templateComboBox.getValue();
		if (selectedTemplate == null || selectedTemplate.equals("-- Select Template --")) {
			showAlert("Error", "Please select a template to delete", Alert.AlertType.ERROR);
			return;
		}

		NotificationTemplate template = templateMap.get(selectedTemplate);
		if (template == null) {
			showAlert("Error", "Selected template not found", Alert.AlertType.ERROR);
			return;
		}

		Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
		confirmAlert.setTitle("Confirm Deletion");
		confirmAlert.setHeaderText(null);
		confirmAlert.setContentText("Are you sure you want to delete this template?");

		Optional<ButtonType> result = confirmAlert.showAndWait();
		if (result.isPresent() && result.get() == ButtonType.OK) {
			try {
				boolean success = templateRepository.deleteTemplate(template.getId());
				if (success) {
					showAlert("Success", "Template deleted successfully", Alert.AlertType.INFORMATION);
					loadTemplates();
					notificationTitleField.clear();
					notificationContentArea.clear();
				} else {
					showAlert("Error", "Failed to delete template", Alert.AlertType.ERROR);
				}
			} catch (SQLException e) {
				showAlert("Error", "Error deleting template: " + e.getMessage(), Alert.AlertType.ERROR);
				e.printStackTrace();
			}
		}
	}

	@FXML
	private void handleViewHistory(ActionEvent event) {
		try {
			Dialog<Void> dialog = new Dialog<>();
			dialog.setTitle("Notification History");
			dialog.setHeaderText("Previously sent notifications");
			dialog.getDialogPane().getStylesheets()
					.add(getClass().getResource("/frontend/view/admin/adminMainPage.css").toExternalForm());

			ScrollPane scrollPane = new ScrollPane();
			scrollPane.setFitToWidth(true);
			scrollPane.setPrefHeight(500);
			scrollPane.setStyle("-fx-background: white; -fx-background-color: white;");

			VBox dialogContent = new VBox(10);
			dialogContent.setPadding(new javafx.geometry.Insets(10));
			dialogContent.setStyle("-fx-background-color: white;");

			List<Map<String, Object>> history = notificationRepository.getNotificationHistory();
			for (Map<String, Object> entry : history) {
				VBox notificationBox = new VBox(5);
				notificationBox.setStyle(
						"-fx-background-color: #f5f5f5; -fx-padding: 15; -fx-border-color: #25b6aa; -fx-border-width: 1; -fx-border-radius: 5;");

				Label titleLabel = new Label("Title: " + entry.get("title"));
				titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #00363D;");

				Label dateLabel = new Label("Sent: " + entry.get("date"));
				dateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666666;");

				Label typeLabel = new Label("Type: " + entry.get("type"));
				typeLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #00363D;");

				Label targetLabel = new Label("Target: " + entry.get("target"));
				targetLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #00363D;");

				TextArea contentArea = new TextArea(entry.get("content").toString());
				contentArea.setEditable(false);
				contentArea.setWrapText(true);
				contentArea.setMaxHeight(100);
				contentArea.setStyle(
						"-fx-font-size: 12px; -fx-background-color: transparent; -fx-border-color: transparent; -fx-text-fill: #00363D;");

				notificationBox.getChildren().addAll(titleLabel, dateLabel, typeLabel, targetLabel, contentArea);
				dialogContent.getChildren().add(notificationBox);
			}

			scrollPane.setContent(dialogContent);
			dialog.getDialogPane().setContent(scrollPane);
			dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
			dialog.getDialogPane().setPrefSize(700, 600);

			Node closeButton = dialog.getDialogPane().lookupButton(ButtonType.CLOSE);
			closeButton.setStyle("-fx-background-color: #25b6aa; -fx-text-fill: white; -fx-font-weight: bold;");

			dialog.showAndWait();
		} catch (SQLException e) {
			showAlert("Error", "Failed to load notification history: " + e.getMessage(), Alert.AlertType.ERROR);
			e.printStackTrace();
		}
	}

	@FXML
	private void handleBack(ActionEvent event) {
		if ("instructor".equals(sourceScreen) || "main".equals(sourceScreen)) {
			if (currentStage != null) {
				currentStage.close();
			} else {
				Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
				stage.close();
			}
		} else {
			navigateBackToMain(event);
		}
	}

	private void navigateBackToMain(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/frontend/view/admin/AdminMainPage.fxml"));
			Parent root = loader.load();

			Scene scene = new Scene(root);
			Stage stage;

			if (currentStage != null) {
				stage = currentStage;
			} else {
				stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			}

			stage.setScene(scene);

			Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
			stage.setX((screenBounds.getWidth() - stage.getWidth()) / 2);
			stage.setY((screenBounds.getHeight() - stage.getHeight()) / 2);

			stage.show();
		} catch (IOException e) {
			showAlert("Error", "Failed to navigate back: " + e.getMessage(), Alert.AlertType.ERROR);
			e.printStackTrace();
		}
	}

	private String getIconForNotificationCategory(String category) {
		switch (category) {
		case "System Maintenance":
			return "🛠";
		case "Event/Campaign Announcement":
			return "📣";
		case "Course Promotion":
			return "✅";
		case "New Course Notification":
			return "📚";
		case "Behavior Warning":
			return "⚠️";
		case "Policy/System Update":
			return "🧾";
		case "Course Approved":
			return "✔️";
		case "Course Rejected":
			return "❌";
		case "System Upgrade Notice":
			return "🔄";
		default:
			return "📢";
		}
	}

	private int getTargetRoleId(String targetRole) {
		switch (targetRole) {
		case "Students Only":
			return 2;
		case "Instructors Only":
			return 1;
		default:
			return 0;
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