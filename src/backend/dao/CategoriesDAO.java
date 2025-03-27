package backend.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import model.course.Categories;

public class CategoriesDAO implements DAOInterface<Categories> {

	public static CategoriesDAO getInstance() {
		return new CategoriesDAO();
	}

	@Override
	public int Insert(Categories t) throws SQLException {
		int result = 0;
		String sql = "INSERT INTO CATEGORIES(CATEGORYNAME) VALUES (?)";
		try (Connection con = DatabaseConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

			ps.setString(1, t.getCategoryName());
			result = ps.executeUpdate();

			try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					t.setCategoryID(generatedKeys.getInt(1));
				}
			}

			System.out.println("Insert executed. " + result + " row(s) affected");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DatabaseConnection.closeConnection();
		}
		return result;
	}

	@Override
	public int Update(Categories t) throws SQLException {
		int result = 0;
		String sql = "UPDATE CATEGORIES SET CATEGORYNAME = ? WHERE CATEGORYID = ?";
		try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setString(1, t.getCategoryName());
			ps.setInt(2, t.getCategoryID());

			result = ps.executeUpdate();
			System.out.println("Update executed. " + result + " row(s) affected");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DatabaseConnection.closeConnection();
		}
		return result;
	}

	@Override
	public int Delete(Categories t) throws SQLException {
		int result = 0;
		String sql = "DELETE FROM CATEGORIES WHERE CATEGORYID = ?";
		try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setInt(1, t.getCategoryID());
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
	public ArrayList<Categories> SelectAll() throws SQLException {
		ArrayList<Categories> categoryList = new ArrayList<>();
		String sql = "SELECT * FROM CATEGORIES";
		try (Connection con = DatabaseConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				Categories category = new Categories();
				category.setCategoryID(rs.getInt("CATEGORYID"));
				category.setCategoryName(rs.getString("CATEGORYNAME"));
				categoryList.add(category);
			}
			System.out.println(categoryList.size() + " row(s) found");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DatabaseConnection.closeConnection();
		}
		return categoryList;
	}

	@Override
	public Categories SelectByID(int id) throws SQLException {
		Categories category = new Categories();
		String sql = "SELECT * FROM CATEGORIES WHERE CATEGORYID = " + String.valueOf(id);
		try (Connection con = DatabaseConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			if (rs.next()) {
				category.setCategoryID(rs.getInt("CATEGORYID"));
				category.setCategoryName(rs.getString("CATEGORYNAME"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DatabaseConnection.closeConnection();
		}
		return category;
	}

	@Override
	public ArrayList<Categories> SelectByCondition(String condition) throws SQLException {
		ArrayList<Categories> categoryList = new ArrayList<>();
		String sql = "SELECT * FROM CATEGORIES WHERE " + condition;
		try (Connection con = DatabaseConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				Categories category = new Categories();
				category.setCategoryID(rs.getInt("CATEGORYID"));
				category.setCategoryName(rs.getString("CATEGORYNAME"));
				categoryList.add(category);
			}
			System.out.println(categoryList.size() + " row(s) found");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DatabaseConnection.closeConnection();
		}
		return categoryList;
	}
}