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
		String str_username = username.getText();
		String str_password = password.getText();
		System.out.println(str_username);
		System.out.println(str_password);
		if (str_username.isEmpty() || str_password.isEmpty()) {
			loginWarning.setText("Username or password is empty");
			return;
		}
		boolean isMatching = LoginService.getInstance().LoginCheck(str_username, str_password);
		if (isMatching == true) {
			Session.setCurrentUser(userService.GetUserByUsername(str_username));
			if (Session.getCurrentUser().getRoleID() == 1) {
				SceneManager.switchScene("Instructor Main Page",
						"/frontend/view/instructorMainPage/instructorMainPage.fxml");
			} else if (Session.getCurrentUser().getRoleID() == 2) {
				// Xử lý cho role 2 nếu cần
			} else if (Session.getCurrentUser().getRoleID() == 3) {
				SceneManager.switchScene("Admin Main Page", "/frontend/view/admin/AdminMainPage.fxml");
			}
		} else {
			loginWarning.setText("Wrong Username and password");
		}
	}

	public void Register(ActionEvent e) throws SQLException, IOException {
		SceneManager.switchScene("Register", "/frontend/view/register/register.fxml");
	}
}