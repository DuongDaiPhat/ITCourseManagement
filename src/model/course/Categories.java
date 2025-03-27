package model.course;

import java.util.Objects;

public class Categories {
	private int categoryID;
	private String categoryName;

	public Categories() {
	}

	public Categories(int categoryID, String categoryName) {
		this.categoryID = categoryID;
		this.categoryName = categoryName;
	}

	public int getCategoryID() {
		return categoryID;
	}

	public void setCategoryID(int categoryID) {
		this.categoryID = categoryID;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Categories that = (Categories) o;
		return categoryID == that.categoryID;
	}

	@Override
	public int hashCode() {
		return Objects.hash(categoryID);
	}

	@Override
	public String toString() {
		return "Categories{" + "categoryID=" + categoryID + ", categoryName='" + categoryName + '\'' + '}';
	}

	public void print() {
		System.out.println(this.toString());
	}
}