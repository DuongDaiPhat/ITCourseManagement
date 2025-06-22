package backend.controller.myWishList;

import backend.controller.scene.SceneManager;
import backend.service.user.UserService;
import backend.service.state.StudentStateManager;
import backend.repository.user.MyWishListRepository;
import backend.repository.course.CourseRepository;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.course.Courses;
import model.user.Session;
import model.user.Users;
import model.user.MyWishList;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.ArrayList;
import java.sql.SQLException;

public class MyWishListController implements Initializable {
    
    // Navigation Elements
    @FXML private Label homeLabel;
    @FXML private Label categoryLabel;
    @FXML private Label myLearningLabel;
    @FXML private TextField searchField;
    @FXML private Button searchButton;
    @FXML private ImageView searchIcon;    @FXML private Button cartButton;
    @FXML private ImageView cartIcon;
    @FXML private Label wishlistLabel;
    @FXML private ImageView wishlistIcon;
    @FXML private Button notificationButton;
    @FXML private ImageView notificationIcon;
    @FXML private Button profileButton;
    @FXML private ImageView profileIcon;
      // Main Content Elements
    @FXML private ScrollPane wishlistItemsScrollPane;
    @FXML private Label coursesCountLabel;
    @FXML private VBox wishlistCoursesContainer;
    @FXML private Button browseCoursesBtn;
    @FXML private Button goToCartBtn;
    @FXML private Button myLearningBtn;// Services and Repositories
    private UserService userService;
    private MyWishListRepository wishListRepository;
    private CourseRepository courseRepository;
    private ContextMenu profileMenu;    @Override
    public void initialize(URL location, ResourceBundle resources) {
        userService = new UserService();
        wishListRepository = MyWishListRepository.getInstance();
        courseRepository = CourseRepository.getInstance();
        
        setupProfileMenu();
        setupNavigationEvents();
        setupIcons();
        setupStateManager();
        loadWishlistCourses();
    }
    
    /**
     * Setup state manager for cart/wishlist synchronization
     */
    private void setupStateManager() {
        StudentStateManager stateManager = StudentStateManager.getInstance();
        
        // Register wishlist state listener to refresh page when wishlist changes
        stateManager.addWishlistStateListener(wishlistIds -> {
            System.out.println("Wishlist state changed, refreshing MyWishList page");
            javafx.application.Platform.runLater(() -> loadWishlistCourses());
        });
        
        // Initialize state if user is logged in
        if (Session.getCurrentUser() != null) {
            stateManager.initializeState();
        }
    }    private void setupNavigationEvents() {
        homeLabel.setOnMouseClicked(event -> goToStudentMainPage());
        categoryLabel.setOnMouseClicked(event -> goToStudentExplorePage());
        myLearningLabel.setOnMouseClicked(event -> goToMyLearning());
        
        // Add navigation button actions
        cartButton.setOnAction(event -> goToStudentCart());
    }
    
