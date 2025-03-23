package entity;

public class LectureProgress {
	private int userId;
	private int lectureId;
	private LectureProgressStatus status;

	public LectureProgress(int userId, int lectureId, LectureProgressStatus status) {
		this.userId = userId;
		this.lectureId = lectureId;
		this.status = status;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getLectureId() {
		return lectureId;
	}

	public void setLectureId(int lectureId) {
		this.lectureId = lectureId;
	}

	public LectureProgressStatus getStatus() {
		return status;
	}

	public void setStatus(LectureProgressStatus status) {
		this.status = status;
	}
}