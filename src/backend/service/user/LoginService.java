package backend.service.user;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

import backend.repository.user.UsersRepository;
import utils.PasswordUtil;

public class LoginService {
	public static LoginService getInstance() {
		return new LoginService();
	}
	//1: valid username and password;
	//0: wrong username or password;
	public boolean LoginCheck(String username, String password) throws SQLException {
        try {
            // Get encrypted password and salt from database
            UsersRepository repo = UsersRepository.getInstance();
            String storedHashedPassword = repo.GetUserPasswordByName(username);
            String storedSalt = repo.GetUserSaltByName(username);
            
            if (storedHashedPassword.isEmpty() || storedSalt.isEmpty()) {
                System.out.println("User not found or missing salt/password");
                return false;
            }

            //  Encrypt user entered passwords with the same salt
            String inputHashedPassword = PasswordUtil.getSHA256Hash(password, storedSalt);
            // Compare encryption passwords
            return inputHashedPassword.equals(storedHashedPassword);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return false;
        }
    }
}
