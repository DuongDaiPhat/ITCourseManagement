package frontend;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Program extends Application{
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		Parent root = FXMLLoader.load(this.getClass().getResource("/frontend/view/login/Login.fxml"));
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.setResizable(false);
		Image icon = new Image(this.getClass().getResource("/images/logo/logoIcon.png").toExternalForm(), 80,30, false, false);
		stage.getIcons().add(icon);
		stage.setTitle("AiTeeCo");
		stage.setOnShown(e->{
			Rectangle2D rec = Screen.getPrimary().getVisualBounds();
			stage.setX((rec.getWidth() - stage.getWidth())/2);
			stage.setY((rec.getHeight() - stage.getHeight())/2);
		});
		stage.show();
	}
}
