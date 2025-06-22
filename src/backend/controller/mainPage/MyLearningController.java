package backend.controller.mainPage;

import backend.repository.user.MyCartRepository;
import backend.service.course.CourseService;
import utils.SimpleEventBus;
import utils.CartUpdatedEvent;
import utils.Subscribe;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.course.Courses;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MyLearningController {

    @FXML private VBox containerCourses;
    @FXML private Label pageCategory;
    @FXML private Label pageStudent;
    @FXML private TextField searchField;
    @FXML private ImageView searchIcon;

    private MyCartRepository cartRepository;
    private CourseService courseService;
    private int currentUserId;

    @FXML
    public void initialize() {
        cartRepository = new MyCartRepository();
        courseService = new CourseService();
        currentUserId = utils.UserSession.getInstance().getCurrentUserId();
        System.out.println("Registering MyLearningController with SimpleEventBus");
        SimpleEventBus.getInstance().register(this);
        loadPurchasedCourses();
    }

    @Subscribe
    public void onCartUpdated(CartUpdatedEvent event) {
        System.out.println("Cart updated event received in MyLearning, refreshing courses");
        Platform.runLater(this::loadPurchasedCourses);
    }

    private void loadPurchasedCourses() {
        try {
            ArrayList<Integer> purchasedCourseIds = cartRepository.getPurchasedCourseIdsByUserId(currentUserId);
            List<Courses> allCourses = courseService.getAllCourses();
            List<Courses> purchasedCourses = allCourses.stream()
                    .filter(course -> course != null && purchasedCourseIds.contains(course.getCourseID()))
                    .toList();
            System.out.println("Loaded " + purchasedCourses.size() + " purchased courses for userId=" + currentUserId + " in MyLearning");
            Platform.runLater(() -> displayCourses(purchasedCourses));
        } catch (SQLException e) {
            System.err.println("Error loading purchased courses in MyLearning: " + e.getMessage());
            Platform.runLater(() -> {
                containerCourses.getChildren().clear();
                Label errorLabel = new Label("Lỗi tải danh sách khóa học: " + e.getMessage());
                containerCourses.getChildren().add(errorLabel);
            });
        }
    }

    private void displayCourses(List<Courses> courses) {
        containerCourses.getChildren().clear();
        for (Courses course : courses) {
            if (course != null) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/frontend/view/mainPage/CourseLearn.fxml"));
                    Parent courseNode = loader.load();
                    CourseLearnController controller = loader.getController();
                    controller.setCourse(course);
                    containerCourses.getChildren().add(courseNode);
                    System.out.println("Added purchased course node in MyLearning: " + course.getCourseName());
                } catch (IOException e) {
                    System.err.println("Error loading course learn node in MyLearning: " + e.getMessage());
                }
            } else {
                System.err.println("Skipping null course in purchased courses in MyLearning");
            }
        }
    }

    @FXML
    void switchToMainPage(MouseEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/frontend/view/mainPage/mainPage.fxml"));
        Stage stage = (Stage) pageStudent.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setUserData(null);
        stage.show();
    }

    @FXML
    void switchToCategory(MouseEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/frontend/view/mainPage/category.fxml"));
        Stage stage = (Stage) pageCategory.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setUserData(null);
        stage.show();
    }
}