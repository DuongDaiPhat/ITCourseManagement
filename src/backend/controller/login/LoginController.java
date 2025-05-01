package backend.controller.login;

import java.io.IOException;
import java.sql.SQLException;

import backend.service.user.LoginService;
import backend.service.user.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import model.user.Session;
import model.user.Users;

public class LoginController {
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private Button loginButton;
    @FXML
    private Label loginWarning;
    
    private Stage stage;
    private Scene scene;
    private Parent root;
    private UserService userService = new UserService();
    
    public void Login(ActionEvent e) {
        String str_username = username.getText().trim();
        String str_password = password.getText();
        
        // Log để kiểm tra đầu vào
        System.out.println("Login attempt - Username: " + str_username);
        System.out.println("Login attempt - Password: " + str_password);
        
        // Kiểm tra trường rỗng
        if (str_username.isEmpty() || str_password.isEmpty()) {
            loginWarning.setText("Username or password cannot be empty");
            loginWarning.setVisible(true);
            return;
        }
        
        try {
            // Kiểm tra username và password
            boolean isMatching = LoginService.getInstance().LoginCheck(str_username, str_password);
            
            if (isMatching) {
                // Đăng nhập thành công, lấy thông tin người dùng và lưu vào session
                Users currentUser = userService.GetUserByUsername(str_username);
                if (currentUser == null) {
                    loginWarning.setText("Error: Could not retrieve user information");
                    loginWarning.setVisible(true);
                    return;
                }
                
                Session.setCurrentUser(currentUser);
                System.out.println("Login successful for user: " + str_username + ", RoleID: " + currentUser.getRoleID());
                
                // Chuyển hướng dựa trên roleID
                FXMLLoader loader;
                if (currentUser.getRoleID() == 1) { // Instructor
                    loader = new FXMLLoader(getClass().getResource("/frontend/view/instructorMainPage/instructorMainPage.fxml"));
                    root = loader.load();
                } else if (currentUser.getRoleID() == 2) { // Student
                    loader = new FXMLLoader(getClass().getResource("/frontend/view/mainPage/mainPage.fxml"));
                    root = loader.load();
                } else {
                    loginWarning.setText("Error: Invalid role for user");
                    loginWarning.setVisible(true);
                    return;
                }
                
                // Chuyển hướng giao diện
                stage = (Stage)((Node)e.getSource()).getScene().getWindow();
                scene = new Scene(root);
                Rectangle2D rec = Screen.getPrimary().getVisualBounds();
                stage.setScene(scene);
                stage.setX((rec.getWidth() - stage.getWidth())/2);
                stage.setY((rec.getHeight() - stage.getHeight())/2);
                stage.show();
            } else {
                // Đăng nhập thất bại
                // Kiểm tra xem username có tồn tại không để hiển thị thông báo chi tiết
                boolean usernameExists = userService.GetUserByUsername(str_username) != null;
                if (!usernameExists) {
                    loginWarning.setText("Username does not exist");
                } else {
                    loginWarning.setText("Incorrect password");
                }
                loginWarning.setVisible(true);
                System.out.println("Login failed for user: " + str_username);
            }
        } catch (SQLException ex) {
            // Xử lý lỗi cơ sở dữ liệu
            loginWarning.setText("Database error. Please try again later.");
            loginWarning.setVisible(true);
            System.err.println("SQLException during login: " + ex.getMessage());
            ex.printStackTrace();
        } catch (IOException ex) {
            // Xử lý lỗi tải giao diện
            loginWarning.setText("Error loading the next page. Please try again.");
            loginWarning.setVisible(true);
            System.err.println("IOException during login: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    public void Register(ActionEvent e) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/frontend/view/register/register.fxml"));
        Rectangle2D rec = Screen.getPrimary().getVisualBounds();
        stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.setX((rec.getWidth() - stage.getWidth())/2);
        stage.setY((rec.getHeight() - stage.getHeight())/2);
        stage.show();
    }
}