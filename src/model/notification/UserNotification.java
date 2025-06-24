package model.notification;

import java.time.LocalDateTime;

public class UserNotification {
	private int notificationID;
	private int userID;
	private String notificationName;
	private String content;
	private String icon;
	private String category;
	private NotificationStatus status = NotificationStatus.UNREAD;
	private LocalDateTime notifiedAt;

	public UserNotification() {
	}

	public UserNotification(int notificationID, int userID, NotificationStatus status) {
		this.notificationID = notificationID;
		this.userID = userID;
		this.status = status;
	}

	public int getNotificationID() {
		return notificationID;
	}

	public void setNotificationID(int notificationID) {
		this.notificationID = notificationID;
	}

	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
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

	public NotificationStatus getStatus() {
		return status;
	}

	public void setStatus(NotificationStatus status) {
		this.status = status;
	}

	public LocalDateTime getNotifiedAt() {
		return notifiedAt;
	}

	public void setNotifiedAt(LocalDateTime notifiedAt) {
		this.notifiedAt = notifiedAt != null ? notifiedAt : LocalDateTime.now();
	}
}