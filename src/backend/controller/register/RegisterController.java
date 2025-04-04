package backend.controller.register;

import java.io.IOException;
import java.sql.SQLException;

import backend.service.user.RegisterService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Screen;
import javafx.stage.Stage;
import model.user.Users;

public class RegisterController {
	private Parent root;
	private Stage stage;
	private Scene scene;
	
	@FXML
	private TextField firstName;
	@FXML
	private TextField lastName;
	@FXML
	private TextField username;
	@FXML
	private TextField email;
	@FXML
	private TextField phoneNumber;
	@FXML
	private PasswordField password;
	@FXML
	private PasswordField confirmPassword;
	@FXML
	private CheckBox checkBox;
	
	
	
	public void BackToLogin(ActionEvent e) throws IOException {
		root = FXMLLoader.load(getClass().getResource("/frontend/view/login/Login.fxml"));
		stage = (Stage)((Node)e.getSource()).getScene().getWindow();
		Rectangle2D rec = Screen.getPrimary().getVisualBounds();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.setX((rec.getWidth() - stage.getWidth())/2);
		stage.setY((rec.getHeight() - stage.getHeight())/2);
		stage.show();
	}
	public void SignIn(ActionEvent e) throws IOException, SQLException {
		String str_firstName = firstName.getText().trim();
		String str_lastName = lastName.getText().trim();
		String str_username = username.getText().trim();
		String str_email = email.getText().trim();
		String str_phoneNumber = phoneNumber.getText().trim();
		String str_password =password.getText();
		String str_confirmPassword =confirmPassword.getText();
		
		if(str_firstName.isEmpty() || str_lastName.isEmpty()) {
			System.out.println("First name or Last name is empty");
			return;
		}
		if(str_username.isEmpty()) {
			System.out.println("Username is empty");
			return;
		}
		else if(RegisterService.getInstance().isExistUsername(str_username)) {
			System.out.println("This username is existed");
			return;
		}
		if(!IsValidUsername(str_username)) {
			System.out.println("Username must contains 3-20 letter and doesn't have whitespace");
			return;
		}
		if(str_email.isEmpty()) {
			System.out.println("Email is empty");
			return;
		}
		if(!IsValidEmail(str_email)) {
			System.out.println("Invalid email");
			return;
		}
		if(str_phoneNumber.isEmpty()) {
			System.out.println("Phone number is empty");
			return;
		}
		if(!IsValidPhoneNumber(str_phoneNumber)) {
			System.out.println("Invalid phone number");
			return;
		}
		if(str_password.isEmpty()) {
			System.out.println("Password is empty");
			return;
		}
		if(!IsValidPassword(str_password)) {
			System.out.println("Invalid password");
			return;
		}
		if(str_confirmPassword.isEmpty()) {
			System.out.println("Confirm pass is empty");
			return;
		}
		if(!str_confirmPassword.equals(str_password)) {
			System.out.println("Comfirm password doesn't match your password");
			return;
		}
		if(!checkBox.isSelected()) {
			System.out.println("Please check our terms and conditions");
		}
		
		Users user = new Users();
		user.setUserFirstName(str_firstName);
		user.setUserLastName(str_lastName);
		user.setUserName(str_username);
		user.setEmail(str_email);
		user.setPhoneNumber(str_phoneNumber);
		user.setPassword(str_password);
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/frontend/view/register/selectRole.fxml"));
		root = loader.load();
		
		SelectRoleController selectRoleController = loader.getController();
		selectRoleController.SelectRoleForUser(user);
		stage = (Stage)((Node)e.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		Rectangle2D rec = Screen.getPrimary().getVisualBounds();
		stage.setX((rec.getWidth() - stage.getWidth())/2);
		stage.setY((rec.getHeight() - stage.getHeight())/2);
		stage.show();
	}
	private static boolean IsValidUsername(String username) {
		String usernameRegex = "^[a-zA-Z0-9_]{3,20}$";
		if(username.matches(usernameRegex)) return true;
		return false;
	}
	private static boolean IsValidEmail(String email) {
		String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
		if(email.matches(emailRegex)) {
			return true;
		}
		return false;
	}
	private static boolean IsValidPhoneNumber(String phoneNumber) {
		String phoneNumberRegex = "^[0-9]{10,11}$";
		if(phoneNumber.matches(phoneNumberRegex)) return true;
		return false;
	}
	private static boolean IsValidPassword(String password) {
		String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,20}$";
		if(password.matches(passwordRegex)) return true;
		return false;
	}
}
