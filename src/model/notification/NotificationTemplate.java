package model.notification;

public class NotificationTemplate {
	private int id;
	private String title;
	private String content;
	private String notificationType;
	private String category;

	// Default constructor
	public NotificationTemplate() {
	}

	// Constructor with all fields
	public NotificationTemplate(int id, String title, String content, String notificationType, String category) {
		this.id = id;
		this.title = title;
		this.content = content;
		this.notificationType = notificationType;
		this.category = category;
	}

	// Getters and Setters
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getNotificationType() {
		return notificationType;
	}

	public void setNotificationType(String notificationType) {
		this.notificationType = notificationType;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	// toString() method for debugging/logging
	@Override
	public String toString() {
		return "NotificationTemplate{" + "id=" + id + ", title='" + title + '\'' + ", content='" + content + '\''
				+ ", notificationType='" + notificationType + '\'' + ", category='" + category + '\'' + '}';
	}
}