package backend.controller.login;

import java.io.IOException;
import java.sql.SQLException;

import backend.service.user.LoginService;
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
	
	public void Login(ActionEvent e) throws SQLException {
		String str_username = username.getText();
		String str_password = password.getText();
		System.out.println(str_username);
		System.out.println(str_password);
		if(str_username.isEmpty() || str_password.isEmpty()) {
			loginWarning.setText("Username or password is empty");
			return;
		}
		boolean isMatching = LoginService.getInstance().LoginCheck(str_username, str_password);
		if(isMatching == true) {
			System.out.println("Login successfully");
		}
		else {
			loginWarning.setText("Wrong Username and password");
		}
	}
	public void Register(ActionEvent e) throws SQLException, IOException{
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
