import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TestStudentCart extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("src/frontend/view/studentCart/StudentCart.fxml"));
            VBox root = loader.load();
            
            Scene scene = new Scene(root);
            primaryStage.setTitle("Student Cart Test");
            primaryStage.setScene(scene);
            primaryStage.show();
            
            System.out.println("StudentCart FXML loaded successfully!");
            
        } catch (Exception e) {
            System.err.println("Error loading StudentCart FXML:");
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
