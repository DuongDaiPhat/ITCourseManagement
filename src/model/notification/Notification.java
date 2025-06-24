package model.notification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Notification {
	private int notificationID;
	private String notificationName;
	private String content;
	private LocalDateTime notifiedAt = LocalDateTime.now();
	private String icon;
	private String category;
	private List<UserNotification> notificationDetails = new ArrayList<>();

	public Notification() {
	}

	public Notification(int notificationID, String notificationName, String content, LocalDateTime notifiedAt,
			String icon, String category) {
		this.notificationID = notificationID;
		this.notificationName = notificationName;
		this.content = content;
		this.notifiedAt = notifiedAt;
		this.icon = icon;
		this.category = category;
	}

	public int getNotificationID() {
		return notificationID;
	}

	public void setNotificationID(int notificationID) {
		this.notificationID = notificationID;
	}

	public String getNotificationName() {
		return notificationName;
	}

	public void setNotificationName(String notificationName) {
		this.notificationName = notificationName;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public LocalDateTime getNotifiedAt() {
		return notifiedAt;
	}

	public void setNotifiedAt(LocalDateTime notifiedAt) {
		this.notifiedAt = notifiedAt;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public List<UserNotification> getNotificationDetails() {
		return notificationDetails;
	}

	public void setNotificationDetails(List<UserNotification> notificationDetails) {
		this.notificationDetails = notificationDetails;
	}
}