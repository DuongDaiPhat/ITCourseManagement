package backend.controller.InstructorMainPage;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import model.course.Courses;
import model.user.Session;
import model.user.Users;
import javafx.scene.control.Button;

import java.io.File;
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
    
    @FXML
    private Label createCourse;

    private Stage stage;
    private Scene scene;
    private CourseService courseService; // Service to fetch course data
    private UserService userService; // Service to fetch user data

    @FXML
    public void initialize() throws SQLException {
    	createCourse.setOnMouseClicked(event-> {
			try {
				ToCreateCoursePage();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
        courseService = new CourseService();
        userService = new UserService();
        loadUser();
    }
    public void loadUser() throws SQLException {
    	this.currentUser = Session.getCurrentUser();
    	this.loadUserInfo();
    	this.loadCourses();
    }
    public void CreateCoursePage(ActionEvent e) throws IOException {
    	this.ToCreateCoursePage();
    }
    private void ToCreateCoursePage() throws IOException {
    	Parent root = FXMLLoader.load(getClass().getResource("/frontend/view/instructorCreatePage/instructorCreatePage.fxml"));
		Rectangle2D rec = Screen.getPrimary().getVisualBounds();
		stage =  (Stage) usernameLabel.getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.setX((rec.getWidth() - stage.getWidth())/2);
		stage.setY((rec.getHeight() - stage.getHeight())/2);
		stage.show();
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
        ImageView courseThumbnail = (ImageView) findNodeById(courseItem, "courseThumbnail");
        Label courseNameLabel = (Label) findNodeById(courseItem, "courseNameLabel");
        Label languageLabel = (Label) findNodeById(courseItem, "languageLabel");
        Label technologyLabel = (Label) findNodeById(courseItem, "technologyLabel");
        Label levelLabel = (Label) findNodeById(courseItem, "levelLabel");
        Label categoryLabel = (Label) findNodeById(courseItem, "categoryLabel");
        Label priceLabel = (Label) findNodeById(courseItem, "priceLabel");
        Label createdDateLabel = (Label) findNodeById(courseItem, "createdDateLabel");

        // Set course data
        if (courseThumbnail != null) {
            try {
            	courseThumbnail.setImage(new Image(new File(course.getThumbnailURL()).toURI().toString()));
          
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

        if (technologyLabel != null) {
        	technologyLabel.setText(String.valueOf(course.getTechnology()));
        }
        
        if(categoryLabel != null) {
        	categoryLabel.setText(String.valueOf(course.getCategory()).replace("_", " "));
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
}
