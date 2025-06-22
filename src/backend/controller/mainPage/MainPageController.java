package backend.controller.mainPage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import backend.service.course.CourseService;
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
import model.course.Courses;
import model.user.Session;
import model.user.Users;

public class MainPageController {
	
    @FXML
    private ImageView Cartpage; // Thêm tham chiếu đến ImageView của giỏ hàng

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
    private CourseService courseService; // Dịch vụ để lấy danh sách khóa học

    // Hàm khởi tạo được gọi khi giao diện được tải
    @FXML
    public void initialize() {
        // Khởi tạo CourseService
        courseService = new CourseService();

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

        // Tải các khóa học đã được duyệt theo danh mục
        loadApprovedCoursesByCategory(Category.Artificial_Intelligence, aiCourseContainer);
        loadApprovedCoursesByCategory(Category.Web_Development, webCourseContainer);

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

 // Trong MainPageController.java
    @FXML
    void switchToCartPage(MouseEvent event) throws IOException {
        stopSlideshow(); // Dừng slideshow khi chuyển giao diện
        Parent root = FXMLLoader.load(getClass().getResource("/frontend/view/mainPage/CartPage.fxml"));
        Stage stage = (Stage) Cartpage.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
    
    private void stopSlideshow() {
        if (slideshowTimeline != null) {
            slideshowTimeline.stop();
        }
    }

    // Hàm lấy tên hiển thị của danh mục
    private String getDisplayName(Category category) {
        return category.name().replace('_', ' ');
    }

    // Hàm tải các khóa học đã được duyệt theo danh mục
    private void loadApprovedCoursesByCategory(Category category, HBox container) {
        try {
            // Lấy tất cả các khóa học đã được duyệt
            List<Courses> allCourses = courseService.getAllCourses();
            List<Courses> approvedCourses = allCourses.stream()
                    .filter(Courses::isApproved) // Chỉ lấy các khóa học có ISAPPROVED = true
                    .filter(course -> course.getCategory() == category) // Lọc theo danh mục
                    .toList();

            container.getChildren().clear(); // Xóa nội dung cũ trong container

            if (approvedCourses.isEmpty()) {
                Label emptyLabel = new Label("Không có khóa học nào trong danh mục này.");
                emptyLabel.setStyle("-fx-text-fill: gray; -fx-font-size: 14px;");
                container.getChildren().add(emptyLabel);
            } else {
                for (Courses course : approvedCourses) {
                    // Tải giao diện khóa học từ FXML
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/frontend/view/mainPage/courseStudent.fxml"));
                    Region courseCard = loader.load();

                    // Gán dữ liệu khóa học vào controller
                    CourseStudentController controller = loader.getController();
                    controller.setCourseData(course);

                    container.getChildren().add(courseCard);
                }
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            Label errorLabel = new Label("Lỗi khi tải khóa học: " + e.getMessage());
            errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");
            container.getChildren().clear();
            container.getChildren().add(errorLabel);
        }
    }
}