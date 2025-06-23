package backend.controller.notifications;

import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.geometry.Pos;
import model.notification.NotificationStatus;
import model.notification.UserNotification;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class NotificationListCell extends ListCell<UserNotification> {
	@Override
	protected void updateItem(UserNotification notification, boolean empty) {
		super.updateItem(notification, empty);

		if (empty || notification == null) {
			setText(null);
			setGraphic(null);
		} else {
			VBox container = new VBox();
			container.setStyle(
					"-fx-background-color: white; -fx-padding: 15px; -fx-border-color: #eeeeee; -fx-border-width: 0 0 1 0;");
			container.setSpacing(8);

			HBox header = new HBox();
			header.setAlignment(Pos.CENTER_LEFT);
			header.setSpacing(10);

			Label iconLabel = new Label(notification.getIcon());
			iconLabel.setStyle("-fx-font-size: 16px;");

			Label titleLabel = new Label(notification.getNotificationName());
			titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #25b6aa;");

			Label categoryLabel = new Label("(" + notification.getCategory() + ")");
			categoryLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666666;");

			header.getChildren().addAll(iconLabel, titleLabel, categoryLabel);

			Label contentLabel = new Label(notification.getContent());
			contentLabel.setStyle("-fx-font-size: 14px; -fx-wrap-text: true; -fx-text-fill: #333333;");
			contentLabel.setMaxWidth(600);

			// Format thời gian
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");
			String timeText = notification.getNotifiedAt() != null ? notification.getNotifiedAt().format(formatter)
					: "Unknown time";
			Label timeLabel = new Label(timeText);
			timeLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #999999; -fx-padding: 5 0 0 0;");

			// Thêm indicator cho thông báo chưa đọc
			Label statusLabel = new Label();
			if (notification.getStatus() == NotificationStatus.UNREAD) {
				statusLabel.setText("•");
				statusLabel.setStyle("-fx-text-fill: #25b6aa; -fx-font-weight: bold; -fx-font-size: 20px;");
			}

			HBox footer = new HBox(10, timeLabel, statusLabel);
			footer.setAlignment(Pos.CENTER_LEFT);

			container.getChildren().addAll(header, contentLabel, footer);
			setGraphic(container);
		}
	}
}