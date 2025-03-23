package entity;

import java.time.LocalDateTime;

public class MyCart {
	private int userId;
    private int courseId;
    private boolean isBuy;
    private LocalDateTime addedAt;
    
	public MyCart(int userId, int courseId, boolean isBuy, LocalDateTime addedAt) {
		super();
		this.userId = userId;
		this.courseId = courseId;
		this.isBuy = isBuy;
		this.addedAt = addedAt;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getCourseId() {
		return courseId;
	}

	public void setCourseId(int courseId) {
		this.courseId = courseId;
	}

	public boolean isBuy() {
		return isBuy;
	}

	public void setBuy(boolean isBuy) {
		this.isBuy = isBuy;
	}

	public LocalDateTime getAddedAt() {
		return addedAt;
	}

	public void setAddedAt(LocalDateTime addedAt) {
		this.addedAt = addedAt;
	}
    
    
    
}
