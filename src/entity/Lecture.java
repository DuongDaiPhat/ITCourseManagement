package entity;

public class Lecture {
	private int lectureId;
	private String lectureName;
	private int courseId;
	private String videoUrl;
	private int duration;
	private String lectureDescription;

	public Lecture() {
	}

	public Lecture(int lectureId, String lectureName, int courseId, String videoUrl, int duration,
			String lectureDescription) {
		this.lectureId = lectureId;
		this.lectureName = lectureName;
		this.courseId = courseId;
		this.videoUrl = videoUrl;
		this.duration = duration;
		this.lectureDescription = lectureDescription;
	}

	public int getLectureId() {
		return lectureId;
	}

	public void setLectureId(int lectureId) {
		this.lectureId = lectureId;
	}

	public String getLectureName() {
		return lectureName;
	}

	public void setLectureName(String lectureName) {
		this.lectureName = lectureName;
	}

	public int getCourseId() {
		return courseId;
	}

	public void setCourseId(int courseId) {
		this.courseId = courseId;
	}

	public String getVideoUrl() {
		return videoUrl;
	}

	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public String getLectureDescription() {
		return lectureDescription;
	}

	public void setLectureDescription(String lectureDescription) {
		this.lectureDescription = lectureDescription;
	}
}