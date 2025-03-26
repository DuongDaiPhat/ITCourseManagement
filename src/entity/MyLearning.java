package entity;

import model.user.CourseStatus;
import java.time.LocalDateTime;
import java.util.Objects;

public class MyLearning {
	private int userID;
	private int courseID;
	private CourseStatus courseStatus;
	private LocalDateTime lastAccessedAt;

	public MyLearning() {
	}

	public MyLearning(int userID, int courseID, CourseStatus courseStatus, LocalDateTime lastAccessedAt) {
		this.userID = userID;
		this.courseID = courseID;
		this.courseStatus = courseStatus;
		this.lastAccessedAt = lastAccessedAt;
	}

	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

	public int getCourseID() {
		return courseID;
	}

	public void setCourseID(int courseID) {
		this.courseID = courseID;
	}

	public CourseStatus getCourseStatus() {
		return courseStatus;
	}

	public void setCourseStatus(CourseStatus courseStatus) {
		this.courseStatus = courseStatus;
	}

	public LocalDateTime getLastAccessedAt() {
		return lastAccessedAt;
	}

	public void setLastAccessedAt(LocalDateTime lastAccessedAt) {
		this.lastAccessedAt = lastAccessedAt;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		MyLearning that = (MyLearning) o;
		return userID == that.userID && courseID == that.courseID;
	}

	@Override
	public int hashCode() {
		return Objects.hash(userID, courseID);
	}

	@Override
	public String toString() {
		return "MyLearning{" + "userID=" + userID + ", courseID=" + courseID + ", courseStatus=" + courseStatus
				+ ", lastAccessedAt=" + lastAccessedAt + '}';
	}

	public void print() {
		System.out.println(this.toString());
	}
}