    private void setupIcons() {
        // Load icons with error handling using file: protocol
        loadIconSafely(searchIcon, "file:resources/images/main_page/icon/WhiteMagnifier.png");
        loadIconSafely(cartIcon, "file:resources/images/main_page/icon/MyCart.png");
        loadIconSafely(wishlistIcon, "file:resources/images/main_page/icon/MyWishList.png");
        loadIconSafely(notificationIcon, "file:resources/images/main_page/icon/Notification.png");
        loadIconSafely(profileIcon, "file:resources/images/main_page/icon/MyProfile.png");
    }
      private void loadIconSafely(ImageView imageView, String iconPath) {
        try {
            Image icon = new Image(iconPath);
            if (icon != null && !icon.isError()) {
                imageView.setImage(icon);
            }
        } catch (Exception e) {
            System.err.println("Failed to load icon: " + iconPath + " - " + e.getMessage());
        }
    }
      private void setupProfileMenu() {
        profileMenu = new ContextMenu();
        
        MenuItem profileItem = new MenuItem("My Profile");
        profileItem.setOnAction(e -> goToUserProfile());
        
        MenuItem logoutItem = new MenuItem("Logout");
        logoutItem.setOnAction(e -> logout());
        
        profileMenu.getItems().addAll(profileItem, new SeparatorMenuItem(), logoutItem);
    }    private void loadWishlistCourses() {
        try {
            wishlistCoursesContainer.getChildren().clear();
            
            Users currentUser = Session.getCurrentUser();
            if (currentUser == null) {
                showEmptyWishlistState("Please log in to view your wishlist");
                return;
            }
            
            // Get wishlist items from database
            ArrayList<MyWishList> wishlistItems = wishListRepository.SelectByUserID(currentUser.getUserID());
            
            if (wishlistItems.isEmpty()) {
                showEmptyWishlistState("Your wishlist is empty");
                coursesCountLabel.setText("0 courses in wishlist");
                return;
            }
            
            // Get course details for each wishlist item
            ArrayList<Courses> wishlistCourses = new ArrayList<>();
            for (MyWishList item : wishlistItems) {
                try {
                    Courses course = courseRepository.SelectByID(item.getCourseID());
                    if (course != null) {
                        wishlistCourses.add(course);
                    }
                } catch (SQLException e) {
                    System.err.println("Error fetching course with ID " + item.getCourseID() + ": " + e.getMessage());
                }
            }
            
            // Update UI with wishlist courses
            coursesCountLabel.setText(wishlistCourses.size() + " course" + (wishlistCourses.size() != 1 ? "s" : "") + " in wishlist");
            
            // Create course cards
            for (Courses course : wishlistCourses) {
                VBox courseCard = createWishlistCourseCard(course);
                wishlistCoursesContainer.getChildren().add(courseCard);
            }
            
            System.out.println("MyWishList loaded successfully with " + wishlistCourses.size() + " courses for user: " + currentUser.getUserID());
            
        } catch (SQLException e) {
            System.err.println("Database error loading wishlist courses: " + e.getMessage());
            e.printStackTrace();
            showAlert("Database Error", "Failed to load wishlist courses. Please try again.");
            showEmptyWishlistState("Error loading wishlist");
        } catch (Exception e) {
            System.err.println("Error loading wishlist courses: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Failed to load wishlist courses. Please try again.");
            showEmptyWishlistState("Error loading wishlist");
        }
    }
      private VBox createWishlistCourseCard(Courses course) {
        VBox itemContainer = new VBox();
        itemContainer.getStyleClass().add("learning-course-item"); // Use same style as MyLearning
        
        HBox mainContent = new HBox();
        mainContent.setSpacing(20);
        mainContent.setAlignment(Pos.CENTER_LEFT);
        
        // Course Image (same as MyLearning)
        ImageView courseImage = new ImageView();
        courseImage.getStyleClass().add("learning-course-image");
        courseImage.setFitWidth(150);
        courseImage.setFitHeight(100);
        courseImage.setPreserveRatio(true);
        
        // Load course image
        try {
            String imagePath = course.getThumbnailURL();
            if (imagePath != null && !imagePath.isEmpty()) {
                Image image = new Image("file:" + imagePath);
                if (image != null && !image.isError()) {
                    courseImage.setImage(image);
                } else {
                    setDefaultCourseImage(courseImage);
                }
            } else {
                setDefaultCourseImage(courseImage);
            }
        } catch (Exception e) {
            setDefaultCourseImage(courseImage);
        }
        
        // Course Details (same structure as MyLearning)
        VBox courseDetails = new VBox();
        courseDetails.getStyleClass().add("learning-course-content");
        javafx.scene.layout.HBox.setHgrow(courseDetails, javafx.scene.layout.Priority.ALWAYS);
        
        Label titleLabel = new Label(course.getCourseName());
        titleLabel.getStyleClass().add("learning-course-title");
        
        String instructorName = getInstructorName(course.getUserID());
        Label instructorLabel = new Label("By " + instructorName);
        instructorLabel.getStyleClass().add("learning-course-instructor");
        
        Label descriptionLabel = new Label(course.getCourseDescription());
        descriptionLabel.getStyleClass().add("learning-course-description");
        
        // Course attributes section (same as MyLearning)
        HBox attributesBox = new HBox();
        attributesBox.setSpacing(15);
        attributesBox.getStyleClass().add("learning-course-attributes");
        
        Label categoryLabel = new Label("Category: " + (course.getCategory() != null ? course.getCategory().toString().replace("_", " ") : "N/A"));
        categoryLabel.getStyleClass().add("learning-course-attribute");
        
        Label levelLabel = new Label("Level: " + (course.getLevel() != null ? course.getLevel().toString() : "N/A"));
        levelLabel.getStyleClass().add("learning-course-attribute");
        
        Label languageLabel = new Label("Language: " + (course.getLanguage() != null ? course.getLanguage().toString() : "N/A"));
        languageLabel.getStyleClass().add("learning-course-attribute");
        
        Label technologyLabel = new Label("Technology: " + (course.getTechnology() != null ? course.getTechnology().toString() : "N/A"));
        technologyLabel.getStyleClass().add("learning-course-attribute");
        
        attributesBox.getChildren().addAll(categoryLabel, levelLabel, languageLabel, technologyLabel);
        
        courseDetails.getChildren().addAll(titleLabel, instructorLabel, descriptionLabel, attributesBox);
          // Action Section (price and remove button)
        VBox actionSection = new VBox();
        actionSection.getStyleClass().add("learning-course-actions");
        
        // Price (blue text, no background)
        Label priceLabel = new Label("$" + String.format("%.2f", course.getPrice()));
        priceLabel.getStyleClass().add("course-price-text");
        
        // Remove from Wishlist Button
        Button removeButton = new Button("Remove from Wishlist");
        removeButton.getStyleClass().add("remove-wishlist-btn");
        removeButton.setOnAction(event -> {
            event.consume(); // Prevent course card click
            handleRemoveFromWishlist(course);
        });
        
        actionSection.getChildren().addAll(priceLabel, removeButton);
        
        mainContent.getChildren().addAll(courseImage, courseDetails, actionSection);
        itemContainer.getChildren().add(mainContent);
        
        // Make the course card clickable (except for the remove button)
        itemContainer.setOnMouseClicked(event -> {
            if (event.getTarget() != removeButton && event.getClickCount() == 1) {
                navigateToStudentCourseDetail(course);
            }
        });
        
        return itemContainer;
    }
      private void setDefaultCourseImage(ImageView imageView) {
        try {
            Image defaultImage = new Image("file:resources/images/course_default.png");
            if (defaultImage != null && !defaultImage.isError()) {
                imageView.setImage(defaultImage);
            } else {
                // If even default image fails, create a placeholder
                imageView.setStyle("-fx-background-color: #e0e0e0;");
            }
        } catch (Exception e) {
            // If even default image fails, create a placeholder
            imageView.setStyle("-fx-background-color: #e0e0e0;");
        }
    }
      private String getInstructorName(int instructorId) {
        try {
            Users instructor = userService.GetUserByID(instructorId);
            return instructor != null ? (instructor.getUserFirstName() + " " + instructor.getUserLastName()) : "Unknown Instructor";
        } catch (Exception e) {
            return "Unknown Instructor";
        }
    }
      private void showEmptyWishlistState(String message) {
        VBox emptyContainer = new VBox();
        emptyContainer.getStyleClass().add("empty-wishlist-container");
        emptyContainer.setAlignment(Pos.CENTER);
        emptyContainer.setSpacing(20);
        
        Label iconLabel = new Label("♡");
        iconLabel.getStyleClass().add("empty-wishlist-icon");
        
        Label titleLabel = new Label("Your Wishlist is Empty");
        titleLabel.getStyleClass().add("empty-wishlist-title");
        
        Label messageLabel = new Label(message + "\nDiscover amazing courses and add them to your wishlist!");
        messageLabel.getStyleClass().add("empty-wishlist-message");
        
        Button browseButton = new Button("Browse Courses");
        browseButton.getStyleClass().add("browse-courses-btn");
        browseButton.setOnAction(event -> goToStudentExplorePage());
        
        emptyContainer.getChildren().addAll(iconLabel, titleLabel, messageLabel, browseButton);
        wishlistCoursesContainer.getChildren().add(emptyContainer);
    }
      private void handleRemoveFromWishlist(Courses course) {
        try {
            Users currentUser = Session.getCurrentUser();
            if (currentUser == null) {
                showAlert("Error", "Please log in to manage your wishlist.");
                return;
            }
            
            // Show confirmation dialog
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Remove from Wishlist");
            confirmAlert.setHeaderText("Remove Course from Wishlist");
            confirmAlert.setContentText("Are you sure you want to remove \"" + course.getCourseName() + "\" from your wishlist?");
            
            if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                // Create MyWishList object for deletion
                MyWishList wishlistItem = new MyWishList();
                wishlistItem.setUserID(currentUser.getUserID());
                wishlistItem.setCourseID(course.getCourseID());
                
                // Remove from database
                int result = wishListRepository.Delete(wishlistItem);
                  if (result > 0) {
                    showAlert("Success", "Course removed from wishlist successfully!");
                    loadWishlistCourses(); // Refresh the list
                    
                    // Update state manager if available
                    try {
                        StudentStateManager.getInstance().initializeState();
                    } catch (Exception e) {
                        System.err.println("Error updating state manager: " + e.getMessage());
                    }
                } else {
                    showAlert("Error", "Failed to remove course from wishlist. Please try again.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error removing from wishlist: " + e.getMessage());
            showAlert("Database Error", "A database error occurred while removing from wishlist. Please try again.");
        } catch (Exception e) {
            System.err.println("Error removing from wishlist: " + e.getMessage());
            showAlert("Error", "An error occurred while removing from wishlist. Please try again.");
        }
    }
    
    @FXML
    public void handleSearch() {
        String searchText = searchField.getText().trim();
        if (!searchText.isEmpty()) {
            goToStudentExplorePageWithSearch(searchText);
        } else {
            goToStudentExplorePage();
        }
    }
      // Public FXML navigation methods
    @FXML
    public void goToStudentExplorePage() {
        try {
            SceneManager.switchSceneWithRefresh(
                "Explore Courses", 
                "/frontend/view/studentExplorePage/StudentExplorePage.fxml"
            );
        } catch (Exception e) {
            System.err.println("Error navigating to Student Explore Page: " + e.getMessage());
        }
    }
    
    @FXML
    public void goToStudentCart() {
        try {
            SceneManager.switchSceneWithRefresh(
                "Shopping Cart", 
                "/frontend/view/studentCart/StudentCart.fxml"
            );
        } catch (Exception e) {
            System.err.println("Error navigating to Student Cart: " + e.getMessage());
        }
    }
    
    @FXML
    public void goToMyLearning() {
        try {
            SceneManager.switchSceneWithRefresh(
                "My Learning", 
                "/frontend/view/myLearning/MyLearning.fxml"
            );
        } catch (Exception e) {
            System.err.println("Error navigating to My Learning: " + e.getMessage());
        }
    }    // Navigation Methods
    private void goToStudentMainPage() {
        try {
            SceneManager.switchSceneWithRefresh(
                "Student Main Page", 
                "/frontend/view/studentMainPage/studentMainPage.fxml"
            );
        } catch (Exception e) {
            System.err.println("Error navigating to Student Main Page: " + e.getMessage());
        }
    }
      private void goToStudentExplorePageWithSearch(String searchText) {
        try {
            // Navigate directly without setting search query for now
            SceneManager.switchSceneWithRefresh(
                "Explore Courses", 
                "/frontend/view/studentExplorePage/StudentExplorePage.fxml"
            );
        } catch (Exception e) {
            System.err.println("Error navigating to Student Explore Page with search: " + e.getMessage());
        }
    }
      private void navigateToStudentCourseDetail(Courses course) {
        try {
            // Set course data in session for StudentCourseDetailPage
            Session.setCurrentCourse(course, false); // Wishlist courses are not purchased
            
            SceneManager.switchSceneWithRefresh(
                "Course Details", 
                "/frontend/view/studentCourseDetailPage/StudentCourseDetailPage.fxml"
            );
        } catch (Exception e) {
            System.err.println("Error navigating to course detail: " + e.getMessage());
            showAlert("Error", "Failed to open course details. Please try again.");
        }
    }
    
    // Profile Menu Methods
    @FXML
    public void showProfileMenu() {
        try {
            if (profileMenu != null) {
                profileMenu.show(profileButton, javafx.geometry.Side.BOTTOM, 0, 0);
            }
        } catch (Exception e) {
            System.err.println("Error showing profile menu: " + e.getMessage());
        }
    }
      private void logout() {
        try {
            Session.setCurrentUser(null);
            SceneManager.switchScene("/frontend/view/startPage/startPage.fxml", "AiTeeCo - Learning Platform");
        } catch (Exception e) {
            System.err.println("Error during logout: " + e.getMessage());
        }
    }
      /**
     * Method to be called when returning to this page from another page
     * to ensure wishlist state is synchronized
     */
    public void refreshPageData() {
        System.out.println("Refreshing MyWishList page data...");
        
        // Ensure StateManager is initialized
        StudentStateManager stateManager = StudentStateManager.getInstance();
        if (Session.getCurrentUser() != null) {
            stateManager.initializeState();
        }
        
        loadWishlistCourses();
        System.out.println("MyWishList page data refreshed successfully");
    }    
    // Navigation Methods
    private void goToUserProfile() {
        try {
            SceneManager.switchScene("/frontend/view/userProfile/UserProfile.fxml", "AiTeeCo - My Profile");
        } catch (Exception e) {
            System.err.println("Error navigating to user profile: " + e.getMessage());
            showAlert("Navigation Error", "Could not open profile page. Please try again.");
        }
    }
    
    private void showAlert(String title, String message) {
        try {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        } catch (Exception e) {
            System.err.println("Error showing alert: " + e.getMessage());
        }
    }
}
