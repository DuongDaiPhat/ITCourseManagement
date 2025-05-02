package backend.repository.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
		String sql = "SELECT * FROM USERS WHERE ROLEID = 2"; // Assuming roleID 2 is for students

		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				Users student = new Users();
				student.setUserID(rs.getInt("USERID"));
				student.setUserFirstName(rs.getString("USERFIRSTNAME"));
				student.setUserLastName(rs.getString("USERLASTNAME"));
				student.setPhoneNumber(rs.getString("PHONENUMBER"));
				student.setEmail(rs.getString("EMAIL"));
				student.setCreatedAt(rs.getDate("CREATEDAT").toLocalDate());
				student.setStatus(UserStatus.valueOf(rs.getString("STATUS")));
				students.add(student);
			}
		}
		return students;
	}

	public List<Users> getAllInstructors() throws SQLException {
		List<Users> instructors = new ArrayList<>();
		String sql = "SELECT u.*, COUNT(c.COURSEID) as courseCount FROM USERS u "
				+ "LEFT JOIN COURSES c ON u.USERID = c.USERID " + "WHERE u.ROLEID = 1 " + // RoleID 1 là giảng viên
				"GROUP BY u.USERID";

		try (PreparedStatement ps = con.prepareStatement(sql)) {
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
				instructors.add(instructor);
			}
		}
		return instructors;
	}

	public int updateStudentStatus(int userId, UserStatus status) throws SQLException {
		String sql = "UPDATE USERS SET STATUS = ? WHERE USERID = ?";
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, status.toString());
			ps.setInt(2, userId);
			return ps.executeUpdate();
		}
	}
}