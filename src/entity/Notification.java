package entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Notification {
	private int notificationID;
    private String notificationName;
    private String content;
    private LocalDateTime notifiedAt = LocalDateTime.now();
    private List<NotificationDetail> notificationDetails = new ArrayList<>();
    
	public Notification(int notificationID, String notificationName, String content, LocalDateTime notifiedAt,
			List<NotificationDetail> notificationDetails) {
		super();
		this.notificationID = notificationID;
		this.notificationName = notificationName;
		this.content = content;
		this.notifiedAt = notifiedAt;
		this.notificationDetails = notificationDetails;
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
	public List<NotificationDetail> getNotificationDetails() {
		return notificationDetails;
	}
	public void setNotificationDetails(List<NotificationDetail> notificationDetails) {
		this.notificationDetails = notificationDetails;
	}  
}
