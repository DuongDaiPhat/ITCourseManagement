package backend.service.user;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import backend.repository.user.UsersRepository;
import utils.PasswordUtil;

public class LoginService {
    public static LoginService getInstance() {
        return new LoginService();
    }

    // Trả về true nếu username và password hợp lệ, false nếu sai
    public boolean LoginCheck(String username, String password) throws SQLException {
        try {
            // Lấy mật khẩu đã mã hóa và salt từ cơ sở dữ liệu
            UsersRepository repo = UsersRepository.getInstance();
            String storedHashedPassword = repo.GetUserPasswordByName(username);
            String storedSalt = repo.GetUserSaltByName(username); // Giả sử có phương thức này

            if (storedHashedPassword.isEmpty() || storedSalt.isEmpty()) {
                System.out.println("User not found or missing salt/password");
                return false;
            }

            // Mã hóa mật khẩu người dùng nhập với cùng salt
            String inputHashedPassword = PasswordUtil.getSHA256Hash(password, storedSalt);

            // So sánh mật khẩu mã hóa
            return inputHashedPassword.equals(storedHashedPassword);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return false;
        }
    }
}