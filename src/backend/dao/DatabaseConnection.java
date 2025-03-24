package backend.dao;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import utils.ConfigReader;

public class DatabaseConnection {
    private static Connection connection = null;

    // Phương thức lấy kết nối
    public static Connection getConnection() {
        if (connection == null) {
            try {
                // Đăng ký MySQL Driver 
                Class.forName("com.mysql.cj.jdbc.Driver");
                String url = ConfigReader.get("db.url");
                String user = ConfigReader.get("db.user");
                String password = ConfigReader.get("db.password");
                // Thiết lập kết nối
                connection = DriverManager.getConnection(url, user, password);
                System.out.println("Connect to MySQL successfully!");
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
                System.out.println("Error: Can't connect to MySQL!");
            }
        }
        return connection;
    }

    // Phương thức đóng kết nối
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                System.out.println("Đã đóng kết nối MySQL!");
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Lỗi khi đóng kết nối!");
            }
        }
    }
}
