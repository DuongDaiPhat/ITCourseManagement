package backend.dao;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
	private static final String URL = "jdbc:mysql://localhost:3306/it_course_management";
    private static final String USER = "root";
    private static final String PASSWORD = "Phatdaiduong521@";
    private static Connection connection = null;

    // Phương thức lấy kết nối
    public static Connection getConnection() {
        if (connection == null) {
            try {
                // Đăng ký MySQL Driver 
                Class.forName("com.mysql.cj.jdbc.Driver");
                
                // Thiết lập kết nối
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Kết nối MySQL thành công!");
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
                System.out.println("Lỗi kết nối MySQL!");
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
