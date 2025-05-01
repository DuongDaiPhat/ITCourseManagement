package backend.service.user;

import java.sql.SQLException;
import java.security.NoSuchAlgorithmException;
import backend.repository.user.UsersRepository;
import model.user.Users;
import utils.PasswordUtil;

public class RegisterService {
    public static RegisterService getInstance() {
        return new RegisterService();
    }

    public boolean isExistUsername(String username) throws SQLException {
        return UsersRepository.getInstance().isExistUser(username);
    }

    public boolean registerUser(Users user) throws SQLException {
        // Kiểm tra tên đăng nhập đã tồn tại
        if (isExistUsername(user.getUserName())) {
            System.out.println("Username already exists: " + user.getUserName());
            return false;
        }

        try {
            // Tạo salt
            String salt = PasswordUtil.getSalt();
            System.out.println("Generated salt: " + salt);

            // Mã hóa mật khẩu với salt
            String hashedPassword = PasswordUtil.getSHA256Hash(user.getPassword(), salt);
            System.out.println("Hashed password: " + hashedPassword);

            // Lưu salt và mật khẩu đã mã hóa vào đối tượng user
            user.setSalt(salt); // Giả sử lớp Users có phương thức setSalt
            user.setPassword(hashedPassword);

            // Lưu người dùng vào cơ sở dữ liệu
            int result = UsersRepository.getInstance().Insert(user);
            System.out.println("Insert result: " + result + " row(s) affected");

            return result > 0;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return false;
        }
    }
}