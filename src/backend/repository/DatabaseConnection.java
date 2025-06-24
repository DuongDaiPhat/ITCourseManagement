package backend.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import utils.ConfigReader;

public class DatabaseConnection {

	private static Connection connection = null;

	// Dùng biến tĩnh đọc từ ConfigReader 1 lần
	private static final String URL = ConfigReader.get("db.url");
	private static final String USER = ConfigReader.get("db.user");
	private static final String PASSWORD = ConfigReader.get("db.password");

	// Kết nối dùng lại (singleton)
	public static Connection getConnection() {
		if (connection == null) {
			try {
				Class.forName("com.mysql.cj.jdbc.Driver");
				connection = DriverManager.getConnection(URL, USER, PASSWORD);
				System.out.println("Connect to MySQL successfully!");
			} catch (ClassNotFoundException | SQLException e) {
				e.printStackTrace();
				System.out.println("Error: Can't connect to MySQL!");
			}
		}
		return connection;
	}

	// Đóng connection nếu cần
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

	// Mỗi lần gọi tạo connection mới — tránh lỗi 'connection closed'
	public static Connection getNewConnection() throws SQLException {
		return DriverManager.getConnection(URL, USER, PASSWORD);
	}
}