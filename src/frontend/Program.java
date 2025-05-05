package frontend;

import backend.controller.scene.SceneManager;
import backend.service.user.UserService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import model.user.Session;
import model.user.Users;
import utils.PasswordUtil;

public class Program extends Application{
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		SceneManager.setPrimaryStage(stage);
		Image icon = new Image(getClass().getResource("/images/logo/logoIcon.png").toExternalForm());
		stage.getIcons().add(icon);
	    SceneManager.switchScene("AiTeeCo", "/frontend/view/login/Login.fxml");
	}
}
