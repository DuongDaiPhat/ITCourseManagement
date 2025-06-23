package backend.controller.mainPage;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import model.course.Courses;
import backend.service.user.UserService;
import model.user.Users;

public class CourseStudentController {

    @FXML
    private Label courseAuthor; // Lấy tên người tạo khóa học

    @FXML
    private ImageView courseImage;

    @FXML
    private Label courseLanguage;

    @FXML
    private Label courseName;

    @FXML
    private Label coursePrice;

    private UserService userService;

    // Hàm gán dữ liệu khóa học vào giao diện
    public void setCourseData(Courses course) {
        courseName.setText(course.getCourseName());
        courseLanguage.setText(course.getLanguage().toString());
        coursePrice.setText(String.format("%.2f", course.getPrice()) + " $");
        
        // Khởi tạo UserService để lấy thông tin tác giả
        userService = new UserService();
        try {
            Users author = userService.GetUserByID(course.getUserID()); // Sử dụng GetUserByID
            if (author != null && author.getUserFirstName() != null && author.getUserLastName() != null) {
                courseAuthor.setText("" + author.getUserFirstName() + " " + author.getUserLastName());
            } else {
                courseAuthor.setText("Không xác định");
            }
        } catch (Exception e) {
            courseAuthor.setText("Tác giả: Không xác định");
            System.err.println("Lỗi khi lấy thông tin tác giả: " + e.getMessage());
            e.printStackTrace();
        }

        // Hiển thị ảnh thumbnail nếu có
        if (course.getThumbnailURL() != null && !course.getThumbnailURL().isEmpty()) {
            try {
                Image image = new Image("file:" + course.getThumbnailURL());
                courseImage.setImage(image);
            } catch (Exception e) {
                System.err.println("Lỗi khi tải ảnh khóa học: " + course.getThumbnailURL());
                e.printStackTrace();
            }
        }
    }
}