package backend.service.user;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import model.user.UserStatus;
import model.user.Users;
import backend.repository.user.AdminRepository;

public class AdminService {
	private AdminRepository adminRepository = AdminRepository.getInstance();

	public List<Users> getAllStudents() throws SQLException {
		return adminRepository.getAllStudents();
	}

	public List<Users> searchStudents(String keyword) throws SQLException {
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			LocalDate searchDate = LocalDate.parse(keyword, formatter);
			return adminRepository.searchStudentsByDate(searchDate);
		} catch (DateTimeParseException e) {
			return adminRepository.searchStudents(keyword);
		}
	}

	public List<Users> searchInstructors(String keyword) throws SQLException {
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			LocalDate searchDate = LocalDate.parse(keyword, formatter);
			return adminRepository.searchInstructors(keyword);
		} catch (DateTimeParseException e) {
			return adminRepository.searchInstructors(keyword);
		}
	}

	public List<Users> getAllInstructors() throws SQLException {
		return adminRepository.getAllInstructors();
	}

	public boolean updateStudentStatus(int userId, String status) throws SQLException {
		try {
			UserStatus userStatus = UserStatus.valueOf(status.toLowerCase());
			int result = adminRepository.updateStudentStatus(userId, userStatus);
			return result > 0;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	public boolean updateInstructorStatus(int instructorId, String status) throws SQLException {
		try {
			UserStatus userStatus = UserStatus.valueOf(status.toLowerCase());
			int result = adminRepository.updateInstructorStatus(instructorId, userStatus);
			return result > 0;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	public boolean warnStudent(int studentId) throws SQLException {
		int result = adminRepository.incrementWarningCount(studentId);

		int warningCount = adminRepository.getWarningCount(studentId);
		if (warningCount >= 5) {
			adminRepository.updateStudentStatus(studentId, UserStatus.banned);
			return true;
		}

		return result > 0;
	}

	public boolean warnInstructor(int instructorId) throws SQLException {
		int result = adminRepository.incrementWarningCount(instructorId);

		int warningCount = adminRepository.getWarningCount(instructorId);
		if (warningCount >= 5) {
			adminRepository.updateInstructorStatus(instructorId, UserStatus.banned);
			return true;
		}

		return result > 0;
	}

	public boolean unbanStudent(int studentId) throws SQLException {
		adminRepository.resetWarningCount(studentId);
		return updateStudentStatus(studentId, "online");
	}

	public boolean unbanInstructor(int instructorId) throws SQLException {
		adminRepository.resetWarningCount(instructorId);
		return updateInstructorStatus(instructorId, "online");
	}

	public int getStudentWarningCount(int studentId) throws SQLException {
		return adminRepository.getWarningCount(studentId);
	}

	public int getInstructorWarningCount(int instructorId) throws SQLException {
		return adminRepository.getWarningCount(instructorId);
	}
}