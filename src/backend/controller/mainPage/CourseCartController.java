package backend.controller.mainPage;

import backend.repository.user.MyCartRepository;
import backend.service.user.UserService;
import utils.SimpleEventBus;
import utils.CartUpdatedEvent;
import utils.Subscribe;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import model.course.Courses;
import model.user.MyCart;
import model.user.Users;

import java.io.IOException;
import java.sql.SQLException;
import java.util.function.Consumer;

public class CourseCartController {

    @FXML private Label Author;
    @FXML private Label Evaluate;
    @FXML private Label Price;
    @FXML private Label courseName;
    @FXML private Hyperlink deleteCoure;
    @FXML private ImageView imageCourse;
    @FXML private Label numberLectures;

    private Courses course;
    private MyCartRepository cartRepository;
    private UserService userService;
    private Consumer<Void> refreshCallback;
    private int currentUserId;

    public CourseCartController() {
        this.cartRepository = new MyCartRepository();
        this.userService = new UserService();
        this.currentUserId = utils.UserSession.getInstance().getCurrentUserId();
    }

    public void setCourseAndCallback(Courses course, Consumer<Void> refreshCallback) {
        this.course = course;
        this.refreshCallback = refreshCallback;
        if (course == null) {
            System.err.println("Course is null in setCourseAndCallback, skipping initialization");
            return;
        }
        initialize();
    }

    @FXML
    public void initialize() {
        if (course != null) {
            System.out.println("Initializing CourseCartController for course: " + course.getCourseName());
            courseName.setText(course.getCourseName());
            Price.setText(String.format("%.2f VND", course.getPrice()));
            Evaluate.setText("Chưa có đánh giá");
            numberLectures.setText("Số bài giảng");

            try {
                Users courseAuthor = userService.GetUserByID(course.getUserID());
                Author.setText(courseAuthor != null ? courseAuthor.getUserFirstName() + " " + courseAuthor.getUserLastName() : "Không xác định");
            } catch (SQLException e) {
                Author.setText("Không xác định");
                System.err.println("Error fetching author: " + e.getMessage());
            }

            if (course.getThumbnailURL() != null && !course.getThumbnailURL().isEmpty()) {
                try {
                    imageCourse.setImage(new Image("file:" + course.getThumbnailURL()));
                } catch (Exception e) {
                    System.err.println("Error loading course image: " + e.getMessage());
                }
            }

            deleteCoure.setOnAction(event -> handleDeleteCourse());
        } else {
            System.err.println("Course is null in CourseCartController during initialization");
        }
    }

    private void handleDeleteCourse() {
        System.out.println("Delete clicked for course: " + (course != null ? course.getCourseName() : "null"));
        if (course != null) {
            try {
                MyCart cartItem = new MyCart(currentUserId, course.getCourseID(), false, null);
                int rowsAffected = cartRepository.Delete(cartItem);
                if (rowsAffected > 0) {
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Đã xóa khóa học khỏi giỏ hàng!");
                        alert.showAndWait();
                        if (refreshCallback != null) {
                            refreshCallback.accept(null);
                        }
                    });
                    utils.SimpleEventBus.getInstance().post(new utils.CartUpdatedEvent());
                } else {
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Không thể xóa khóa học khỏi giỏ hàng!");
                        alert.showAndWait();
                    });
                }
            } catch (SQLException e) {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Lỗi hệ thống khi xóa khóa học: " + e.getMessage());
                    alert.showAndWait();
                });
                System.err.println("Error deleting course from cart: " + e.getMessage());
            }
        }
    }

    public static Parent loadWithData(Courses course, Consumer<Void> refreshCallback) throws IOException {
        FXMLLoader loader = new FXMLLoader(CourseCartController.class.getResource("/frontend/view/mainPage/CourseCart.fxml"));
        Parent parent = loader.load();
        CourseCartController controller = loader.getController();
        controller.setCourseAndCallback(course, refreshCallback);
        return parent;
    }
}