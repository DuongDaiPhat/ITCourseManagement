package frontend;

import backend.controller.scene.SceneManager;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

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
