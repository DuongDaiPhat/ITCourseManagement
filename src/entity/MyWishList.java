package entity;

import java.util.Date;

public class MyWishList {
	private int userId;
	private int courseId;
	private Date addedAt;

	public MyWishList() {
	}

	public MyWishList(int userId, int courseId, Date addedAt) {
		this.userId = userId;
		this.courseId = courseId;
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

	public Date getAddedAt() {
		return addedAt;
	}

	public void setAddedAt(Date addedAt) {
		this.addedAt = addedAt;
	}
}
