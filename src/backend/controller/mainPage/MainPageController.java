package backend.controller.mainPage;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class MainPageController {

    // ImageView icons and images
    @FXML private ImageView bellIcon, cartIcon, courseImage, courseImage1, courseImage2, 
                              courseImage3, courseImage4, courseImage5, courseImage6, 
                              courseImage7, heartIcon, leftButton, rightButton, 
                              searchIcon, sliderPane, user;

    // Labels for course details
    @FXML private Label categoryCombo, courseAuthor, courseAuthor1, courseAuthor2, 
                         courseAuthor3, courseAuthor4, courseAuthor5, courseAuthor6, 
                         courseAuthor7, courseName, courseName1, courseName14, 
                         courseName2, courseName3, courseName5, courseName6, 
                         courseName7, coursePrice, coursePrice1, coursePrice2, 
                         coursePrice3, coursePrice4, coursePrice5, coursePrice6, 
                         coursePrice7, courseRating, courseRating1, courseRating2, 
                         courseRating3, courseRating4, courseRating5, courseRating6, 
                         courseRating7, helloName, labelLogo, myLearningLabel, 
                         titleLabel, titleLabel1;

    // TextField for search input
    @FXML private TextField searchField;

<<<<<<< Updated upstream
    // Initialize method to set up default values and update course info
    public void initialize() {
        // Example: set the name of the user dynamically
        helloName.setText("Hello, John Doe!");

        // Example: set course details dynamically
 /*       courseName.setText("JavaFX for Beginners");
        courseAuthor.setText("John Smith");
        courseRating.setText("4.5/5");
        coursePrice.setText("$39.99");*/
=======
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
    
// Navigate between pages when click on its event.
    @FXML
    void switchToMyRoles(MouseEvent event) throws IOException {
    	Parent root = FXMLLoader.load(getClass().getResource("/frontend/view/register/register.fxml"));
        Stage stage = (Stage) pageRole.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    void switchToCategory(MouseEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/frontend/view/mainPage/category.fxml"));
        Stage stage = (Stage) pageCategory.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    void switchToMyLearning(MouseEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/frontend/view/mainPage/mylearning.fxml"));
        Stage stage = (Stage) pageMyLearning.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    private String getDisplayName(Category category) {
        return category.name().replace('_', ' ');
    }

    @FXML
    public void initialize() {
        // Get user information from Session
        Users currentUser = Session.getCurrentUser();
        if (currentUser != null) {
        // Assign user name to labeluser.
            String userDisplayName = currentUser.getUserFirstName() + " " + currentUser.getUserLastName();
            labeluser.setText(userDisplayName);
            System.out.println("User display name set to labeluser: " + userDisplayName);
        } else {
            labeluser.setText("Guest");
            System.err.println("No user found in Session. User must be logged in to see their name.");
        }

        // Name the category
        aiCategory.setText(getDisplayName(Category.Artificial_Intelligence));
        webCategory.setText(getDisplayName(Category.Web_Development));
>>>>>>> Stashed changes

        // Set images for the courses
        courseImage.setImage(new Image("file:assets/javafx-course.jpg"));
        courseImage1.setImage(new Image("file:assets/java-course1.jpg"));
    }
<<<<<<< Updated upstream

    // Event handler when search icon is clicked
    @FXML
    private void onSearchIconClick(MouseEvent event) {
        String query = searchField.getText();
        // You can add your search logic here, e.g., showing results in a list
        System.out.println("Searching for: " + query);
=======
       // Load lesson list from database for each category
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
>>>>>>> Stashed changes
    }

    // Event handler when the left button is clicked (e.g., to move a slider)
    @FXML
    private void onLeftButtonClick(MouseEvent event) {
        System.out.println("Left Button Clicked");
        // Implement the logic to handle the slider moving to the left
    }

    // Event handler when the right button is clicked (e.g., to move a slider)
    @FXML
    private void onRightButtonClick(MouseEvent event) {
        System.out.println("Right Button Clicked");
        // Implement the logic to handle the slider moving to the right
    }

    // Event handler when the cart icon is clicked
    @FXML
    private void onCartIconClick(MouseEvent event) {
        System.out.println("Cart Icon Clicked");
        // You can navigate to the cart or show a popup with cart details
    }

    // Event handler when the bell icon is clicked (e.g., show notifications)
    @FXML
    private void onBellIconClick(MouseEvent event) {
        System.out.println("Bell Icon Clicked");
        // Implement the logic to show notifications
    }

    // Event handler when the heart icon is clicked (e.g., to mark a course as favorite)
    @FXML
    private void onHeartIconClick(MouseEvent event) {
        System.out.println("Heart Icon Clicked");
        // Implement the logic to add the course to the favorites
    }

    // Event handler when the user icon is clicked (e.g., navigate to user profile)
    @FXML
    private void onUserClick(MouseEvent event) {
        System.out.println("User Icon Clicked");
        // Implement logic to navigate to user profile or settings
    }

    // Optionally, you can add other event handlers as needed for specific actions
}
