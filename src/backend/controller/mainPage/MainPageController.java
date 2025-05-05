package backend.controller.mainPage;

import java.io.IOException;
import java.util.List;

import backend.repository.course.LessonRepository;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import model.course.Category;
import model.course.Lesson;
import model.user.Session;
import model.user.Users;

public class MainPageController {

    @FXML
    private Label aiCategory;

    @FXML
    private HBox aiCourseContainer;

    @FXML
    private HBox webCourseContainer;

    @FXML
    private Label pageCategory;

    @FXML
    private Label pageMyLearning;

    @FXML
    private TextField searchField;

    @FXML
    private ImageView searchIcon;

    @FXML
    private Label webCategory;
    
    @FXML
    private Label labeluser;
    
    @FXML
    private ImageView pageRole;
    
    @FXML
    void switchToMyRoles(MouseEvent event) throws IOException {
    	Parent root = FXMLLoader.load(getClass().getResource("/frontend/view/register/register.fxml"));
        Stage stage = (Stage) pageRole.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    void switchToCategory(MouseEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/frontend/view/mainPage/category.fxml"));
        Stage stage = (Stage) pageCategory.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    void switchToMyLearning(MouseEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/frontend/view/mainPage/mylearning.fxml"));
        Stage stage = (Stage) pageMyLearning.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    private String getDisplayName(Category category) {
        return category.name().replace('_', ' ');
    }

    @FXML
    public void initialize() {
        // Get user information from Session
        Users currentUser = Session.getCurrentUser();
        if (currentUser != null) {
        // Assign user name to labeluser.
            String userDisplayName = currentUser.getUserFirstName() + " " + currentUser.getUserLastName();
            labeluser.setText(userDisplayName);
            System.out.println("User display name set to labeluser: " + userDisplayName);
        } else {
            labeluser.setText("Guest");
            System.err.println("No user found in Session. User must be logged in to see their name.");
        }

        // Name the category
        aiCategory.setText(getDisplayName(Category.Artificial_Intelligence));
        webCategory.setText(getDisplayName(Category.Web_Development));

        loadCoursesByCategory(Category.Artificial_Intelligence, aiCourseContainer);
        loadCoursesByCategory(Category.Web_Development, webCourseContainer);
    }
       // Load lesson list from database for each category
    private void loadCoursesByCategory(Category category, HBox container) {
        List<Lesson> lessons = new LessonRepository().getLessonsByCategory(category);

        try {
            for (Lesson lesson : lessons) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/frontend/view/mainPage/lesson.fxml"));
                Region lessonCard = loader.load();

                LessonItemController controller = loader.getController();
                controller.setLesson(lesson);

                container.getChildren().add(lessonCard);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

