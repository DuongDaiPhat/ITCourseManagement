package backend.service.user;

import java.sql.SQLException;

import backend.repository.user.UsersRepository;

public class RegisterService {
	
	public static RegisterService getInstance() {
		return new RegisterService();
	}
	public boolean isExistUsername(String username) throws SQLException {
		return UsersRepository.getInstance().isExistUser(username);
	}
}
