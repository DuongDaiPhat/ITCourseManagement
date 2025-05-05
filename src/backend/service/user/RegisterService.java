package backend.service.user;

import java.sql.SQLException;

import backend.repository.user.UsersRepository;

public class RegisterService {
<<<<<<< Updated upstream
	
	public static RegisterService getInstance() {
		return new RegisterService();
	}
	public boolean isExistUsername(String username) throws SQLException {
		return UsersRepository.getInstance().isExistUser(username);
	}
}
=======
    public static RegisterService getInstance() {
        return new RegisterService();
    }

    public boolean isExistUsername(String username) throws SQLException {
        return UsersRepository.getInstance().isExistUser(username);
    }

    public boolean registerUser(Users user) throws SQLException {
        // Check if the login name already exists
        if (isExistUsername(user.getUserName())) {
            System.out.println("Username already exists: " + user.getUserName());
            return false;
        }

        try {
            // Create salt
            String salt = PasswordUtil.getSalt();
            System.out.println("Generated salt: " + salt);

            // Encrypt password with salt
            String hashedPassword = PasswordUtil.getSHA256Hash(user.getPassword(), salt);
            System.out.println("Hashed password: " + hashedPassword);

            //Save salt and encrypted password to user object
            user.setSalt(salt); 
            user.setPassword(hashedPassword);

            // Save user to database
            int result = UsersRepository.getInstance().Insert(user);
            System.out.println("Insert result: " + result + " row(s) affected");

            return result > 0;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return false;
        }
    }
}
>>>>>>> Stashed changes
