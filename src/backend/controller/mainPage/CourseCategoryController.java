package backend.controller.mainPage;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import model.course.Courses;
import backend.service.user.UserService;
import model.user.Users;

public class CourseCategoryController {
	
    @FXML
    private Button Addtocart;

    @FXML
    private Label Price;

    @FXML
    private Label Rating;

    @FXML
    private Label author;

    @FXML
    private Label beginner;

    @FXML
    private Label courseName;

    @FXML
    private Label numberLecture;

    @FXML
    private Label totalHours;
    
    @FXML
    private ImageView courseImage;

    private UserService userService;

    // Hàm gán dữ liệu khóa học vào giao diện
    public void setCourseData(Courses course) {
        courseName.setText(course.getCourseName());
        Price.setText(String.format("%.2f", course.getPrice()) + " VND");
        beginner.setText(course.getLevel().toString());
        // Giả sử Rating, numberLecture, totalHours chưa có dữ liệu trong Courses
        Rating.setText("Chưa có đánh giá");
        numberLecture.setText("Số bài giảng");
        totalHours.setText("Tổng giờ học");

        // Lấy thông tin tác giả
        userService = new UserService();
        try {
            Users courseAuthor = userService.GetUserByID(course.getUserID());
            if (courseAuthor != null && courseAuthor.getUserFirstName() != null && courseAuthor.getUserLastName() != null) {
                author.setText("" + courseAuthor.getUserFirstName() + " " + courseAuthor.getUserLastName());
            } else {
                author.setText("Tác giả: Không xác định");
            }
        } catch (Exception e) { // Xử lý mọi ngoại lệ, bao gồm SQLException
            author.setText("Tác giả: Không xác định");
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