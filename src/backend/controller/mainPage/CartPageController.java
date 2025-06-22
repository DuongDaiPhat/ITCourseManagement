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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.course.Courses;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CartPageController {

    @FXML private Button Back;
    @FXML private VBox CourseContainer;
    @FXML private Button Payment;
    @FXML private Label courseNumber;
    @FXML private Label totalAmount;

    private MyCartRepository cartRepository;
    private CourseService courseService;
    private int currentUserId;
    private List<Courses> cartCourses;

    @FXML
    public void initialize() {
        cartRepository = new MyCartRepository();
        courseService = new CourseService();
        currentUserId = utils.UserSession.getInstance().getCurrentUserId();
        cartCourses = new ArrayList<>();
        System.out.println("Registering CartPageController with SimpleEventBus");
        utils.SimpleEventBus.getInstance().register(this);
        loadCartCourses();
    }

    @Subscribe
    public void onCartUpdated(CartUpdatedEvent event) {
        System.out.println("Cart updated event received, refreshing cart");
        Platform.runLater(this::loadCartCourses);
    }

    private void loadCartCourses() {
        try {
            ArrayList<Integer> courseIds = cartRepository.getCourseIdsByUserId(currentUserId);
            List<Courses> allCourses = courseService.getAllCourses();
            cartCourses = allCourses.stream()
                    .filter(course -> course != null && courseIds.contains(course.getCourseID()))
                    .toList();
            System.out.println("Loaded " + cartCourses.size() + " courses in cart for userId=" + currentUserId);
            Platform.runLater(() -> {
                displayCourses(cartCourses);
                updateSummary();
            });
        } catch (SQLException e) {
            System.err.println("Error loading cart courses: " + e.getMessage());
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Lỗi tải giỏ hàng: " + e.getMessage());
                alert.showAndWait();
            });
            cartRepository = new MyCartRepository();
            Platform.runLater(this::loadCartCourses);
        }
    }

    private void displayCourses(List<Courses> courses) {
        CourseContainer.getChildren().clear();
        for (Courses course : courses) {
            if (course != null) {
                try {
                    Parent courseNode = CourseCartController.loadWithData(course, (v) -> loadCartCourses());
                    CourseContainer.getChildren().add(courseNode);
                    System.out.println("Added cart course node: " + course.getCourseName());
                } catch (IOException e) {
                    System.err.println("Error loading cart course node: " + e.getMessage());
                }
            } else {
                System.err.println("Skipping null course in cart");
            }
        }
    }

    private void updateSummary() {
        courseNumber.setText(String.valueOf(cartCourses.size()));
        double total = cartCourses.stream()
                .filter(course -> course != null)
                .mapToDouble(Courses::getPrice)
                .sum();
        totalAmount.setText(String.format("%.2f VND", total));
    }

    @FXML
    void switchToMainPage(MouseEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/frontend/view/mainPage/mainPage.fxml"));
        Stage stage = (Stage) Back.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void refreshCart() {
        loadCartCourses();
    }
}