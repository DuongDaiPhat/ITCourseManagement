package backend.repository.course;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import backend.repository.RepositoryInterface;
import backend.repository.DatabaseConnection;
import model.course.CourseCategories;

public class CourseCategoriesRepository implements RepositoryInterface<CourseCategories> {

	public static CourseCategoriesRepository getInstance() {
		return new CourseCategoriesRepository();
	}

	@Override
	public int Insert(CourseCategories t) throws SQLException {
		int result = 0;
		String sql = "INSERT INTO COURSECATEGORIES (COURSEID, CATEGORYID) VALUES (?, ?)";
		try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setInt(1, t.getCourseID());
			ps.setInt(2, t.getCategoryID());

			result = ps.executeUpdate();
			System.out.println("Insert executed. " + result + " row(s) affected");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DatabaseConnection.closeConnection();
		}
		return result;
	}

	@Override
	public int Update(CourseCategories t) throws SQLException {
		int result = 0;
		result += Delete(t);
		result += Insert(t);
		return result;
	}

	@Override
	public int Delete(CourseCategories t) throws SQLException {
		int result = 0;
		String sql = "DELETE FROM COURSECATEGORIES WHERE COURSEID = ? AND CATEGORYID = ?";
		try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setInt(1, t.getCourseID());
			ps.setInt(2, t.getCategoryID());

			result = ps.executeUpdate();
			System.out.println("Delete executed. " + result + " row(s) affected");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DatabaseConnection.closeConnection();
		}
		return result;
	}

	@Override
	public ArrayList<CourseCategories> SelectAll() throws SQLException {
		ArrayList<CourseCategories> courseCategoriesList = new ArrayList<>();
		String sql = "SELECT * FROM COURSECATEGORIES";
		try (Connection con = DatabaseConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				CourseCategories courseCategory = new CourseCategories();
				courseCategory.setCourseID(rs.getInt("COURSEID"));
				courseCategory.setCategoryID(rs.getInt("CATEGORYID"));
				courseCategoriesList.add(courseCategory);
			}
			System.out.println(courseCategoriesList.size() + " row(s) found");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DatabaseConnection.closeConnection();
		}
		return courseCategoriesList;
	}

	@Override
	public CourseCategories SelectByID(int id) throws SQLException {
		CourseCategories courseCategory = null;
		String sql = "SELECT * FROM COURSECATEGORIES WHERE COURSEID = ? OR CATEGORYID = ? LIMIT 1";
		try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setInt(1, id);
			ps.setInt(2, id);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				courseCategory = new CourseCategories();
				courseCategory.setCourseID(rs.getInt("COURSEID"));
				courseCategory.setCategoryID(rs.getInt("CATEGORYID"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DatabaseConnection.closeConnection();
		}
		return courseCategory;
	}

	@Override
	public ArrayList<CourseCategories> SelectByCondition(String condition) throws SQLException {
		ArrayList<CourseCategories> courseCategoriesList = new ArrayList<>();
		String sql = "SELECT * FROM COURSECATEGORIES WHERE " + condition;
		try (Connection con = DatabaseConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				CourseCategories courseCategory = new CourseCategories();
				courseCategory.setCourseID(rs.getInt("COURSEID"));
				courseCategory.setCategoryID(rs.getInt("CATEGORYID"));
				courseCategoriesList.add(courseCategory);
			}
			System.out.println(courseCategoriesList.size() + " row(s) found");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DatabaseConnection.closeConnection();
		}
		return courseCategoriesList;
	}

	public ArrayList<CourseCategories> SelectByCourseID(int courseID) throws SQLException {
		ArrayList<CourseCategories> courseCategoriesList = new ArrayList<>();
		String sql = "SELECT * FROM COURSECATEGORIES WHERE COURSEID = ?";
		try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setInt(1, courseID);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				CourseCategories courseCategory = new CourseCategories();
				courseCategory.setCourseID(rs.getInt("COURSEID"));
				courseCategory.setCategoryID(rs.getInt("CATEGORYID"));
				courseCategoriesList.add(courseCategory);
			}
			System.out.println(courseCategoriesList.size() + " row(s) found for courseID: " + courseID);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DatabaseConnection.closeConnection();
		}
		return courseCategoriesList;
	}

	public ArrayList<CourseCategories> SelectByCategoryID(int categoryID) throws SQLException {
		ArrayList<CourseCategories> courseCategoriesList = new ArrayList<>();
		String sql = "SELECT * FROM COURSECATEGORIES WHERE CATEGORYID = ?";
		try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setInt(1, categoryID);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				CourseCategories courseCategory = new CourseCategories();
				courseCategory.setCourseID(rs.getInt("COURSEID"));
				courseCategory.setCategoryID(rs.getInt("CATEGORYID"));
				courseCategoriesList.add(courseCategory);
			}
			System.out.println(courseCategoriesList.size() + " row(s) found for categoryID: " + categoryID);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DatabaseConnection.closeConnection();
		}
		return courseCategoriesList;
	}

	public CourseCategories SelectByCompositeID(int courseID, int categoryID) throws SQLException {
		CourseCategories courseCategory = null;
		String sql = "SELECT * FROM COURSECATEGORIES WHERE COURSEID = ? AND CATEGORYID = ?";
		try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setInt(1, courseID);
			ps.setInt(2, categoryID);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				courseCategory = new CourseCategories();
				courseCategory.setCourseID(rs.getInt("COURSEID"));
				courseCategory.setCategoryID(rs.getInt("CATEGORYID"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DatabaseConnection.closeConnection();
		}
		return courseCategory;
	}
}