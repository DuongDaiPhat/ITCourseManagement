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
import javafx.scene.control.Label;
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
    
    @FXML
    private Label firstNameError;
    @FXML
    private Label lastNameError;
    @FXML
    private Label usernameError;
    @FXML
    private Label emailError;
    @FXML
    private Label phoneNumberError;
    @FXML
    private Label passwordError;
    @FXML
    private Label confirmPasswordError;
    @FXML
    private Label termsError;
    
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
        resetErrors();
        
        String str_firstName = firstName.getText().trim();
        String str_lastName = lastName.getText().trim();
        String str_username = username.getText().trim();
        String str_email = email.getText().trim();
        String str_phoneNumber = phoneNumber.getText().trim();
        String str_password = password.getText();
        String str_confirmPassword = confirmPassword.getText();
        
        boolean isValid = true;
        
        if(str_firstName.isEmpty()) {
            firstNameError.setText("First name is empty");
            firstNameError.setVisible(true);
            firstName.getStyleClass().add("error-border");
            isValid = false;
        }
        
        if(str_lastName.isEmpty()) {
            lastNameError.setText("Last name is empty");
            lastNameError.setVisible(true);
            lastName.getStyleClass().add("error-border");
            isValid = false;
        }
        
        if(str_username.isEmpty()) {
            usernameError.setText("Username is empty");
            usernameError.setVisible(true);
            username.getStyleClass().add("error-border");
            isValid = false;
        } else if(RegisterService.getInstance().isExistUsername(str_username)) {
            usernameError.setText("This username is existed");
            usernameError.setVisible(true);
            username.getStyleClass().add("error-border");
            isValid = false;
        } else if(!IsValidUsername(str_username)) {
            usernameError.setText("Requires: 3-20 chars (A-Z, a-z, 0-9, _)");
            usernameError.setVisible(true);
            username.getStyleClass().add("error-border");
            isValid = false;
        }
        
        if(str_email.isEmpty()) {
            emailError.setText("Email is empty");
            emailError.setVisible(true);
            email.getStyleClass().add("error-border");
            isValid = false;
        } else if(!IsValidEmail(str_email)) {
            emailError.setText("Invalid email");
            emailError.setVisible(true);
            email.getStyleClass().add("error-border");
            isValid = false;
        }
        
        if(str_phoneNumber.isEmpty()) {
            phoneNumberError.setText("Phone number is empty");
            phoneNumberError.setVisible(true);
            phoneNumber.getStyleClass().add("error-border");
            isValid = false;
        } else if(!IsValidPhoneNumber(str_phoneNumber)) {
            phoneNumberError.setText("Phone number must be 10-11 digits");
            phoneNumberError.setVisible(true);
            phoneNumber.getStyleClass().add("error-border");
            isValid = false;
        }
        
        if(str_password.isEmpty()) {
            passwordError.setText("Password is empty");
            passwordError.setVisible(true);
            password.getStyleClass().add("error-border");
            isValid = false;
        } else if(!IsValidPassword(str_password)) {
            passwordError.setText("Requires: A-Z, a-z, 0-9 (8-20 chars)");
            passwordError.setVisible(true);
            password.getStyleClass().add("error-border");
            isValid = false;
        }

        if(str_confirmPassword.isEmpty()) {
            confirmPasswordError.setText("Confirm password is empty");
            confirmPasswordError.setVisible(true);
            confirmPassword.getStyleClass().add("error-border");
            isValid = false;
        } else if(!str_confirmPassword.equals(str_password)) {
            confirmPasswordError.setText("Confirm password doesn't match your password");
            confirmPasswordError.setVisible(true);
            confirmPassword.getStyleClass().add("error-border");
            isValid = false;
        }
        
        if(!checkBox.isSelected()) {
            termsError.setText("Please check our terms and conditions");
            termsError.setVisible(true);
            isValid = false;
        }
        
        if(!isValid) {
            return;
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
    
    private void resetErrors() {
        firstNameError.setVisible(false);
        lastNameError.setVisible(false);
        usernameError.setVisible(false);
        emailError.setVisible(false);
        phoneNumberError.setVisible(false);
        passwordError.setVisible(false);
        confirmPasswordError.setVisible(false);
        termsError.setVisible(false);
        
        firstName.getStyleClass().remove("error-border");
        lastName.getStyleClass().remove("error-border");
        username.getStyleClass().remove("error-border");
        email.getStyleClass().remove("error-border");
        phoneNumber.getStyleClass().remove("error-border");
        password.getStyleClass().remove("error-border");
        confirmPassword.getStyleClass().remove("error-border");
    }
    
    private static boolean IsValidUsername(String username) {
        String usernameRegex = "^[a-zA-Z0-9_]{3,20}$";
        return username.matches(usernameRegex);
    }
    
    private static boolean IsValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        return email.matches(emailRegex);
    }
    
    private static boolean IsValidPhoneNumber(String phoneNumber) {
        String phoneNumberRegex = "^[0-9]{10,11}$";
        return phoneNumber.matches(phoneNumberRegex);
    }
    
    private static boolean IsValidPassword(String password) {
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,20}$";
        return password.matches(passwordRegex);
    }
}