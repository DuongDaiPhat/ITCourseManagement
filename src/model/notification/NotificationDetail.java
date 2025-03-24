package model.notification;

public class NotificationDetail {
	private int notificationID;
    private int userID;
    private NotificationStatus status = NotificationStatus.UNREAD;
    
	public NotificationDetail(int notificationID, int userID, NotificationStatus status) {
		super();
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
	public NotificationStatus getStatus() {
		return status;
	}
	public void setStatus(NotificationStatus status) {
		this.status = status;
	}
    
    
}
