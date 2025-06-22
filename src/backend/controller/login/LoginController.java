package backend.controller.login;

import java.io.IOException;
import java.sql.SQLException;

import backend.controller.InstructorMainPage.InstructorMainPageController;
import backend.controller.register.SelectRoleController;
import backend.controller.scene.SceneManager;
import backend.service.user.LoginService;
import backend.service.user.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Screen;
import javafx.stage.Stage;
import model.user.Session;
import model.user.Users;

public class LoginController implements ILoginController {
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

	public void Login(ActionEvent e) throws SQLException, IOException {
		String str_username = username.getText().trim();
		String str_password = password.getText();
		System.out.println(str_username);
		System.out.println(str_password);
		if (str_username.isEmpty() || str_password.isEmpty()) {
			loginWarning.setText("Username or password is empty");
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
                if (currentUser.getRoleID() == 1) { // Instructor
                    SceneManager.switchScene("Instructor Main Page", "/frontend/view/instructorMainPage/instructorMainPage.fxml");
                } else if (currentUser.getRoleID() == 2) { // Student
                    SceneManager.switchScene("Student Main Page", "/frontend/view/studentMainPage/studentMainPage.fxml");
                } else if (Session.getCurrentUser().getRoleID() == 3) {
                	SceneManager.switchScene("Admin Main Page", "/frontend/view/admin/AdminMainPage.fxml");
                }
                else {
                	loginWarning.setText("Wrong Username and password");
                    return;
                }
            } else {
            	loginWarning.setText("Wrong Username and password");
            }
		}catch (SQLException ex) {
                loginWarning.setText("Database error. Please try again later.");
                loginWarning.setVisible(true);
                System.err.println("SQLException during login: " + ex.getMessage());
                ex.printStackTrace();
		}
	}
	public void Register(ActionEvent e) throws SQLException, IOException {
		this.ResetError();
		SceneManager.switchScene("Register", "/frontend/view/register/register.fxml");
	}
	private void ResetError() {
		loginWarning.setVisible(false);
	}
}
