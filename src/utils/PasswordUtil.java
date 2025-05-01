package utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordUtil {

    // Hàm tạo Salt ngẫu nhiên
    public static String getSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);  // Mã hóa Salt thành chuỗi base64
    }

    // Hàm mã hóa mật khẩu với SHA-256 và Salt
    public static String getSHA256Hash(String password, String salt) throws NoSuchAlgorithmException {
        String saltedPassword = password + salt;  // Ghép mật khẩu và Salt
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(saltedPassword.getBytes());
        return Base64.getEncoder().encodeToString(hashBytes);  // Mã hóa kết quả thành chuỗi base64
    }
}

