package backend.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;

import entity.Courses;
import entity.Language;
import entity.Level;
import entity.ProgrammingLanguage;

public class CourseDAO implements DAOInterface<Courses>{

	public static CourseDAO getInstance() {
		return new CourseDAO();
	}
	@Override
	public int Insert(Courses t) throws SQLException {
	    int result = 0;
	    String sql = "INSERT INTO COURSES(COURSEID, COURSENAME, LANGUAGE, PROGRAMMINGLANGUAGE, LEVEL, USERID, THUMBNAILURL, PRICE, COURSEDESCRIPTION, CREATEDAT, UPDATEDAT) "
	               + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	    try (Connection con = DatabaseConnection.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql)) {

	        ps.setString(1, t.getCourseID()+"");
	        ps.setString(2, t.getCourseName());
	        ps.setString(3, t.getLanguage()+"");
	        ps.setString(4, t.getProgrammingLanguage()+"");
	        ps.setString(5, t.getLevel()+"");
	        ps.setString(6, t.getUserID()+"");
	        ps.setString(7, t.getThumbnailURL());
	        ps.setDouble(8, t.getPrice());
	        ps.setString(9, t.getCourseDescription()+"");
	        ps.setString(10, t.getCreatedAt()+"");
	        ps.setString(11, t.getUpdatedAt()+"");

	        result = ps.executeUpdate();
	        System.out.println("Insert executed. " + result + " row(s) affected");
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return result;
	}

	@Override
	public int Update(Courses t) throws SQLException {
		int result = 0;
		String sql = "UPDATE COURSES SET COURSENAME = ?, LANGUAGE = ?, PROGRAMMINGLANGUAGE = ?, LEVEL = ?, USERID = ?, THUMBNAILURL = ?, PRICE = ?, COURSEDESCRIPTION = ?, CREATEDAT = ?, UPDATEDAT = ? WHERE COURSESID = ?;";
		try(Connection con = DatabaseConnection.getConnection();
			PreparedStatement ps = con.prepareStatement(sql)){
			ps.setString(1, t.getCourseName());
			ps.setString(2, t.getLanguage() + "");
			ps.setString(3, t.getProgrammingLanguage() + "");
			ps.setString(4, t.getLevel() + "");
			ps.setString(5, t.getUserID() + "");
			ps.setString(6, t.getThumbnailURL() + "");
			ps.setString(7, t.getPrice() + "");
			ps.setString(8, t.getCourseDescription() + "");
			ps.setString(9, t.getUpdatedAt() + "");
			ps.setString(10, t.getLanguage() + "");
			ps.setString(11, t.getLanguage() + "");
		}
		return result;
	}

	@Override
	public int Delete(Courses t) throws SQLException {
	    int result = 0;
	    String sql = "DELETE FROM COURSES WHERE COURSEID = ?";
	    try (Connection con = DatabaseConnection.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql)) {

	        ps.setString(1, t.getCourseID() + "");
	        result = ps.executeUpdate();
	        System.out.println("Delete executed. " + result + " row(s) affected");
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return result;
	}
	@Override
	public ArrayList<Courses> SelectAll() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Courses SelectByID(Courses t) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Courses> SelectByCondition(String condition) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	
}
