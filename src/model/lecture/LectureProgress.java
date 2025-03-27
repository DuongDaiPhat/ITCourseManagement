package model.lecture;

import model.lecture.LectureStatus;
import java.util.Objects;

public class LectureProgress {
	private int userID;
	private int lectureID;
	private LectureStatus status;

	public LectureProgress() {
	}

	public LectureProgress(int userID, int lectureID, LectureStatus status) {
		this.userID = userID;
		this.lectureID = lectureID;
		this.status = status;
	}

	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

	public int getLectureID() {
		return lectureID;
	}

	public void setLectureID(int lectureID) {
		this.lectureID = lectureID;
	}

	public LectureStatus getStatus() {
		return status;
	}

	public void setStatus(LectureStatus status) {
		this.status = status;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		LectureProgress that = (LectureProgress) o;
		return userID == that.userID && lectureID == that.lectureID;
	}

	@Override
	public int hashCode() {
		return Objects.hash(userID, lectureID);
	}

	@Override
	public String toString() {
		return "LectureProgress{" + "userID=" + userID + ", lectureID=" + lectureID + ", status=" + status + '}';
	}

	public void print() {
		System.out.println(this.toString());
	}
}