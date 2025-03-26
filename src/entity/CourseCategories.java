package entity;

import java.util.Objects;

public class CourseCategories {
	private int courseID;
	private int categoryID;

	public CourseCategories() {
	}

	public CourseCategories(int courseID, int categoryID) {
		this.courseID = courseID;
		this.categoryID = categoryID;
	}

	public int getCourseID() {
		return courseID;
	}

	public void setCourseID(int courseID) {
		this.courseID = courseID;
	}

	public int getCategoryID() {
		return categoryID;
	}

	public void setCategoryID(int categoryID) {
		this.categoryID = categoryID;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		CourseCategories that = (CourseCategories) o;
		return courseID == that.courseID && categoryID == that.categoryID;
	}

	@Override
	public int hashCode() {
		return Objects.hash(courseID, categoryID);
	}

	@Override
	public String toString() {
		return "CourseCategories{" + "courseID=" + courseID + ", categoryID=" + categoryID + '}';
	}

	public void print() {
		System.out.println(this.toString());
	}
}