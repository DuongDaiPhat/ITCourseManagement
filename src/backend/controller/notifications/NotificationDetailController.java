package backend.controller.notifications;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import model.user.Session;
import backend.controller.scene.SceneManager;
import backend.repository.notification.NotificationRepository;
import model.notification.Notification;
import javafx.scene.control.Alert;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

public class NotificationDetailController {
	@FXML
	private VBox notificationDetailContainer;

	@FXML
	private Label titleLabel;

	@FXML
	private Label categoryLabel;

	@FXML
	private Label timeLabel;

	@FXML
	private Label contentLabel;

	private NotificationRepository notificationRepository = new NotificationRepository();

	public void initialize() {
		int notificationId = (int) SceneManager.getData();
		loadNotificationDetail(notificationId);
	}

	private void loadNotificationDetail(int notificationId) {
		try {
			Notification notification = notificationRepository.getNotificationById(notificationId);

			titleLabel.setText(notification.getNotificationName());
			titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #25b6aa;");

			categoryLabel.setText("Category: " + notification.getCategory());
			categoryLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666666; -fx-padding: 5 0 10 0;");

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");
			String formattedTime = notification.getNotifiedAt() != null ? notification.getNotifiedAt().format(formatter)
					: "Unknown time";
			timeLabel.setText("Received: " + formattedTime);
			timeLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #999999;");

			contentLabel.setText(notification.getContent());
			contentLabel
					.setStyle("-fx-font-size: 16px; -fx-wrap-text: true; -fx-text-fill: #333333; -fx-padding: 20 0;");

			notificationRepository.markNotificationAsRead(notificationId, Session.getCurrentUser().getUserID());

		} catch (SQLException e) {
			e.printStackTrace();
			showAlert("Error", "Failed to load notification details", Alert.AlertType.ERROR);
		}
	}

	@FXML
	private void handleBack() {
		SceneManager.switchScene("Instructor Main Page", "/frontend/view/instructorMainPage/instructorMainPage.fxml");
	}

	private void showAlert(String title, String message, Alert.AlertType type) {
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
}