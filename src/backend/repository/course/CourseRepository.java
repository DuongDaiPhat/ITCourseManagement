package backend.repository.course;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import backend.repository.RepositoryInterface;
import backend.repository.DatabaseConnection;
import model.course.Courses;
import model.course.Language;
import model.course.Level;
import model.course.ProgrammingLanguage;

public class CourseRepository implements RepositoryInterface<Courses>{

	public static CourseRepository getInstance() {
		return new CourseRepository();
	}
	@Override
	public int Insert(Courses t) throws SQLException {
	    int result = 0;
	    String sql = "INSERT INTO COURSES(COURSENAME, LANGUAGE, PROGRAMMINGLANGUAGE, LEVEL, USERID, THUMBNAILURL, PRICE, COURSEDESCRIPTION, CREATEDAT, UPDATEDAT) "
	               + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	    try (Connection con = DatabaseConnection.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql)) {

	        ps.setString(1, t.getCourseName());
	        ps.setString(2, t.getLanguage()+"");
	        ps.setString(3, t.getProgrammingLanguage()+"");
	        ps.setString(4, t.getLevel()+"");
	        ps.setString(5, t.getUserID()+"");
	        ps.setString(6, t.getThumbnailURL());
	        ps.setDouble(7, t.getPrice());
	        ps.setString(8, t.getCourseDescription()+"");
	        ps.setString(9, t.getCreatedAt()+"");
	        ps.setString(10, t.getUpdatedAt()+"");

	        result = ps.executeUpdate();
	        System.out.println("Insert executed. " + result + " row(s) affected");
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    finally {
	    	DatabaseConnection.closeConnection();
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
	 	catch (SQLException e) {
	        e.printStackTrace();
	    }
		finally {
	    	DatabaseConnection.closeConnection();
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
	    finally {
	    	DatabaseConnection.closeConnection();
	    }
	    return result;
	}
	@Override
	public ArrayList<Courses> SelectAll() throws SQLException {
		ArrayList<Courses> courseList = new ArrayList<Courses>();
		String sql = "SELECT * FROM COURSES";
		try(Connection con = DatabaseConnection.getConnection();
			PreparedStatement ps = con.prepareStatement(sql);
			ResultSet rs =ps.executeQuery();){
			while(rs.next()) {
				Courses course = new Courses();
				course.setCourseID(rs.getInt("COURSEID"));
				course.setCourseName(rs.getString("COURSENAME"));
				course.setLanguage(Language.valueOf(rs.getString("LANGUAGE")));
				course.setProgrammingLanguage(ProgrammingLanguage.valueOf(rs.getString("PROGRAMMINGLANGUAGE")));
				course.setLevel(Level.valueOf(rs.getString("LEVEL")));
				course.setUserID(rs.getInt("USERID"));
				course.setThumbnailURL(rs.getString("THUMBNAILURL"));
				course.setPrice(rs.getFloat("PRICE"));
				course.setCourseDescription(rs.getString("COURSEDESCRIPTION"));
				course.setCreatedAt(rs.getTimestamp("CREATEDAT").toLocalDateTime());
				course.setUpdatedAt(rs.getTimestamp("UPDATEDAT").toLocalDateTime());
				courseList.add(course);
			}
			System.out.println(courseList.size() + " row(s) found");
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		finally {
			DatabaseConnection.closeConnection();
		}
		return courseList;
	}

	@Override
	public Courses SelectByID(int id) throws SQLException {
		Courses course = new Courses();
		String sql = "SELECT * FROM COURSES WHERE COURSEID = " + String.valueOf(id);
		try(Connection con = DatabaseConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()){
			while(rs.next()) {
				course.setCourseID(rs.getInt("COURSEID"));
				course.setCourseName(rs.getString("COURSENAME"));
				course.setLanguage(Language.valueOf(rs.getString("LANGUAGE")));
				course.setProgrammingLanguage(ProgrammingLanguage.valueOf(rs.getString("PROGRAMMINGLANGUAGE")));
				course.setLevel(Level.valueOf(rs.getString("LEVEL")));
				course.setUserID(rs.getInt("USERID"));
				course.setThumbnailURL(rs.getString("THUMBNAILURL"));
				course.setPrice(rs.getFloat("PRICE"));
				course.setCourseDescription(rs.getString("COURSEDESCRIPTION"));
				course.setCreatedAt(rs.getTimestamp("CREATEDAT").toLocalDateTime());
				course.setUpdatedAt(rs.getTimestamp("UPDATEDAT").toLocalDateTime());
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		finally {
			DatabaseConnection.closeConnection();
		}
		
		return course;
	}

	@Override
	public ArrayList<Courses> SelectByCondition(String condition) throws SQLException {
		ArrayList<Courses> courseList = new ArrayList<Courses>();
		String sql = "SELECT * FROM COURSES WHERE " + condition;
		try(Connection con = DatabaseConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql);
				ResultSet rs = ps.executeQuery();){
			while(rs.next()) {
				Courses course = new Courses();
				course.setCourseID(rs.getInt("COURSEID"));
				course.setCourseName(rs.getString("COURSENAME"));
				course.setLanguage(Language.valueOf(rs.getString("LANGUAGE")));
				course.setProgrammingLanguage(ProgrammingLanguage.valueOf(rs.getString("PROGRAMMINGLANGUAGE")));
				course.setLevel(Level.valueOf(rs.getString("LEVEL")));
				course.setUserID(rs.getInt("USERID"));
				course.setThumbnailURL(rs.getString("THUMBNAILURL"));
				course.setPrice(rs.getFloat("PRICE"));
				course.setCourseDescription(rs.getString("COURSEDESCRIPTION"));
				course.setCreatedAt(rs.getTimestamp("CREATEDAT").toLocalDateTime());
				course.setUpdatedAt(rs.getTimestamp("UPDATEDAT").toLocalDateTime());
				courseList.add(course);
			}
			System.out.println(courseList.size() + " row(s) found");
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		finally {
			DatabaseConnection.closeConnection();
		}
		return courseList;
	}
	
}
