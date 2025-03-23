package entity;

public class CourseCategories {
	private int courseId;
	private int categoryId;

	public CourseCategories(int courseId, int categoryId) {
		this.courseId = courseId;
		this.categoryId = categoryId;
	}

	public int getCourseId() {
		return courseId;
	}

	public void setCourseId(int courseId) {
		this.courseId = courseId;
	}

	public int getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}
}