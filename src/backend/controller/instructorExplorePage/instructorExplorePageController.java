package backend.controller.instructorExplorePage;

import backend.controller.scene.SceneManager;
import backend.service.course.CourseService;
import backend.service.user.UserService;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

public class instructorExplorePageController {
	@FXML
	private Label createCourse;
	@FXML
    private Button profileButton;

	private CourseService courseService;
	private UserService userService;
	private ContextMenu profileMenu;

	@FXML
	public void initialize() {
		courseService = new CourseService();
		userService = new UserService();
		setupProfileMenu();
        profileButton.setOnAction(event -> showProfileMenu());
	}
	private void setupProfileMenu() {
        profileMenu = new ContextMenu();
        
        MenuItem profileInfoItem = new MenuItem("My information");
        MenuItem paymentMethodItem = new MenuItem("Payment");
        MenuItem logoutItem = new MenuItem("Log out");
        
        profileInfoItem.getStyleClass().add("menu-item");
        paymentMethodItem.getStyleClass().add("menu-item");
        logoutItem.getStyleClass().add("menu-item");
        profileInfoItem.setOnAction(event -> showProfileInfo());
        paymentMethodItem.setOnAction(event -> showPaymentMethods());
        logoutItem.setOnAction(event -> logout());
        
        profileMenu.getItems().addAll(profileInfoItem, paymentMethodItem, logoutItem);
        profileMenu.getStyleClass().add("ProfileMenu.css");
	}
    
    private void showProfileMenu() {
        profileMenu.show(profileButton, profileButton.localToScreen(0, profileButton.getHeight()).getX(), 
                     profileButton.localToScreen(0, profileButton.getHeight()).getY());
    }
    
    // Methods to handle menu item actions
    private void showProfileInfo() {
        SceneManager.switchScene("My Information", "/frontend/view/UserProfile/UserProfile.fxml");
    }
    
    private void showPaymentMethods() {
        System.out.println("Opening payment methods...");
    
    }
    
    private void logout() {
        SceneManager.clearSceneCache();
        SceneManager.switchScene("Login", "/frontend/view/login/Login.fxml");
    }

}
