package backend.repository.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import backend.repository.DatabaseConnection;
import model.user.Users;
import model.user.UserStatus;

public class AdminRepository {
	private Connection con = DatabaseConnection.getConnection();

	public static AdminRepository getInstance() {
		return new AdminRepository();
	}

	public List<Users> getAllStudents() throws SQLException {
		List<Users> students = new ArrayList<>();
		String sql = "SELECT * FROM USERS WHERE ROLEID = 2";

		try (Connection con = DatabaseConnection.getNewConnection();
				PreparedStatement ps = con.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				students.add(extractUserFromResultSet(rs));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return students;
	}

	public List<Users> getAllInstructors() throws SQLException {
		List<Users> instructors = new ArrayList<>();
		String sql = "SELECT u.*, COUNT(c.COURSEID) as courseCount FROM USERS u "
				+ "LEFT JOIN COURSES c ON u.USERID = c.USERID " + "WHERE u.ROLEID = 1 GROUP BY u.USERID";

		try (Connection con = DatabaseConnection.getNewConnection();
				PreparedStatement ps = con.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				Users instructor = new Users();
				instructor.setUserID(rs.getInt("USERID"));
				instructor.setUserFirstName(rs.getString("USERFIRSTNAME"));
				instructor.setUserLastName(rs.getString("USERLASTNAME"));
				instructor.setPhoneNumber(rs.getString("PHONENUMBER"));
				instructor.setEmail(rs.getString("EMAIL"));
				instructor.setCreatedAt(rs.getDate("CREATEDAT").toLocalDate());
				instructor.setStatus(UserStatus.valueOf(rs.getString("STATUS")));
				instructor.setCourseCount(rs.getInt("courseCount"));
				instructor.setWarningCount(rs.getInt("WARNING_COUNT"));
				instructors.add(instructor);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return instructors;
	}

	public List<Users> searchStudents(String keyword) throws SQLException {
		List<Users> students = new ArrayList<>();
		String sql = "SELECT * FROM USERS WHERE ROLEID = 2 AND " + "(USERID LIKE ? OR " + "USERFIRSTNAME LIKE ? OR "
				+ "USERLASTNAME LIKE ? OR " + "PHONENUMBER LIKE ? OR " + "EMAIL LIKE ? OR " + "STATUS LIKE ? OR "
				+ "DATE_FORMAT(CREATEDAT, '%d/%m/%Y') LIKE ?)";

		try (PreparedStatement ps = con.prepareStatement(sql)) {
			String searchPattern = "%" + keyword + "%";
			ps.setString(1, searchPattern);
			ps.setString(2, searchPattern);
			ps.setString(3, searchPattern);
			ps.setString(4, searchPattern);
			ps.setString(5, searchPattern);
			ps.setString(6, searchPattern);
			ps.setString(7, searchPattern);

			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				students.add(extractUserFromResultSet(rs));
			}
		}
		return students;
	}

	public List<Users> searchInstructors(String keyword) throws SQLException {
		List<Users> instructors = new ArrayList<>();
		String sql = "SELECT u.*, COUNT(c.COURSEID) as courseCount FROM USERS u "
				+ "LEFT JOIN COURSES c ON u.USERID = c.USERID " + "WHERE u.ROLEID = 1 AND " + "(u.USERID LIKE ? OR "
				+ "u.USERFIRSTNAME LIKE ? OR " + "u.USERLASTNAME LIKE ? OR " + "u.PHONENUMBER LIKE ? OR "
				+ "u.EMAIL LIKE ? OR " + "u.STATUS LIKE ? OR " + "DATE_FORMAT(u.CREATEDAT, '%d/%m/%Y') LIKE ?) "
				+ "GROUP BY u.USERID";

		try (PreparedStatement ps = con.prepareStatement(sql)) {
			String searchPattern = "%" + keyword + "%";
			ps.setString(1, searchPattern);
			ps.setString(2, searchPattern);
			ps.setString(3, searchPattern);
			ps.setString(4, searchPattern);
			ps.setString(5, searchPattern);
			ps.setString(6, searchPattern);
			ps.setString(7, searchPattern);

			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				Users instructor = new Users();
				instructor.setUserID(rs.getInt("USERID"));
				instructor.setUserFirstName(rs.getString("USERFIRSTNAME"));
				instructor.setUserLastName(rs.getString("USERLASTNAME"));
				instructor.setPhoneNumber(rs.getString("PHONENUMBER"));
				instructor.setEmail(rs.getString("EMAIL"));
				instructor.setCreatedAt(rs.getDate("CREATEDAT").toLocalDate());
				instructor.setStatus(UserStatus.valueOf(rs.getString("STATUS")));
				instructor.setCourseCount(rs.getInt("courseCount"));
				instructor.setWarningCount(rs.getInt("WARNING_COUNT"));
				instructors.add(instructor);
			}
		}
		return instructors;
	}

	public List<Users> searchStudentsByDate(LocalDate date) throws SQLException {
		List<Users> students = new ArrayList<>();
		String sql = "SELECT * FROM USERS WHERE ROLEID = 2 AND CREATEDAT = ?";

		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setDate(1, java.sql.Date.valueOf(date));
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				students.add(extractUserFromResultSet(rs));
			}
		}
		return students;
	}

	private Users extractUserFromResultSet(ResultSet rs) throws SQLException {
		Users student = new Users();
		student.setUserID(rs.getInt("USERID"));
		student.setUserFirstName(rs.getString("USERFIRSTNAME"));
		student.setUserLastName(rs.getString("USERLASTNAME"));
		student.setPhoneNumber(rs.getString("PHONENUMBER"));
		student.setEmail(rs.getString("EMAIL"));
		student.setCreatedAt(rs.getDate("CREATEDAT").toLocalDate());
		student.setStatus(UserStatus.valueOf(rs.getString("STATUS")));
		student.setWarningCount(rs.getInt("WARNING_COUNT"));
		return student;
	}

	public int updateStudentStatus(int userId, UserStatus status) throws SQLException {
		String sql = "UPDATE USERS SET STATUS = ? WHERE USERID = ?";
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, status.toString());
			ps.setInt(2, userId);
			return ps.executeUpdate();
		}
	}

	public int updateInstructorStatus(int instructorId, UserStatus userStatus) throws SQLException {
		String sql = "UPDATE USERS SET STATUS = ? WHERE USERID = ? AND ROLEID = 1";
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, userStatus.toString());
			ps.setInt(2, instructorId);
			return ps.executeUpdate();
		}
	}

	public int incrementWarningCount(int userId) throws SQLException {
		String sql = "UPDATE USERS SET WARNING_COUNT = WARNING_COUNT + 1 WHERE USERID = ?";
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, userId);
			return ps.executeUpdate();
		}
	}

	public int resetWarningCount(int userId) throws SQLException {
		String sql = "UPDATE USERS SET WARNING_COUNT = 0 WHERE USERID = ?";
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, userId);
			return ps.executeUpdate();
		}
	}

	public int getWarningCount(int userId) throws SQLException {
		String sql = "SELECT WARNING_COUNT FROM USERS WHERE USERID = ?";
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, userId);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt("WARNING_COUNT");
			}
			return 0;
		}
	}
}