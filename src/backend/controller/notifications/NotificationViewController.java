package backend.controller.notifications;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import model.user.Session;
import backend.controller.scene.SceneManager;
import backend.repository.notification.NotificationRepository;
import model.notification.UserNotification;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import javafx.scene.control.Button;

public class NotificationViewController implements Initializable {
	@FXML
	private ListView<UserNotification> notificationListView;

	@FXML
	private ScrollPane scrollPane;

	@FXML
	private ComboBox<String> filterComboBox;

	@FXML
	private Button backButton;

	private NotificationRepository notificationRepository = new NotificationRepository();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		setupFilterComboBox();
		loadNotifications("ALL");
		setupListView();
		setupScrollPane();

		backButton.setOnAction(event -> handleBack());
	}

	private void setupFilterComboBox() {
		filterComboBox.getItems().addAll("ALL", "READ", "UNREAD");
		filterComboBox.setValue("ALL");
		filterComboBox.setStyle("-fx-background-color: white; -fx-border-color: #25b6aa; -fx-border-radius: 5px;");
		filterComboBox.setOnAction(event -> {
			loadNotifications(filterComboBox.getValue());
		});
	}

	private void setupListView() {
		notificationListView.setCellFactory(listView -> new NotificationListCell());
		notificationListView.setStyle("-fx-background-color: white; -fx-border-color: transparent;");

		notificationListView.setOnMouseClicked(event -> {
			UserNotification selected = notificationListView.getSelectionModel().getSelectedItem();
			if (selected != null) {
				SceneManager.switchScene("Notification Detail",
						"/frontend/view/notifications/NotificationDetailView.fxml", selected.getNotificationID());
			}
		});
	}

	private void setupScrollPane() {
		scrollPane.setStyle(
				"-fx-background: white; -fx-background-color: white; -fx-border-color: #25b6aa; -fx-border-radius: 5px;");
		scrollPane.setFitToWidth(true);
	}

	private void loadNotifications(String filter) {
		try {
			int userId = Session.getCurrentUser().getUserID();
			List<UserNotification> notifications;

			if ("READ".equals(filter)) {
				notifications = notificationRepository.getReadNotificationsForUser(userId);
			} else if ("UNREAD".equals(filter)) {
				notifications = notificationRepository.getUnreadNotificationsForUser(userId);
			} else {
				notifications = notificationRepository.getNotificationsForUser(userId);
			}

			ObservableList<UserNotification> observableList = FXCollections.observableArrayList(notifications);
			notificationListView.setItems(observableList);

			if ("ALL".equals(filter)) {
				markAllNotificationsAsRead(userId);
			}
		} catch (SQLException e) {
			showAlert("Database Error", "Failed to load notifications: " + e.getMessage(), Alert.AlertType.ERROR);
			e.printStackTrace();
		}
	}

	@FXML
	private void handleBack() {
		SceneManager.switchScene("Instructor Main Page", "/frontend/view/instructorMainPage/instructorMainPage.fxml");
	}

	private void markAllNotificationsAsRead(int userId) {
		new Thread(() -> {
			try {
				notificationRepository.markAllNotificationsAsRead(userId);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}).start();
	}

	private void showAlert(String title, String message, Alert.AlertType type) {
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
}