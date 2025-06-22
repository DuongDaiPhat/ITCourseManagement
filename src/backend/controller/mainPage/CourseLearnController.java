package backend.controller.mainPage;

import backend.service.user.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import model.course.Courses;
import model.user.Users;

import java.sql.SQLException;

public class CourseLearnController {

    @FXML private Label Author;
    @FXML private Button Ratingcourse;
    @FXML private Button Wachingvideo;
    @FXML private Label courseName;
    @FXML private Label numberLecture;
    @FXML private Label rating;
    @FXML private ImageView courseImage;

    private Courses course;
    private UserService userService;

    public CourseLearnController() {
        userService = new UserService();
    }

    public void setCourse(Courses course) {
        this.course = course;
        initialize();
    }

    @FXML
    public void initialize() {
        if (course != null) {
            courseName.setText(course.getCourseName());
            numberLecture.setText("Số bài giảng: Chưa có dữ liệu");
            rating.setText("Chưa có đánh giá");
            try {
                Users courseAuthor = userService.GetUserByID(course.getUserID());
                Author.setText(courseAuthor != null ? courseAuthor.getUserFirstName() + " " + courseAuthor.getUserLastName() : "Không xác định");
            } catch (SQLException e) {
                Author.setText("Không xác định");
                System.err.println("Error fetching author: " + e.getMessage());
            }
            if (course.getThumbnailURL() != null && !course.getThumbnailURL().isEmpty()) {
                try {
                    courseImage.setImage(new Image("file:" + course.getThumbnailURL()));
                } catch (Exception e) {
                    System.err.println("Error loading course image: " + e.getMessage());
                }
            }
            Wachingvideo.setOnAction(event -> handleWatchVideo());
            Ratingcourse.setOnAction(event -> handleRateCourse());
        } else {
            System.err.println("Course is null in CourseLearnController");
        }
    }

    private void handleWatchVideo() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Chức năng xem video đang được phát triển!");
        alert.showAndWait();
    }

    private void handleRateCourse() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Chức năng đánh giá khóa học đang được phát triển!");
        alert.showAndWait();
    }
}