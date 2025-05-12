package backend.controller.mainPage;

import java.io.IOException;
import java.util.List;

import backend.repository.course.LessonRepository;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.util.Duration;
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
    private ScrollPane myScrollPane;
    
    @FXML
    private ImageView imageView; // ImageView để hiển thị ảnh
    
    @FXML
    private ImageView nextImageView; // Nút để chuyển sang ảnh tiếp theo
    
    @FXML
    private ImageView prevImageView; // Nút để quay lại ảnh trước đó

    // Mảng chứa đường dẫn của các ảnh
    private final String[] imagePaths = {
        "/images/main_page/images/images1.jpeg",
        "/images/main_page/images/images2.jpeg",
        "/images/main_page/images/images3.jpeg"
    };
    
    private int currentImageIndex = 0; // Chỉ số ảnh hiện tại
    private Timeline slideshowTimeline; // Timeline để chạy ảnh tự động

    // Hàm khởi tạo được gọi khi giao diện được tải
    @FXML
    public void initialize() {
        // Lấy thông tin người dùng từ Session
        Users currentUser = Session.getCurrentUser();
        if (currentUser != null) {
            String userDisplayName = currentUser.getUserFirstName() + " " + currentUser.getUserLastName();
            labeluser.setText(userDisplayName);
            System.out.println("Tên hiển thị của người dùng đã được gán vào labeluser: " + userDisplayName);
        } else {
            labeluser.setText("Khách");
            System.err.println("Không tìm thấy người dùng trong Session. Người dùng phải đăng nhập để thấy tên của họ.");
        }

        // Đặt tên cho danh mục
        aiCategory.setText(getDisplayName(Category.Artificial_Intelligence));
        webCategory.setText(getDisplayName(Category.Web_Development));

        loadCoursesByCategory(Category.Artificial_Intelligence, aiCourseContainer);
        loadCoursesByCategory(Category.Web_Development, webCourseContainer);

        // Tải ảnh đầu tiên vào imageView
        loadImage(currentImageIndex);

        // Khởi tạo và chạy slideshow tự động (mỗi 3 giây)
        startSlideshow();
    }

    // Hàm tải ảnh dựa trên chỉ số
    private void loadImage(int index) {
        try {
            Image image = new Image(getClass().getResourceAsStream(imagePaths[index]));
            imageView.setImage(image);
        } catch (Exception e) {
            System.err.println("Lỗi khi tải ảnh: " + imagePaths[index]);
            e.printStackTrace();
        }
    }

    // Hàm bắt đầu slideshow tự động
    private void startSlideshow() {
        slideshowTimeline = new Timeline(
            new KeyFrame(Duration.seconds(3), event -> {
                currentImageIndex++;
                if (currentImageIndex >= imagePaths.length) {
                    currentImageIndex = 0; // Quay lại ảnh đầu nếu hết ảnh
                }
                loadImage(currentImageIndex);
            })
        );
        slideshowTimeline.setCycleCount(Timeline.INDEFINITE); // Chạy vô hạn
        slideshowTimeline.play(); // Bắt đầu chạy
    }

    // Hàm dừng slideshow khi nhấn nút (tùy chọn, nếu muốn)
    private void stopSlideshow() {
        if (slideshowTimeline != null) {
            slideshowTimeline.stop();
        }
    }

    // Hàm xử lý sự kiện khi nhấn nút "Next" (ảnh tiếp theo)
    @FXML
    void nextImage(MouseEvent event) {
        stopSlideshow(); // Dừng slideshow tự động
        currentImageIndex++;
        if (currentImageIndex >= imagePaths.length) {
            currentImageIndex = 0; // Quay lại ảnh đầu
        }
        loadImage(currentImageIndex);
        startSlideshow(); // Tiếp tục slideshow sau khi nhấn
    }

    // Hàm xử lý sự kiện khi nhấn nút "Previous" (ảnh trước đó)
    @FXML
    void prevImage(MouseEvent event) {
        stopSlideshow(); // Dừng slideshow tự động
        currentImageIndex--;
        if (currentImageIndex < 0) {
            currentImageIndex = imagePaths.length - 1; // Quay lại ảnh cuối
        }
        loadImage(currentImageIndex);
        startSlideshow(); // Tiếp tục slideshow sau khi nhấn
    }

    // Hàm chuyển đổi sang giao diện My Roles
    @FXML
    void switchToMyRoles(MouseEvent event) throws IOException {
        stopSlideshow(); // Dừng slideshow khi chuyển giao diện
        Parent root = FXMLLoader.load(getClass().getResource("/frontend/view/register/register.fxml"));
        Stage stage = (Stage) pageRole.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    // Hàm chuyển đổi sang giao diện Category
    @FXML
    void switchToCategory(MouseEvent event) throws IOException {
        stopSlideshow(); // Dừng slideshow khi chuyển giao diện
        Parent root = FXMLLoader.load(getClass().getResource("/frontend/view/mainPage/category.fxml"));
        Stage stage = (Stage) pageCategory.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    // Hàm chuyển đổi sang giao diện My Learning
    @FXML
    void switchToMyLearning(MouseEvent event) throws IOException {
        stopSlideshow(); // Dừng slideshow khi chuyển giao diện
        Parent root = FXMLLoader.load(getClass().getResource("/frontend/view/mainPage/mylearning.fxml"));
        Stage stage = (Stage) pageMyLearning.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    // Hàm lấy tên hiển thị của danh mục
    private String getDisplayName(Category category) {
        return category.name().replace('_', ' ');
    }

    // Hàm tải danh sách bài học theo danh mục
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