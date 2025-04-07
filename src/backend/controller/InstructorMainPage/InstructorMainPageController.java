package backend.controller.InstructorMainPage;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.course.Courses;
import model.user.Users;
import javafx.scene.control.Button;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import backend.service.course.CourseService;
import backend.service.user.UserService;

public class InstructorMainPageController {
	private Users currentUser;
 	@FXML
    private Label usernameLabel;

    @FXML
    private VBox courseListContainer;

    @FXML
    private Label emptyCourseLabel;

    private CourseService courseService; // Service to fetch course data
    private UserService userService; // Service to fetch user data

    @FXML
    public void initialize() throws SQLException {
        // Initialize services
        courseService = new CourseService();
        userService = new UserService();
    }
    public void loadUser(int userID) throws SQLException {
    	this.currentUser = userService.GetUserByID(userID);
    	this.loadUserInfo();
    	this.loadCourses();
    }
    private void loadUserInfo() {
        if (currentUser != null) {
            usernameLabel.setText(currentUser.getUserFirstName() +" "+ currentUser.getUserLastName());
        }
    }

    private void loadCourses() throws SQLException {
        // Clear existing items
        courseListContainer.getChildren().clear();

        // Get courses for current user
        List<Courses> courses = courseService.GetCourseByUserID(currentUser.getUserID());

        if (courses == null || courses.isEmpty()) {
            // Show empty state message
            emptyCourseLabel.setVisible(true);
            courseListContainer.getChildren().add(emptyCourseLabel);
        } else {
            // Hide empty state message
            emptyCourseLabel.setVisible(false);

            // Add courses to the container
            for (Courses course : courses) {
                try {
                    // Load the course item FXML
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/frontend/view/instructorMainPage/CourseItem.fxml"));
                    HBox courseItem = loader.load();
                    Separator sp = new Separator();

                    // Configure course item
                    configureCourseItem(courseItem, course);
                    
                    // Add to container
                    courseListContainer.getChildren().add(sp);
                    courseListContainer.getChildren().add(courseItem);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void configureCourseItem(HBox courseItem, Courses course) {
        // Find all components in the loaded FXML
        ImageView courseThumbnail = (ImageView) findNodeById(courseItem, "courseThumbnail");
        Label courseNameLabel = (Label) findNodeById(courseItem, "courseNameLabel");
        Label languageLabel = (Label) findNodeById(courseItem, "languageLabel");
        Label programmingLanguageLabel = (Label) findNodeById(courseItem, "programmingLanguageLabel");
        Label levelLabel = (Label) findNodeById(courseItem, "levelLabel");
        Label priceLabel = (Label) findNodeById(courseItem, "priceLabel");
        Label createdDateLabel = (Label) findNodeById(courseItem, "createdDateLabel");

        // Set course data
        if (courseThumbnail != null) {
            try {
                courseThumbnail.setImage(new Image(course.getThumbnailURL()));
            } catch (Exception e) {
                // Use default image if thumbnail URL is invalid
            	System.out.println("bad image");
                courseThumbnail.setImage(new Image(getClass().getResourceAsStream("/images/default_image.png")));
            }
        }

        if (courseNameLabel != null) {
            courseNameLabel.setText(course.getCourseName());
        }

        if (languageLabel != null) {
            languageLabel.setText(String.valueOf(course.getLanguage()));
        }

        if (programmingLanguageLabel != null) {
            programmingLanguageLabel.setText(String.valueOf(course.getProgrammingLanguage()));
        }

        if (levelLabel != null) {
            levelLabel.setText(String.valueOf(course.getLevel()));
        }

        if (priceLabel != null) {
            if (course.getPrice() <= 0) {
                priceLabel.setText("Free");
            } else {
                priceLabel.setText(String.format("$%.2f", course.getPrice()));
            }
        }

        if (createdDateLabel != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
            createdDateLabel.setText("Created: " + course.getCreatedAt().format(formatter));
        }
    }

    private Node findNodeById(Node parent, String id) {
        if (parent.getId() != null && parent.getId().equals(id)) {
            return parent;
        }

        if (parent instanceof javafx.scene.Parent) {
            for (Node child : ((javafx.scene.Parent) parent).getChildrenUnmodifiable()) {
                Node result = findNodeById(child, id);
                if (result != null) {
                    return result;
                }
            }
        }

        return null;
    }

    @FXML
    private void goToHomePage() {
        System.out.println("Navigating to Home Page");
        // Implement navigation logic
    }

    @FXML
    private void goToExplorePage() {
        System.out.println("Navigating to Explore Page");
        // Implement navigation logic
    }

    @FXML
    private void goToMyCoursePage() {
        System.out.println("Navigating to My Course Page");
        // Implement navigation logic
    }

    @FXML
    private void goToCreateCoursePage() {
        System.out.println("Navigating to Create Course Page");
        // Implement navigation logic
    }
}
