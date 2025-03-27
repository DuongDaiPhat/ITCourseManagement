package model.user;

import java.time.LocalDateTime;
import java.util.Objects;

public class MyWishList {
	private int userID;
	private int courseID;
	private LocalDateTime addedAt;

	public MyWishList() {
	}

	public MyWishList(int userID, int courseID, LocalDateTime addedAt) {
		this.userID = userID;
		this.courseID = courseID;
		this.addedAt = addedAt;
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

	public LocalDateTime getAddedAt() {
		return addedAt;
	}

	public void setAddedAt(LocalDateTime addedAt) {
		this.addedAt = addedAt;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		MyWishList that = (MyWishList) o;
		return userID == that.userID && courseID == that.courseID;
	}

	@Override
	public int hashCode() {
		return Objects.hash(userID, courseID);
	}

	@Override
	public String toString() {
		return "MyWishList{" + "userID=" + userID + ", courseID=" + courseID + ", addedAt=" + addedAt + '}';
	}

	public void print() {
		System.out.println(this.toString());
	}
}