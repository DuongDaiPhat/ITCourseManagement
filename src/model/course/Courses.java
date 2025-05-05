package model.course;

import java.time.LocalDateTime;

public class Courses {
	private int courseID;
	private String courseName;
	private Language language;
	private Technology technology;
	private Level level;
	private Category category;
	private int userID;
	private String thumbnailURL;
	private float price;
	private String CourseDescription;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private boolean isApproved;
	private boolean isPublished;
	private boolean isRejected;

	public Courses() {
		super();
	}

	public Courses(int courseID, String courseName, Language language, Technology technology, Level level,
			Category category, int userID, String thumbnailURL, float price, String courseDescription,
			LocalDateTime createdAt, LocalDateTime updatedAt, boolean isApproved, boolean isPublished) {
		super();
		this.courseID = courseID;
		this.courseName = courseName;
		this.language = language;
		this.technology = technology;
		this.level = level;
		this.category = category;
		this.userID = userID;
		this.thumbnailURL = thumbnailURL;
		this.price = price;
		CourseDescription = courseDescription;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.isApproved = isApproved;
		this.isPublished = isPublished;
	}

	public int getCourseID() {
		return courseID;
	}

	public void setCourseID(int courseID) {
		this.courseID = courseID;
	}

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	public Technology getTechnology() {
		return technology;
	}

	public void setTechnology(Technology technology) {
		this.technology = technology;
	}

	public Level getLevel() {
		return level;
	}

	public void setLevel(Level level) {
		this.level = level;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

	public String getThumbnailURL() {
		return thumbnailURL;
	}

	public void setThumbnailURL(String thumbnailURL) {
		this.thumbnailURL = thumbnailURL;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public String getCourseDescription() {
		return CourseDescription;
	}

	public void setCourseDescription(String courseDescription) {
		CourseDescription = courseDescription;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public boolean isPublished() {
		return isPublished;
	}

	public void setPublish(boolean isPublished) {
		this.isPublished = isPublished;
	}

	@Override
	public String toString() {
		return "Courses [courseID=" + courseID + ", courseName=" + courseName + ", language=" + language
				+ ", technology=" + technology + ", level=" + level + ", category=" + category + ", userID=" + userID
				+ ", thumbnailURL=" + thumbnailURL + ", price=" + price + ", CourseDescription=" + CourseDescription
				+ ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + ", isApproved=" + isApproved
				+ ", isPublished=" + isPublished + "]";
	}

	public boolean isApproved() {
		return isApproved;
	}

	public void setApproved(boolean approved) {
		isApproved = approved;
	}

	public void print() {
		System.out.println(this.toString());
	}

	public boolean isRejected() {
		return isRejected;
	}

	public void setRejected(boolean rejected) {
		isRejected = rejected;
	}

}
