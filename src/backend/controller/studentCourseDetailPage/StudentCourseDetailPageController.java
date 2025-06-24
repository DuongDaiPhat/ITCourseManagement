package backend.controller.studentCourseDetailPage;

import backend.controller.scene.SceneManager;
import backend.controller.payment.PaymentDialogController;
import backend.repository.course.CourseReviewRepository;
import backend.service.lecture.LectureService;
import backend.service.user.UserService;
import backend.service.payment.PaymentService;
import backend.service.state.StudentStateManager;
import backend.service.course.CourseReviewService;
import backend.util.ImageCache;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.course.Courses;
import model.lecture.Lecture;
import model.user.Session;
import model.user.Users;
import model.course.CourseReview;

import java.io.File;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class StudentCourseDetailPageController implements Initializable {
    
    // Navigation Elements
    @FXML private Label homeLabel;
    @FXML private Label categoryLabel;
    @FXML private Label myLearningLabel;
    @FXML private TextField searchField;
    @FXML private Button searchButton;
    @FXML private ImageView searchIcon;
    @FXML private Button cartButton;
    @FXML private ImageView cartIcon;
    @FXML private Button wishlistButton;
    @FXML private ImageView wishlistIcon;
    @FXML private Button notificationButton;
    @FXML private ImageView notificationIcon;    @FXML private Button profileButton;
    @FXML private ImageView profileIcon;
    // Cart count badge feature disabled
    
    // Course Detail Elements
    @FXML private ImageView courseThumbnail;
    @FXML private Label courseTitle;
    @FXML private Label courseDescription;
    @FXML private Label courseCategory;
    @FXML private Label courseLevel;
    @FXML private Label courseLanguage;
    @FXML private Label courseTechnology;
    @FXML private Label coursePrice;
    @FXML private Label instructorName;
    @FXML private Label createdDate;    @FXML private Label updatedDate;
    @FXML private VBox lecturesContainer;
    @FXML private VBox instructorDetails;
    
    // Student-specific action buttons
    @FXML private Button addToCartBtn;
    @FXML private Button addToWishlistBtn;
    @FXML private Button payNowBtn;
    
    // Main content container
    @FXML private VBox mainContent;
    
    // Video Player Elements
    @FXML private VBox videoOverlay;
    @FXML private StackPane videoPlayerArea;
    @FXML private HBox videoControlsBar;
    @FXML private Label videoTitle;
    @FXML private Button closeVideoBtn;
    @FXML private Label videoPlaceholder;
    @FXML private Button playPauseBtn;
    @FXML private Label currentTimeLabel;
    @FXML private Slider timeSlider;
    @FXML private Label totalTimeLabel;
    @FXML private Button speedBtn;
    @FXML private Slider volumeSlider;
    @FXML private Button volumeDownBtn;
    @FXML private Button volumeUpBtn;
    @FXML private Button fullscreenBtn;    // Rating and Review Elements
    @FXML private Label averageRatingLabel;
    @FXML private Label totalReviewsLabel;
    @FXML private Button filterAllBtn;
    @FXML private Button filter5StarBtn;
    @FXML private Button filter4StarBtn;
    @FXML private Button filter3StarBtn;
    @FXML private Button filter2StarBtn;
    @FXML private Button filter1StarBtn;
    @FXML private VBox reviewsContainer;
    @FXML private Label noReviewsLabel;
    
    // Services and Data
    private UserService userService;
    private LectureService lectureService;
    private CourseReviewService courseReviewService;
    private ContextMenu profileMenu;
    private Courses currentCourse;
    private boolean isCoursePurchased = false;
    
    // Video Player
    private MediaPlayer mediaPlayer;
    private MediaView mediaView;
    private double[] playbackSpeeds = {0.5, 0.75, 1.0, 1.25, 1.5, 2.0};
    private int currentSpeedIndex = 2; // Default 1.0x
    private boolean isPlaying = false;
    private boolean isVideoFullscreen = false;    @Override
    public void initialize(URL location, ResourceBundle resources) {
        userService = new UserService();
        lectureService = new LectureService();
        courseReviewService = new CourseReviewService();
          setupProfileMenu();
        setupNavigationEvents();
        setupIcons();
        setupStateManager();
        setupReviewFilterButtons();
        
        // Check if we have course data from Session
        Courses sessionCourse = Session.getCurrentCourse();
        if (sessionCourse != null) {
            this.currentCourse = sessionCourse;
            this.isCoursePurchased = Session.isCurrentCoursePurchased();
            
            loadCourseDetails();
            loadLectures();
            loadCourseReviews();
            
            // Wait a moment for UI to be ready, then update action buttons
            javafx.application.Platform.runLater(() -> {
                try {
                    Thread.sleep(100); // Small delay to ensure UI is ready
                    updateActionButtons();
                } catch (InterruptedException e) {
                    updateActionButtons();
                }
            });
        }
        
        // Initialize video player UI
        initializeVideoPlayer();
    }
      /**
     * Setup state manager for cart/wishlist synchronization     */
    private void setupStateManager() {
        StudentStateManager stateManager = StudentStateManager.getInstance();        // Register cart state listener 
        stateManager.addCartStateListener(cartIds -> {
            if (currentCourse != null) {
                System.out.println("Cart state changed, courseId " + currentCourse.getCourseID() + 
                    " is in cart: " + cartIds.contains(currentCourse.getCourseID()));
                javafx.application.Platform.runLater(() -> updateCartButtonState(cartIds.contains(currentCourse.getCourseID())));
            }
        });
        
        // Register wishlist state listener
        stateManager.addWishlistStateListener(wishlistIds -> {
            if (currentCourse != null) {
                System.out.println("Wishlist state changed, courseId " + currentCourse.getCourseID() + 
                    " is in wishlist: " + wishlistIds.contains(currentCourse.getCourseID()));
                javafx.application.Platform.runLater(() -> updateWishlistButtonState(wishlistIds.contains(currentCourse.getCourseID())));
            }
        });
        
        // Initialize state if user is logged in
        if (Session.getCurrentUser() != null) {
            stateManager.initializeState();        }    }
    
    // Cart count badge feature disabled
      /**
     * Method to set course data when navigating from other pages
     */
    public void setCourseData(Courses course) {
        System.out.println("setCourseData called with course: " + (course != null ? course.getCourseName() : "null"));
        this.currentCourse = course;
        
        // Ensure StateManager is initialized
        StudentStateManager stateManager = StudentStateManager.getInstance();
        if (Session.getCurrentUser() != null) {
            System.out.println("Initializing state for user: " + Session.getCurrentUser().getUserID());
            stateManager.initializeState();
            stateManager.printDebugState(); // Debug print
        } else {
            System.err.println("No current user found!");
        }
        
        // Check if this course is purchased by current user
        checkCoursePurchaseStatus();
        
        loadCourseDetails();
        loadLectures();
        loadCourseReviews();
        
        // Wait a moment for UI to be ready, then update action buttons
        javafx.application.Platform.runLater(() -> {
            try {
                Thread.sleep(100); // Small delay to ensure UI is ready
                updateActionButtons();
            } catch (InterruptedException e) {
                updateActionButtons();
            }
        });
    }
      /**
     * Method called by SceneManager.switchSceneWithRefresh() to refresh page data
     */
    public void refreshPageData() {
        System.out.println("Refreshing StudentCourseDetailPage data...");
        
        // Ensure StateManager is initialized
        StudentStateManager stateManager = StudentStateManager.getInstance();
        if (Session.getCurrentUser() != null) {
            stateManager.initializeState();
        }
        
        if (currentCourse != null) {
            checkCoursePurchaseStatus();
            updateActionButtons();
        }
        
        System.out.println("StudentCourseDetailPage data refreshed successfully");
    }
      private void setupNavigationEvents() {
        homeLabel.setOnMouseClicked(event -> goToStudentMainPage());
        categoryLabel.setOnMouseClicked(event -> goToStudentExplorePage());
        myLearningLabel.setOnMouseClicked(event -> {
            try {
                System.out.println("Navigating to My Learning Page...");
                SceneManager.switchSceneWithRefresh(
                    "My Learning",
                    "/frontend/view/myLearning/MyLearning.fxml"
                );
            } catch (Exception e) {
                System.err.println("Error navigating to My Learning Page: " + e.getMessage());
                e.printStackTrace();
                
                // Show error alert
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Navigation Error");
                alert.setHeaderText("Failed to open My Learning Page");
                alert.setContentText("Could not navigate to the My Learning page. Please try again.");
                alert.showAndWait();
            }
        });
          // Add cart button navigation
        cartButton.setOnAction(event -> {            try {
                System.out.println("Navigating to Student Cart Page...");
                SceneManager.switchSceneWithRefresh(
                    "Student Cart Page",
                    "/frontend/view/studentCart/StudentCart.fxml"
                );
            } catch (Exception e) {
                System.err.println("Error navigating to Student Cart Page: " + e.getMessage());
                e.printStackTrace();
                
                // Show error alert
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Navigation Error");
                alert.setHeaderText("Failed to open Cart Page");
                alert.setContentText("Could not navigate to the cart page. Please try again.");
                alert.showAndWait();
            }
        });
        
        // Add wishlist button navigation
        wishlistButton.setOnAction(event -> {
            try {
                System.out.println("Navigating to My Wishlist Page...");
                SceneManager.switchSceneWithRefresh(
                    "My Wishlist",
                    "/frontend/view/myWishList/MyWishList.fxml"
                );
            } catch (Exception e) {
                System.err.println("Error navigating to My Wishlist Page: " + e.getMessage());
                e.printStackTrace();
                
                // Show error alert
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Navigation Error");
                alert.setHeaderText("Failed to open Wishlist Page");
                alert.setContentText("Could not navigate to the wishlist page. Please try again.");
                alert.showAndWait();
            }
        });
    }
      private void setupIcons() {
        try {
            // Set search icon
            if (searchIcon != null) {
                Image searchImage = ImageCache.loadImage("file:resources/images/main_page/icon/WhiteMagnifier.png");
                if (searchImage != null) {
                    searchIcon.setImage(searchImage);
                }
            }
            
            // Set cart icon
            if (cartIcon != null) {
                Image cartImage = ImageCache.loadImage("file:resources/images/main_page/icon/MyCart.png");
                if (cartImage != null) {
                    cartIcon.setImage(cartImage);
                }
            }
            
            // Set wishlist icon
            if (wishlistIcon != null) {
                Image wishlistImage = ImageCache.loadImage("file:resources/images/main_page/icon/MyWishList.png");
                if (wishlistImage != null) {
                    wishlistIcon.setImage(wishlistImage);
                }
            }            
            // Set notification icon
            if (notificationIcon != null) {
                Image notificationImage = ImageCache.loadImage("file:resources/images/main_page/icon/Notification.png");
                if (notificationImage != null) {
                    notificationIcon.setImage(notificationImage);
                }
            }
            
            // Set profile icon
            if (profileIcon != null) {
                Image profileImage = ImageCache.loadImage("file:resources/images/main_page/icon/MyProfile.png");
                if (profileImage != null) {
                    profileIcon.setImage(profileImage);
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading icons: " + e.getMessage());
        }
    }
    
    private void setupProfileMenu() {
        profileMenu = new ContextMenu();
        
        MenuItem profileInfoItem = new MenuItem("My information");
        MenuItem paymentMethodItem = new MenuItem("Payment");
        MenuItem logoutItem = new MenuItem("Log out");
        
        profileInfoItem.getStyleClass().add("menu-item");
        paymentMethodItem.getStyleClass().add("menu-item");
        logoutItem.getStyleClass().add("menu-item");
        
        profileInfoItem.setOnAction(event -> showProfileInfo());
        paymentMethodItem.setOnAction(event -> showPaymentMethods());
        logoutItem.setOnAction(event -> logout());
        
        profileMenu.getItems().addAll(profileInfoItem, paymentMethodItem, logoutItem);
    }
      private void checkCoursePurchaseStatus() {
        try {
            Users currentUser = Session.getCurrentUser();
            if (currentUser != null && currentCourse != null) {
                // Check if course state is set from Session (from MyLearning page)
                if (Session.getCurrentCourse() != null && 
                    Session.getCurrentCourse().getCourseID() == currentCourse.getCourseID()) {
                    isCoursePurchased = Session.isCurrentCoursePurchased();
                    System.out.println("Course purchase status from Session: " + isCoursePurchased);
                } else {
                    // Check if course is in user's learning records
                    backend.service.user.MyLearningService learningService = new backend.service.user.MyLearningService();
                    isCoursePurchased = learningService.isInMyLearning(currentUser.getUserID(), currentCourse.getCourseID());
                    System.out.println("Course purchase status from database: " + isCoursePurchased);
                }
            } else {
                isCoursePurchased = false;
            }
        } catch (Exception e) {
            System.err.println("Error checking course purchase status: " + e.getMessage());
            isCoursePurchased = false;
        }
    }
    
    private void loadCourseDetails() {
        if (currentCourse == null) return;
        
        try {
            // Set course title and description
            courseTitle.setText(currentCourse.getCourseName());
            courseDescription.setText(currentCourse.getCourseDescription());
              // Set course properties
            courseCategory.setText(currentCourse.getCategory() != null ? currentCourse.getCategory().toString() : "N/A");
            courseLevel.setText(currentCourse.getLevel() != null ? currentCourse.getLevel().toString() : "N/A");
            courseLanguage.setText(currentCourse.getLanguage() != null ? currentCourse.getLanguage().toString() : "N/A");
            courseTechnology.setText(currentCourse.getTechnology() != null ? currentCourse.getTechnology().toString() : "N/A");
            
            // Set price
            double price = currentCourse.getPrice();
            coursePrice.setText(String.format("$%.2f", price));
            payNowBtn.setText(String.format("Pay Now - $%.2f", price));
              // Set dates
            if (currentCourse.getCreatedAt() != null) {
                createdDate.setText(currentCourse.getCreatedAt().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
            }
            if (currentCourse.getUpdatedAt() != null) {
                updatedDate.setText(currentCourse.getUpdatedAt().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
            }
              // Set instructor info
            try {
                Users instructor = userService.GetUserByID(currentCourse.getUserID());
                if (instructor != null) {
                    instructorName.setText(instructor.getUserFirstName() + " " + instructor.getUserLastName());
                    loadInstructorDetails(instructor);
                }
            } catch (Exception e) {
                instructorName.setText("Unknown Instructor");
                System.err.println("Error loading instructor info: " + e.getMessage());
            }            // Set course thumbnail
            if (currentCourse.getThumbnailURL() != null && !currentCourse.getThumbnailURL().isEmpty()) {
                try {
                    File thumbnailFile = new File(currentCourse.getThumbnailURL());
                    if (thumbnailFile.exists()) {
                        Image thumbnailImage = ImageCache.loadImage(thumbnailFile.toURI().toString());
                        if (thumbnailImage != null) {
                            courseThumbnail.setImage(thumbnailImage);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error loading course thumbnail: " + e.getMessage());
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error loading course details: " + e.getMessage());
        }
    }
    
    private void loadInstructorDetails(Users instructor) {
        instructorDetails.getChildren().clear();
          try {
            // Instructor name
            Label nameLabel = new Label(instructor.getUserFirstName() + " " + instructor.getUserLastName());
            nameLabel.getStyleClass().add("instructor-name");
            
            // Instructor email
            Label emailLabel = new Label("Email: " + instructor.getEmail());
            emailLabel.getStyleClass().add("instructor-detail");
            
            instructorDetails.getChildren().addAll(nameLabel, emailLabel);
            
        } catch (Exception e) {
            System.err.println("Error loading instructor details: " + e.getMessage());
        }
    }
    
    private void loadLectures() {
        if (currentCourse == null) return;
          try {            ArrayList<Lecture> lectures = lectureService.getLecturesByCourseID(currentCourse.getCourseID());
            
            // Clear existing lectures
            lecturesContainer.getChildren().clear();
            
            // Add each lecture
            for (int i = 0; i < lectures.size(); i++) {
                Lecture lecture = lectures.get(i);
                VBox lectureCard = createLectureCard(lecture, i + 1);
                lecturesContainer.getChildren().add(lectureCard);
            }
              } catch (Exception e) {
            System.err.println("Error loading lectures: " + e.getMessage());
        }
    }
    
    private VBox createLectureCard(Lecture lecture, int lectureNumber) {
        VBox lectureCard = new VBox(5);
        lectureCard.getStyleClass().add("lecture-card");
        
        // Lecture title with number
        Label titleLabel = new Label(lectureNumber + ". " + lecture.getLectureName());
        titleLabel.getStyleClass().add("lecture-title");
        
        // Lecture description
        Label descLabel = new Label(lecture.getLectureDescription());
        descLabel.getStyleClass().add("lecture-description");
        descLabel.setWrapText(true);
        
        // Action button container
        HBox actionBox = new HBox(10);
        actionBox.setAlignment(Pos.CENTER_LEFT);
        
        if (isCoursePurchased) {
            // Show "Watch" button for purchased courses
            Button watchBtn = new Button("Watch");
            watchBtn.getStyleClass().add("watch-btn");
            watchBtn.setOnAction(event -> playLecture(lecture));
            actionBox.getChildren().add(watchBtn);
        } else {
            // Show locked status for unpurchased courses
            Label lockedLabel = new Label("🔒 Purchase required");
            lockedLabel.getStyleClass().add("locked-label");
            actionBox.getChildren().add(lockedLabel);
        }
        
        lectureCard.getChildren().addAll(titleLabel, descLabel, actionBox);
        
        return lectureCard;
    }    private void updateActionButtons() {
        try {
            Users currentUser = Session.getCurrentUser();
            if (currentUser == null || currentCourse == null) {
                System.out.println("Cannot update action buttons: currentUser=" + currentUser + ", currentCourse=" + currentCourse);
                return;
            }
            
            int courseId = currentCourse.getCourseID();
            StudentStateManager stateManager = StudentStateManager.getInstance();
            
            // Force state refresh to ensure we have the latest data
            System.out.println("DEBUG: updateActionButtons - forcing state refresh for course " + courseId);
            stateManager.forceStateRefresh();
            
            // Check if course is in cart using StateManager
            boolean isInCart = stateManager.isInCart(courseId);
            System.out.println("Course " + courseId + " is in cart: " + isInCart);
            updateCartButtonState(isInCart);
            
            // Check if course is in wishlist using StateManager
            boolean isInWishlist = stateManager.isInWishlist(courseId);
            System.out.println("Course " + courseId + " is in wishlist: " + isInWishlist);
            updateWishlistButtonState(isInWishlist);
            
            // Update button states based on purchase status
            if (isCoursePurchased) {
                System.out.println("Course is already purchased, disabling action buttons");
                addToCartBtn.setDisable(true);
                addToCartBtn.setText("Already Owned");
                addToWishlistBtn.setDisable(true);
                addToWishlistBtn.setText("Already Owned");
                payNowBtn.setDisable(true);
                payNowBtn.setText("Already Owned");
            } else {
                System.out.println("Course not purchased, enabling action buttons");
                addToCartBtn.setDisable(false);
                addToWishlistBtn.setDisable(false);
                payNowBtn.setDisable(false);
            }
            
        } catch (Exception e) {
            System.err.println("Error updating action buttons: " + e.getMessage());
            e.printStackTrace();
        }
    }private void updateCartButtonState(boolean isInCart) {
        System.out.println("Updating cart button state: isInCart = " + isInCart);
        javafx.application.Platform.runLater(() -> {
            if (addToCartBtn != null) {
                if (isInCart) {
                    addToCartBtn.setText("Remove from Cart");
                    addToCartBtn.getStyleClass().removeAll("cart-action-btn");
                    addToCartBtn.getStyleClass().add("cart-remove-btn");
                    System.out.println("Cart button set to: Remove from Cart with style: cart-remove-btn");
                } else {
                    addToCartBtn.setText("Add to Cart");
                    addToCartBtn.getStyleClass().removeAll("cart-remove-btn");
                    addToCartBtn.getStyleClass().add("cart-action-btn");
                    System.out.println("Cart button set to: Add to Cart with style: cart-action-btn");
                }
                System.out.println("Current button style classes: " + addToCartBtn.getStyleClass());
            } else {
                System.err.println("addToCartBtn is null!");
            }
        });
    }
    
    private void updateWishlistButtonState(boolean isInWishlist) {
        javafx.application.Platform.runLater(() -> {
            if (isInWishlist) {
                addToWishlistBtn.setText("Remove from Wishlist");
                addToWishlistBtn.getStyleClass().remove("wishlist-action-btn");
                addToWishlistBtn.getStyleClass().add("wishlist-remove-btn");
            } else {
                addToWishlistBtn.setText("Add to Wishlist");
                addToWishlistBtn.getStyleClass().remove("wishlist-remove-btn");
                addToWishlistBtn.getStyleClass().add("wishlist-action-btn");
            }
        });
    }    @FXML
    public void handleAddToCart() {
        System.out.println("handleAddToCart called");
        if (isCoursePurchased) {
            System.out.println("Course is already purchased, skipping cart action");
            return;
        }
        
        try {
            if (currentCourse == null) {
                System.err.println("Current course is null!");
                return;
            }
            
            StudentStateManager stateManager = StudentStateManager.getInstance();
            int courseId = currentCourse.getCourseID();
            
            // Force state refresh before checking
            System.out.println("DEBUG: Forcing state refresh before cart action...");
            stateManager.forceStateRefresh();
            
            // Wait a moment for state to be refreshed
            try {
                Thread.sleep(100); // Small delay to ensure state is refreshed
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            boolean isCurrentlyInCart = stateManager.isInCart(courseId);
            System.out.println("handleAddToCart: courseId=" + courseId + ", isCurrentlyInCart=" + isCurrentlyInCart);
              if (isCurrentlyInCart) {
                // Remove from cart
                System.out.println("Attempting to remove from cart...");
                boolean removed = stateManager.removeFromCart(courseId);
                if (removed) {
                    System.out.println("Successfully removed " + currentCourse.getCourseName() + " from cart");
                    // StateManager will notify listeners automatically
                } else {
                    System.err.println("Failed to remove course from cart");
                }
            } else {
                // Add to cart
                System.out.println("Attempting to add to cart...");
                boolean added = stateManager.addToCart(courseId);
                if (added) {
                    System.out.println("Successfully added " + currentCourse.getCourseName() + " to cart");
                    // StateManager will notify listeners automatically
                } else {
                    System.err.println("Failed to add course to cart");
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error handling cart action: " + e.getMessage());
            e.printStackTrace();
        }
    }@FXML
    public void handleAddToWishlist() {
        if (isCoursePurchased) return;
        
        try {
            if (currentCourse == null) return;
            
            StudentStateManager stateManager = StudentStateManager.getInstance();
            int courseId = currentCourse.getCourseID();
            
            boolean isCurrentlyInWishlist = stateManager.isInWishlist(courseId);              if (isCurrentlyInWishlist) {
                // Remove from wishlist
                boolean removed = stateManager.removeFromWishlist(courseId);
                if (removed) {
                    System.out.println("Successfully removed " + currentCourse.getCourseName() + " from wishlist");
                    // Force UI update
                    javafx.application.Platform.runLater(() -> updateWishlistButtonState(false));
                } else {
                    System.err.println("Failed to remove course from wishlist");
                }
            } else {
                // Add to wishlist
                boolean added = stateManager.addToWishlist(courseId);
                if (added) {
                    System.out.println("Successfully added " + currentCourse.getCourseName() + " to wishlist");
                    // Force UI update
                    javafx.application.Platform.runLater(() -> updateWishlistButtonState(true));
                } else {
                    System.err.println("Failed to add course to wishlist");
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error handling wishlist action: " + e.getMessage());
        }
    }
    
    @FXML
    public void handlePayNow() {
        if (isCoursePurchased) return;
        
        try {
            Users currentUser = Session.getCurrentUser();
            if (currentUser == null || currentCourse == null) {
                showAlert("Error", "Please login to make a purchase.");
                return;
            }
            
            showPaymentDialog();
            
        } catch (Exception e) {
            System.err.println("Error processing payment: " + e.getMessage());
            showAlert("Error", "An error occurred while processing payment.");
        }
    }
    
    @FXML
    public void handleSearch() {
        String searchText = searchField.getText();
        if (searchText != null && !searchText.trim().isEmpty()) {
            // Navigate to explore page with search
            goToStudentExplorePageWithSearch(searchText.trim());
        }
    }
    
    // Navigation Methods
    private void goToStudentMainPage() {
        try {
            SceneManager.switchSceneWithRefresh("studentMainPage", "/frontend/view/studentMainPage/studentMainPage.fxml");
        } catch (Exception e) {
            System.err.println("Error navigating to student main page: " + e.getMessage());
        }
    }
    
    private void goToStudentExplorePage() {
        try {
            SceneManager.switchSceneWithRefresh("studentExplorePage", "/frontend/view/studentExplorePage/StudentExplorePage.fxml");
        } catch (Exception e) {
            System.err.println("Error navigating to student explore page: " + e.getMessage());
        }
    }
    
    private void goToStudentExplorePageWithSearch(String searchText) {
        try {
            SceneManager.switchScene("studentExplorePage", "/frontend/view/studentExplorePage/StudentExplorePage.fxml");
            // TODO: Pass search text to explore page
        } catch (Exception e) {
            System.err.println("Error navigating to student explore page with search: " + e.getMessage());
        }
    }      // Profile Menu Methods    @FXML
    public void showProfileMenu() {
        if (profileMenu != null && profileButton != null) {
            // Calculate the position to show menu below and aligned with the profile button
            // Get the button's bounds in the scene
            Bounds bounds = profileButton.localToScene(profileButton.getBoundsInLocal());
            
            // Position menu below the button, aligned to the right edge of the button
            double menuX = bounds.getMinX() - 120; // Offset to align menu properly
            double menuY = bounds.getMaxY() + 5;   // Position below the button
            
            profileMenu.show(profileButton.getScene().getWindow(), menuX, menuY);
        }
    }
      private void showProfileInfo() {
        try {
            SceneManager.switchScene("userProfile", "/frontend/view/userProfile/UserProfile.fxml");
        } catch (Exception e) {
            System.err.println("Error showing profile info: " + e.getMessage());
            showAlert("Error", "Could not load profile information.");
        }
    }
    
    private void showPaymentMethods() {
        try {
            // TODO: Implement payment methods view
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Payment Methods");
            alert.setHeaderText("Payment");
            alert.setContentText("Payment methods view will be implemented here.");
            alert.showAndWait();
        } catch (Exception e) {
            System.err.println("Error showing payment methods: " + e.getMessage());
        }    }    private void logout() {
        try {
            Session.setCurrentUser(null);
            SceneManager.switchToLoginScene("login", "/frontend/view/login/Login.fxml");
        } catch (Exception e) {
            System.err.println("Error during logout: " + e.getMessage());
        }
    }
    
    // Payment Methods
    private void showPaymentDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/frontend/view/payment/PaymentDialog.fxml"));
            Parent root = loader.load();
            
            PaymentDialogController paymentController = loader.getController();
            paymentController.setPaymentData(Session.getCurrentUser(), currentCourse);
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Payment");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setResizable(false);
            dialogStage.setScene(new Scene(root));
            
            dialogStage.showAndWait();
            
            // Check if payment was completed
            if (paymentController.isPaymentCompleted()) {
                PaymentService.PaymentResult result = paymentController.getPaymentResult();
                processCourseUnlock(result);
            }
            
        } catch (Exception e) {
            System.err.println("Error showing payment dialog: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Could not open payment dialog.");
        }
    }
    
    private void processCourseUnlock(PaymentService.PaymentResult paymentResult) {
        try {
            Users currentUser = Session.getCurrentUser();
            
            // Process course unlock
            boolean unlocked = PaymentService.getInstance().processCourseUnlock(
                currentUser, currentCourse, paymentResult.getTransactionId()
            );
            
            if (unlocked) {
                // Update course status
                isCoursePurchased = true;
                
                // Update UI
                updateActionButtons();
                updateLectureAccess();
                  // Remove from cart if present
                StudentStateManager stateManager = StudentStateManager.getInstance();
                stateManager.removeFromCart(currentCourse.getCourseID());
                
                // Show confirmation
                showAlert("Success", 
                    "Course purchased successfully!\n" +
                    "Transaction ID: " + paymentResult.getTransactionId() + "\n" +
                    "You now have full access to all lectures.");
                
                System.out.println("Course unlocked successfully for user: " + currentUser.getUserName());
            }
            
        } catch (Exception e) {
            System.err.println("Error processing course unlock: " + e.getMessage());
            showAlert("Error", "Payment completed but there was an issue unlocking the course. Please contact support.");
        }
    }
    
    private void updateLectureAccess() {
        // Refresh the lectures display to show unlocked status
        loadLectures();
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Video Player Methods
    private void initializeVideoPlayer() {
        // Hide video overlay by default
        if (videoOverlay != null) {
            videoOverlay.setVisible(false);
            videoOverlay.setManaged(false);
        }
        
        // Initialize volume slider
        if (volumeSlider != null) {
            volumeSlider.setValue(1.0);
        }
    }
    
    private void playLecture(Lecture lecture) {
        if (!isCoursePurchased) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Access Denied");
            alert.setHeaderText("Course Not Purchased");
            alert.setContentText("Please purchase this course to access the lectures.");
            alert.showAndWait();
            return;
        }
          try {
            String videoPath = lecture.getVideoURL();
            if (videoPath != null && !videoPath.isEmpty()) {
                File videoFile = new File(videoPath);
                if (videoFile.exists()) {
                    showVideoPlayer(lecture);
                } else {
                    System.err.println("Video file not found: " + videoPath);
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Video Error");
                    alert.setHeaderText("Video Not Found");
                    alert.setContentText("The video file for this lecture could not be found.");
                    alert.showAndWait();
                }
            } else {
                System.err.println("No video path specified for lecture: " + lecture.getLectureName());
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Video Unavailable");
                alert.setHeaderText("No Video");
                alert.setContentText("This lecture does not have a video associated with it.");
                alert.showAndWait();
            }
        } catch (Exception e) {
            System.err.println("Error playing lecture: " + e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Playback Error");
            alert.setHeaderText("Video Playback Failed");
            alert.setContentText("An error occurred while trying to play the video.");
            alert.showAndWait();
        }
    }
    
    private void showVideoPlayer(Lecture lecture) {
        try {
            // Clean up existing media player
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.dispose();
            }
            
            // Clear video player area
            videoPlayerArea.getChildren().clear();
              // Create new media and player
            File videoFile = new File(lecture.getVideoURL());
            Media media = new Media(videoFile.toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            
            // Create media view
            mediaView = new MediaView(mediaPlayer);
            mediaView.setPreserveRatio(true);
            
            // Set video title
            videoTitle.setText(lecture.getLectureName());
            
            // Add media view to player area
            videoPlayerArea.getChildren().add(mediaView);
            
            // Show video overlay
            mainContent.setDisable(true);
            videoOverlay.setVisible(true);
            videoOverlay.setManaged(true);
            isVideoFullscreen = true;
            
            // Bind media view size to container
            mediaView.fitWidthProperty().bind(videoPlayerArea.widthProperty());
            mediaView.fitHeightProperty().bind(videoPlayerArea.heightProperty());
            
            // Hide placeholder
            videoPlaceholder.setVisible(false);
            
            // Setup media player event handlers
            setupMediaPlayerEvents();
            
            // Auto-play
            mediaPlayer.play();
            isPlaying = true;
            playPauseBtn.setText("⏸");
            
        } catch (Exception e) {
            System.err.println("Error showing video player: " + e.getMessage());
            closeVideo();
        }
    }
    
    private void setupMediaPlayerEvents() {
        if (mediaPlayer == null) return;
        
        try {
            // Duration change handler
            mediaPlayer.setOnReady(() -> {
                Duration duration = mediaPlayer.getTotalDuration();
                if (totalTimeLabel != null) {
                    totalTimeLabel.setText(formatDuration(duration));
                }
                if (timeSlider != null) {
                    timeSlider.setMax(duration.toSeconds());
                }
            });
            
            // Current time handler
            mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
                if (currentTimeLabel != null) {
                    currentTimeLabel.setText(formatDuration(newTime));
                }
                if (timeSlider != null && !timeSlider.isValueChanging()) {
                    timeSlider.setValue(newTime.toSeconds());
                }
            });
            
            // Volume binding
            if (volumeSlider != null) {
                mediaPlayer.volumeProperty().bind(volumeSlider.valueProperty());
            }
            
            // Time slider handler
            if (timeSlider != null) {
                timeSlider.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
                    if (!isChanging) {
                        mediaPlayer.seek(Duration.seconds(timeSlider.getValue()));
                    }
                });
            }
            
        } catch (Exception e) {
            System.err.println("Error setting up media player events: " + e.getMessage());
        }
    }
    
    private String formatDuration(Duration duration) {
        if (duration == null) return "00:00";
        
        int minutes = (int) duration.toMinutes();
        int seconds = (int) (duration.toSeconds() % 60);
        return String.format("%02d:%02d", minutes, seconds);
    }
      @FXML
    public void closeVideo() {
        try {
            // Stop and dispose media player
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.dispose();
                mediaPlayer = null;
            }
            
            // Clear media view
            if (mediaView != null) {
                mediaView = null;
            }
            
            // Hide video overlay
            videoOverlay.setVisible(false);
            videoOverlay.setManaged(false);
            mainContent.setDisable(false);
            isVideoFullscreen = false;
            isPlaying = false;
            
            // Show placeholder again
            videoPlaceholder.setVisible(true);
            
            // Reset UI
            playPauseBtn.setText("▶");
            currentTimeLabel.setText("00:00");
            totalTimeLabel.setText("00:00");
            if (timeSlider != null) {
                timeSlider.setValue(0);
            }
            
        } catch (Exception e) {
            System.err.println("Error closing video: " + e.getMessage());
        }
    }
      @FXML
    public void togglePlayPause() {
        if (mediaPlayer == null) return;
        
        try {
            if (isPlaying) {
                mediaPlayer.pause();
                playPauseBtn.setText("▶");
                isPlaying = false;
            } else {
                mediaPlayer.play();
                playPauseBtn.setText("⏸");
                isPlaying = true;
            }
        } catch (Exception e) {
            System.err.println("Error toggling play/pause: " + e.getMessage());
        }
    }
      @FXML
    public void setPlaybackSpeed() {
        if (mediaPlayer == null) return;
        
        try {
            // Cycle through speeds
            currentSpeedIndex = (currentSpeedIndex + 1) % playbackSpeeds.length;
            double newSpeed = playbackSpeeds[currentSpeedIndex];
            
            mediaPlayer.setRate(newSpeed);
            speedBtn.setText(newSpeed + "x");
            
        } catch (Exception e) {
            System.err.println("Error setting playback speed: " + e.getMessage());
        }
    }    @FXML
    private void decreaseVolume() {
        if (mediaPlayer == null) return;
        
        double currentVolume = volumeSlider.getValue();
        double newVolume = Math.max(0.0, currentVolume - 0.1);
        volumeSlider.setValue(newVolume);
        mediaPlayer.setVolume(newVolume);
    }
    
    @FXML
    private void increaseVolume() {
        if (mediaPlayer == null) return;
        
        double currentVolume = volumeSlider.getValue();
        double newVolume = Math.min(1.0, currentVolume + 0.1);
        volumeSlider.setValue(newVolume);
        mediaPlayer.setVolume(newVolume);
    }

    @FXML
    private void toggleFullscreen() {
        if (mediaPlayer == null || mediaView == null) return;
        
        if (isVideoFullscreen) {
            // Exit video fullscreen - restore original size binding
            mediaView.fitWidthProperty().bind(videoPlayerArea.widthProperty().multiply(0.90));
            mediaView.fitHeightProperty().bind(videoPlayerArea.heightProperty().multiply(0.85));
            
            // Show video controls and title
            videoControlsBar.setVisible(true);
            videoTitle.setVisible(true);
            
            // Remove fullscreen CSS class
            videoOverlay.getStyleClass().remove("video-fullscreen-mode");
            
            // Update button text
            fullscreenBtn.setText("⛶");
            isVideoFullscreen = false;
        } else {
            // Enter video fullscreen - make MediaView fill most of the available space while leaving room for controls
            mediaView.fitWidthProperty().unbind();
            mediaView.fitHeightProperty().unbind();
            
            // Bind MediaView to fill the video overlay but leave space for controls at bottom
            mediaView.fitWidthProperty().bind(videoOverlay.widthProperty().multiply(0.90));
            mediaView.fitHeightProperty().bind(videoOverlay.heightProperty().multiply(0.70)); // Leave more space for controls at bottom
            
            // Ensure MediaView is centered in the StackPane
            StackPane.setAlignment(mediaView, javafx.geometry.Pos.CENTER);
            
            // Center the MediaView in fullscreen
            mediaView.setPreserveRatio(true);
            
            // Keep video controls visible but hide title in fullscreen for more space
            videoControlsBar.setVisible(true);
            videoControlsBar.setManaged(true);
            
            // Add fullscreen CSS class for special styling
            if (!videoOverlay.getStyleClass().contains("video-fullscreen-mode")) {
                videoOverlay.getStyleClass().add("video-fullscreen-mode");
            }
            
            // Update button text
            fullscreenBtn.setText("⧉");
            isVideoFullscreen = true;
        }
    }    private void loadCourseReviews() {
        if (currentCourse == null) return;
        
        // Null check for FXML elements
        if (averageRatingLabel == null || totalReviewsLabel == null || reviewsContainer == null) {
            System.err.println("Rating/Review FXML elements not initialized properly");
            return;
        }
        
        try {
            int courseId = currentCourse.getCourseID();
            double avgRating = courseReviewService.getCourseAverageRating(courseId);
            int reviewCount = courseReviewService.getCourseReviewCount(courseId);
            ArrayList<CourseReviewRepository.CourseReviewWithName> reviewsWithNames = courseReviewService.getCourseReviewsWithNames(courseId);
              averageRatingLabel.setText(String.format("%.1f", avgRating));
            totalReviewsLabel.setText("(" + reviewCount + " reviews)");
            displayReviewsWithNames(reviewsWithNames);
            
            // Set "All" filter button as active by default
            setActiveFilterButton(0);
        } catch (Exception e) {
            System.err.println("Error loading course reviews: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void displayReviewsWithNames(ArrayList<CourseReviewRepository.CourseReviewWithName> reviewsWithNames) {
        if (reviewsContainer == null) return;
        
        reviewsContainer.getChildren().clear();
        if (reviewsWithNames == null || reviewsWithNames.isEmpty()) {
            if (noReviewsLabel != null) {
                noReviewsLabel.setVisible(true);
            }
            return;
        }
        if (noReviewsLabel != null) {
            noReviewsLabel.setVisible(false);
        }
          for (CourseReviewRepository.CourseReviewWithName reviewWithName : reviewsWithNames) {
            CourseReview review = reviewWithName.getReview();
            String reviewerName = reviewWithName.getReviewerName();
            
            VBox reviewBox = new VBox();
            reviewBox.getStyleClass().add("review-item");
            
            // Reviewer name (first line)
            Label reviewerLabel = new Label(reviewerName);
            reviewerLabel.getStyleClass().add("reviewer-name");
            
            // Date (second line)
            Label dateLabel = new Label(review.getCreatedAt().toLocalDate().toString());
            dateLabel.getStyleClass().add("review-date");
            
            // Rating stars (third line)
            HBox ratingStars = new HBox();
            ratingStars.getStyleClass().add("review-rating-stars");
            String starsText = "★".repeat(review.getRating()) + "☆".repeat(5 - review.getRating());
            Label starsLabel = new Label(starsText);
            starsLabel.getStyleClass().add("review-star");
            ratingStars.getChildren().add(starsLabel);
            
            // Review comment (fourth line)
            Label commentLabel = new Label(review.getComment());
            commentLabel.getStyleClass().add("review-comment");
              reviewBox.getChildren().addAll(reviewerLabel, dateLabel, ratingStars, commentLabel);
            reviewsContainer.getChildren().add(reviewBox);
        }
    }
    
    private void setupReviewFilterButtons() {
        if (filterAllBtn != null) filterAllBtn.setOnAction(e -> filterReviews(0));
        if (filter5StarBtn != null) filter5StarBtn.setOnAction(e -> filterReviews(5));
        if (filter4StarBtn != null) filter4StarBtn.setOnAction(e -> filterReviews(4));
        if (filter3StarBtn != null) filter3StarBtn.setOnAction(e -> filterReviews(3));
        if (filter2StarBtn != null) filter2StarBtn.setOnAction(e -> filterReviews(2));
        if (filter1StarBtn != null) filter1StarBtn.setOnAction(e -> filterReviews(1));
    }    private void filterReviews(int star) {
        if (currentCourse == null) return;
        int courseId = currentCourse.getCourseID();
        ArrayList<CourseReviewRepository.CourseReviewWithName> allReviewsWithNames = courseReviewService.getCourseReviewsWithNames(courseId);
        ArrayList<CourseReviewRepository.CourseReviewWithName> filtered = new ArrayList<>();
        if (star == 0) {
            filtered = allReviewsWithNames;
        } else {
            for (CourseReviewRepository.CourseReviewWithName rWithName : allReviewsWithNames) {
                if (rWithName.getReview().getRating() == star) filtered.add(rWithName);
            }
        }
        displayReviewsWithNames(filtered);        setActiveFilterButton(star);
    }
    
    private void setActiveFilterButton(int star) {
        if (filterAllBtn != null) filterAllBtn.getStyleClass().remove("active");
        if (filter5StarBtn != null) filter5StarBtn.getStyleClass().remove("active");
        if (filter4StarBtn != null) filter4StarBtn.getStyleClass().remove("active");
        if (filter3StarBtn != null) filter3StarBtn.getStyleClass().remove("active");
        if (filter2StarBtn != null) filter2StarBtn.getStyleClass().remove("active");
        if (filter1StarBtn != null) filter1StarBtn.getStyleClass().remove("active");
        switch (star) {
            case 5: if (filter5StarBtn != null) filter5StarBtn.getStyleClass().add("active"); break;
            case 4: if (filter4StarBtn != null) filter4StarBtn.getStyleClass().add("active"); break;
            case 3: if (filter3StarBtn != null) filter3StarBtn.getStyleClass().add("active"); break;
            case 2: if (filter2StarBtn != null) filter2StarBtn.getStyleClass().add("active"); break;
            case 1: if (filter1StarBtn != null) filter1StarBtn.getStyleClass().add("active"); break;
            default: if (filterAllBtn != null) filterAllBtn.getStyleClass().add("active");
        }
    }
}
