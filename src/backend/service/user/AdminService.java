package backend.service.user;

import java.sql.SQLException;
import java.util.List;

import model.user.UserStatus;
import model.user.Users;
import backend.repository.user.AdminRepository;

public class AdminService {
	private AdminRepository adminRepository = AdminRepository.getInstance();

	public List<Users> getAllStudents() throws SQLException {
		return adminRepository.getAllStudents();
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
}