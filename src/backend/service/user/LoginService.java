package backend.service.user;

import java.sql.SQLException;

import backend.repository.user.UsersRepository;

public class LoginService {
	public static LoginService getInstance() {
		return new LoginService();
	}
	//1: valid username and password;
	//0: wrong username or password;
	public boolean LoginCheck(String username, String password) throws SQLException {
		String checkPassword = UsersRepository.getInstance().GetUserPasswordByName(username);
		if(checkPassword.isEmpty()) {
			return false;
		}
		if(checkPassword.equals(password)) {
			return true;
		}
		return false;
	}
}
