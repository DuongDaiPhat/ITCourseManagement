package backend.controller.studentMainPage;

import backend.controller.scene.SceneManager;
import backend.service.course.CourseService;
import backend.service.user.UserService;
import backend.service.course.CourseReviewService;
import backend.service.state.StudentStateManager;
import backend.util.ImageCache;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.course.Courses;
import model.user.Users;
import model.user.Session;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class StudentMainPageController implements Initializable {    @FXML private Label homeLabel;
    @FXML private Label categoryLabel;
    @FXML private Label myLearningLabel;
    @FXML private TextField searchField;
    @FXML private Button searchButton;    @FXML private Button cartButton;
    @FXML private Button wishlistButton;
    @FXML private Button notificationButton;
    @FXML private Button profileButton;
    
    // Icon ImageViews
    @FXML private ImageView searchIcon;
    @FXML private ImageView cartIcon;
    @FXML private ImageView wishlistIcon;
    @FXML private ImageView notificationIcon;
    @FXML private ImageView profileIcon;
    
    @FXML private Label welcomeLabel;    @FXML private ScrollPane mainScrollPane;
      // Course containers
    @FXML private HBox aiCoursesContainer;
    @FXML private HBox gameCoursesContainer;
    
    // View All links  
    @FXML private Label aiViewAllLink;
    @FXML private Label gameViewAllLink;private ContextMenu profileMenu;
    private CourseService courseService;
    private UserService userService;
    private CourseReviewService courseReviewService;
    private backend.service.user.MyLearningService myLearningService;
    
    // Cart badge feature disabled    @Override
    public void initialize(URL location, ResourceBundle resources) {        courseService = new CourseService();
        userService = new UserService();
        courseReviewService = new CourseReviewService();
        myLearningService = new backend.service.user.MyLearningService();
        
        setupProfileMenu();
        setupNavigationEvents();
        setupWelcomeMessage();
        setupIcons();
        setupCartBadge();
        setupStateManager();
        loadCourses();
        
        // Refresh page data after all components are loaded
        javafx.application.Platform.runLater(() -> {
            refreshCourseStates();
        });
    }
      /**
     * Setup state manager for cart/wishlist synchronization
     */
    private void setupStateManager() {
        StudentStateManager stateManager = StudentStateManager.getInstance();
        
        // Register cart and wishlist state listeners for course card updates
        stateManager.addCartStateListener(cartIds -> {
            System.out.println("StudentMainPage: Cart state changed - " + cartIds);
            javafx.application.Platform.runLater(() -> {
                updateCourseCardStates(cartIds, stateManager.getWishlistCourseIds());
            });
        });
        
        stateManager.addWishlistStateListener(wishlistIds -> {
            System.out.println("StudentMainPage: Wishlist state changed - " + wishlistIds);
            javafx.application.Platform.runLater(() -> {
                updateCourseCardStates(stateManager.getCartCourseIds(), wishlistIds);
            });
        });
        
        // Initialize state if user is logged in
        if (model.user.Session.getCurrentUser() != null) {
            stateManager.initializeState();
        }
    }
    
    /**
     * Update course card states based on cart and wishlist data
     */
    private void updateCourseCardStates(java.util.Set<Integer> cartIds, java.util.Set<Integer> wishlistIds) {
        // Update AI courses container
        updateContainerCourseStates(aiCoursesContainer, cartIds, wishlistIds);
        
        // Update Game Development courses container  
        updateContainerCourseStates(gameCoursesContainer, cartIds, wishlistIds);
    }
    
    /**
     * Update course states for a specific container
     */    private void updateContainerCourseStates(HBox container, java.util.Set<Integer> cartIds, java.util.Set<Integer> wishlistIds) {
        if (container == null) return;
        
        System.out.println("Updating container course states - Cart IDs: " + cartIds + ", Wishlist IDs: " + wishlistIds);
        
        for (javafx.scene.Node node : container.getChildren()) {
            if (node instanceof VBox) {
                VBox courseCard = (VBox) node;
                // Find course ID from the card's user data
                Integer courseId = (Integer) courseCard.getUserData();
                if (courseId != null) {
                    boolean isInCart = cartIds.contains(courseId);
                    boolean isInWishlist = wishlistIds.contains(courseId);
                    System.out.println("Updating course " + courseId + " - In cart: " + isInCart + ", In wishlist: " + isInWishlist);
                    updateCourseCardIcons(courseCard, isInCart, isInWishlist);
                } else {
                    System.err.println("Course card missing userData (courseId)");
                }
            }
        }
    }
      // Cart count badge feature disabled
    
    /**
     * Update cart and wishlist icon states for a specific course card
     */
    private void updateCourseCardIcons(VBox courseCard, boolean isInCart, boolean isInWishlist) {
        try {
            // Find the actions container (HBox) in the course card
            for (javafx.scene.Node node : courseCard.getChildren()) {
                if (node instanceof VBox) { // Course info container
                    VBox courseInfo = (VBox) node;
                    for (javafx.scene.Node infoNode : courseInfo.getChildren()) {
                        if (infoNode instanceof HBox && infoNode.getStyleClass().contains("course-card-actions")) {
                            HBox actionsContainer = (HBox) infoNode;
                            
                            // Find cart and wishlist buttons
                            Button cartButton = null;
                            Button wishlistButton = null;
                            
                            for (javafx.scene.Node actionNode : actionsContainer.getChildren()) {
                                if (actionNode instanceof Button) {
                                    Button btn = (Button) actionNode;
                                    if (btn.getStyleClass().contains("cart-icon-default") || btn.getStyleClass().contains("cart-icon-active")) {
                                        cartButton = btn;
                                    } else if (btn.getStyleClass().contains("wishlist-icon-default") || btn.getStyleClass().contains("wishlist-icon-active")) {
                                        wishlistButton = btn;
                                    }
                                }
                            }
                            
                            // Update icon states
                            if (cartButton != null) {
                                updateCartIconState(cartButton, isInCart);
                            }
                            if (wishlistButton != null) {
                                updateWishlistIconState(wishlistButton, isInWishlist);
                            }
                            return; // Found and updated, exit early
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error updating course card icons: " + e.getMessage());
        }
    }
    private void setupNavigationEvents() {
        homeLabel.setOnMouseClicked(event -> {
            // Already on home page
        });
          categoryLabel.setOnMouseClicked(event -> {
            try {
                System.out.println("Navigating to Student Explore Page...");
                SceneManager.switchSceneWithRefresh(
                    "Student Explore Page",
                    "/frontend/view/studentExplorePage/StudentExplorePage.fxml"
                );
            } catch (Exception e) {
                System.err.println("Error navigating to Student Explore Page: " + e.getMessage());
                e.printStackTrace();
                
                // Show error alert
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Navigation Error");
                alert.setHeaderText("Failed to open Category Page");
                alert.setContentText("Could not navigate to the category page. Please try again.");                alert.showAndWait();
            }
        });
        
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
        cartButton.setOnAction(event -> {
            try {
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
          // Add View All link navigation
        if (aiViewAllLink != null) {
            aiViewAllLink.setOnMouseClicked(event -> {
                try {
                    System.out.println("Navigating to Student Explore Page with AI filter...");
                    // Set category filter for AI & Machine Learning
                    SceneManager.setPendingCategoryFilter("AI_ML");
                    SceneManager.switchSceneWithRefresh(
                        "Student Explore Page",
                        "/frontend/view/studentExplorePage/StudentExplorePage.fxml"
                    );
                } catch (Exception e) {
                    System.err.println("Error navigating to Student Explore Page: " + e.getMessage());
                    e.printStackTrace();
                    
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Navigation Error");
                    alert.setHeaderText("Failed to open Explore Page");
                    alert.setContentText("Could not navigate to the explore page. Please try again.");
                    alert.showAndWait();
                }
            });
        }
        
        if (gameViewAllLink != null) {
            gameViewAllLink.setOnMouseClicked(event -> {
                try {
                    System.out.println("Navigating to Student Explore Page with Game Development filter...");
                    // Set category filter for Game Development
                    SceneManager.setPendingCategoryFilter("Game_Development");
                    SceneManager.switchSceneWithRefresh(
                        "Student Explore Page",
                        "/frontend/view/studentExplorePage/StudentExplorePage.fxml"
                    );
                } catch (Exception e) {
                    System.err.println("Error navigating to Student Explore Page: " + e.getMessage());
                    e.printStackTrace();
                    
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Navigation Error");
                    alert.setHeaderText("Failed to open Explore Page");
                    alert.setContentText("Could not navigate to the explore page. Please try again.");
                    alert.showAndWait();
                }
            });
        }
    }
    
    private void setupProfileMenu() {
        profileMenu = new ContextMenu();
        
        MenuItem profileItem = new MenuItem("My Profile");
        profileItem.setOnAction(e -> showProfile());
        
        MenuItem settingsItem = new MenuItem("Settings");
        settingsItem.setOnAction(e -> showSettings());
        
        MenuItem logoutItem = new MenuItem("Logout");
        logoutItem.setOnAction(e -> logout());
        
        profileMenu.getItems().addAll(profileItem, settingsItem, new SeparatorMenuItem(), logoutItem);
    }    private void setupWelcomeMessage() {
        try {
            // Try to get current user from session or service
            String userName = getCurrentUserName();
            welcomeLabel.setText("Hello, " + userName);        } catch (Exception e) {
            // Fallback to default message
            welcomeLabel.setText("Hello, Student");
        }
    }

    private String getCurrentUserName() {
        try {
            // Get current user from Session
            Users currentUser = Session.getCurrentUser();
            if (currentUser != null) {
                System.out.println("DEBUG: Getting name for user ID: " + currentUser.getUserID());
                
                // Load user profile image
                loadUserProfileImage(currentUser.getUserID());
                
                String firstName = currentUser.getUserFirstName();
                String lastName = currentUser.getUserLastName();
                
                // Combine first and last name for a complete greeting
                if (firstName != null && !firstName.trim().isEmpty()) {
                    if (lastName != null && !lastName.trim().isEmpty()) {
                        return firstName + " " + lastName;
                    } else {
                        return firstName;
                    }
                } else {
                    return "Student";
                }
            } else {
                System.err.println("No current user found in Session");
                return "Student";
            }
        } catch (Exception e) {
            System.err.println("Error getting current user name: " + e.getMessage());
            return "Student";
        }
    }

    private void setupIcons() {
        // Load icons with error handling using file: protocol
        loadIconSafely(searchIcon, "file:resources/images/main_page/icon/WhiteMagnifier.png");
        loadIconSafely(cartIcon, "file:resources/images/main_page/icon/MyCart.png");
        loadIconSafely(wishlistIcon, "file:resources/images/main_page/icon/MyWishList.png");
        loadIconSafely(notificationIcon, "file:resources/images/main_page/icon/Notification.png");        loadIconSafely(profileIcon, "file:resources/images/main_page/icon/MyProfile.png");
    }private void setupCartBadge() {
        // Cart badge feature disabled
        // No badge display for cart count
    }
    
    // Cart count badge feature disabled
      private void loadIconSafely(ImageView imageView, String iconPath) {
        try {
            System.out.println("Attempting to load icon from: " + iconPath);
            Image icon = ImageCache.loadImage(iconPath);
            if (icon != null) {
                imageView.setImage(icon);
                System.out.println("Successfully loaded icon: " + iconPath);
            } else {
                System.out.println("Icon not found or error loading: " + iconPath);
            }} catch (Exception e) {
            System.out.println("Exception loading icon " + iconPath + ": " + e.getMessage());
        }
    }
      private void loadCourses() {
        try {
            // Load all courses from database
            List<Courses> allCourses = courseService.getAllCourses();
              // Get current user and purchased course IDs
            Users currentUser = Session.getCurrentUser();
            Set<Integer> purchasedCourseIds = new HashSet<>();
            if (currentUser != null) {
                // Get purchased courses from MyLearning
                var learningItems = myLearningService.getUserLearningItems(currentUser.getUserID());
                for (var item : learningItems) {
                    purchasedCourseIds.add(item.getCourseID());
                }
            }
            final Set<Integer> finalPurchasedCourseIds = purchasedCourseIds;
              if (allCourses != null && !allCourses.isEmpty()) {                // Filter out purchased courses first
                List<Courses> availableCourses = allCourses.stream()
                    .filter(course -> !finalPurchasedCourseIds.contains(course.getCourseID()))
                    .collect(Collectors.toList());
                    
                // Filter AI and Machine Learning courses
                List<Courses> aiCourses = availableCourses.stream()
                    .filter(course -> course.getCategory() != null && 
                            (course.getCategory().toString().contains("Artificial_Intelligence") ||
                             course.getCategory().toString().contains("Machine_Learning") ||
                             course.getCategory().toString().contains("Deep_Learning")))
                    .limit(8)  // Show maximum 8 courses
                    .collect(Collectors.toList());
                
                // Filter Game Development courses
                List<Courses> gameCourses = availableCourses.stream()
                    .filter(course -> course.getCategory() != null && 
                            course.getCategory().toString().contains("Game_Development"))
                    .limit(8)  // Show maximum 8 courses
                    .collect(Collectors.toList());
                
                // Populate containers
                populateCoursesContainer(aiCoursesContainer, aiCourses);
                populateCoursesContainer(gameCoursesContainer, gameCourses);
                
                System.out.println("Loaded " + aiCourses.size() + " AI courses and " + 
                                 gameCourses.size() + " Game Development courses from database");
                System.out.println("Filtered out " + finalPurchasedCourseIds.size() + " purchased courses");
            } else {
                System.out.println("No courses found in database, loading sample data");
                loadSampleCourses();
            }
        } catch (Exception e) {
            System.err.println("Error loading courses from database: " + e.getMessage());
            e.printStackTrace();
            // Fallback to sample data
            loadSampleCourses();
        }
    }
    
    private void loadSampleCourses() {
        List<Courses> sampleAiCourses = createSampleCourses("AI");
        populateCoursesContainer(aiCoursesContainer, sampleAiCourses);
        
        List<Courses> sampleGameCourses = createSampleCourses("GAME_DEVELOPMENT");
        populateCoursesContainer(gameCoursesContainer, sampleGameCourses);
    }    private List<Courses> createSampleCourses(String categoryName) {
        List<Courses> courses = new ArrayList<>();
        
        if ("AI".equals(categoryName)) {
            String[] aiCourseNames = {
                "Introduction to Machine Learning",
                "Deep Learning Fundamentals", 
                "Natural Language Processing",
                "Computer Vision Basics"
            };
            
            model.course.Technology[] aiTechnologies = {
                model.course.Technology.Python,
                model.course.Technology.Python,
                model.course.Technology.Python,
                model.course.Technology.Python
            };
            
            for (int i = 0; i < aiCourseNames.length; i++) {
                Courses course = new Courses();
                course.setCourseName(aiCourseNames[i]);
                course.setPrice(99.99f);
                course.setThumbnailURL("/images/default_image.png");
                // Set sample language, technology and level
                course.setLanguage(model.course.Language.English);
                course.setTechnology(aiTechnologies[i]);
                course.setLevel(i % 2 == 0 ? model.course.Level.BEGINNER : model.course.Level.INTERMEDIATE);
                courses.add(course);
            }
        } else if ("GAME_DEVELOPMENT".equals(categoryName)) {
            String[] gamesCourseNames = {
                "Unity Game Development",
                "Unreal Engine Basics",
                "2D Game Programming", 
                "Mobile Game Development"
            };
            
            model.course.Technology[] gameTechnologies = {
                model.course.Technology.CSharp,
                model.course.Technology.Cpp,
                model.course.Technology.JavaScript,
                model.course.Technology.Java
            };
            
            for (int i = 0; i < gamesCourseNames.length; i++) {
                Courses course = new Courses();
                course.setCourseName(gamesCourseNames[i]);
                course.setPrice(79.99f);
                course.setThumbnailURL("/images/default_image.png");
                // Set sample language, technology and level
                course.setLanguage(model.course.Language.English);
                course.setTechnology(gameTechnologies[i]);
                course.setLevel(i % 3 == 0 ? model.course.Level.BEGINNER : 
                               i % 3 == 1 ? model.course.Level.INTERMEDIATE : model.course.Level.ADVANCED);
                courses.add(course);
            }
        }
        
        return courses;
    }private void populateCoursesContainer(HBox container, List<Courses> courses) {
        container.getChildren().clear();
        
        // Limit to 4 courses and sort by creation date (newest first)
        List<Courses> topCourses = courses.stream()
            .sorted((c1, c2) -> {
                // Sort by creation date (newest first)
                if (c1.getCreatedAt() != null && c2.getCreatedAt() != null) {
                    return c2.getCreatedAt().compareTo(c1.getCreatedAt());
                }
                // Fallback to course ID
                return Integer.compare(c2.getCourseID(), c1.getCourseID());
            })
            .limit(4)  // Show only 4 courses per category
            .collect(Collectors.toList());
        
        for (Courses course : topCourses) {
            VBox courseCard = createCourseCard(course);
            container.getChildren().add(courseCard);
        }
    }    private VBox createCourseCard(Courses course) {
        VBox card = new VBox();
        card.getStyleClass().add("course-card");
        card.setSpacing(0);  // No spacing between elements
        card.setAlignment(Pos.TOP_LEFT);
        card.setPrefWidth(230);  // Fixed width for consistency
        card.setMaxWidth(230);
        card.setMinWidth(230);
        card.setPrefHeight(360);  // Increased height to accommodate all attributes
        card.setMaxHeight(360);
        card.setMinHeight(360);  // Fixed height for consistency
        
        // Set course ID as user data for state synchronization
        card.setUserData(course.getCourseID());
        
        // Course image container - aligned to top edge with fixed dimensions
        VBox imageContainer = new VBox();
        imageContainer.setAlignment(Pos.CENTER);
        imageContainer.setStyle("-fx-padding: 10 10 0 10; -fx-background-radius: 12 12 0 0;");
        imageContainer.setPrefHeight(150); // Fixed height for image section
        imageContainer.setMaxHeight(150);
        imageContainer.setMinHeight(150);
        
        ImageView imageView = new ImageView();
        imageView.setFitWidth(210);  // Fixed width
        imageView.setFitHeight(130); // Fixed height
        imageView.setPreserveRatio(false); // Allow stretching to maintain thumbnail aspect ratio
        imageView.setSmooth(true);
        imageView.getStyleClass().add("course-image");try {
            if (course.getThumbnailURL() != null && !course.getThumbnailURL().isEmpty()) {
                // Try to load course image from thumbnailURL first with file: protocol
                String thumbnailPath = course.getThumbnailURL();
                
                // Ensure we use file: protocol for local files
                if (!thumbnailPath.startsWith("file:") && !thumbnailPath.startsWith("http")) {
                    thumbnailPath = "file:" + thumbnailPath;
                }
                  System.out.println("Loading course image from URL: " + thumbnailPath);
                Image image = ImageCache.loadImage(thumbnailPath);
                if (image != null) {
                    imageView.setImage(image);
                    System.out.println("Successfully loaded course image: " + thumbnailPath);
                } else {
                    // Try alternative paths if original failed
                    String altPath = "file:resources/images/" + course.getThumbnailURL().replace("user_data/images/", "");
                    System.out.println("Retrying with alternative path: " + altPath);
                    Image altImage = ImageCache.loadImage(altPath);
                    if (altImage != null) {
                        imageView.setImage(altImage);
                        System.out.println("Successfully loaded alternative image: " + altPath);
                    } else {
                        setDefaultCourseImage(imageView);
                    }
                }
            } else {
                setDefaultCourseImage(imageView);
            }
        } catch (Exception e) {
            System.err.println("Error loading course image: " + e.getMessage());
            setDefaultCourseImage(imageView);        }        
        imageContainer.getChildren().add(imageView);
        
        // Course content container with fixed spacing and layout
        VBox contentContainer = new VBox();
        contentContainer.setSpacing(8);  // Consistent spacing between content elements
        contentContainer.setAlignment(Pos.TOP_LEFT);
        contentContainer.setStyle("-fx-padding: 8 12 12 12;");
        contentContainer.setPrefHeight(210); // Fixed height for content section
        contentContainer.setMaxHeight(210);
        contentContainer.setMinHeight(210);
        
        // Course title - fixed height with ellipsis for long text
        Label titleLabel = new Label(course.getCourseName());
        titleLabel.getStyleClass().add("course-title");
        titleLabel.setWrapText(false);  // Disable wrapping to use ellipsis
        titleLabel.setMaxWidth(206);
        titleLabel.setPrefHeight(20);  // Fixed height
        titleLabel.setMaxHeight(20);
        titleLabel.setMinHeight(20);
        titleLabel.setStyle("-fx-text-overrun: ellipsis;"); // Show ellipsis for long text

        // Course instructor - fixed height
        String instructorName = getInstructorName(course.getUserID());
        Label instructorLabel = new Label("By " + instructorName);
        instructorLabel.getStyleClass().add("course-instructor");
        instructorLabel.setPrefHeight(18);  // Fixed height
        instructorLabel.setMaxHeight(18);
        instructorLabel.setMinHeight(18);
        instructorLabel.setMaxWidth(206);
        instructorLabel.setStyle("-fx-text-overrun: ellipsis;");
          
        // Language, Technology, Level badges in one row - fixed height
        HBox badgesBox = new HBox(5);
        badgesBox.setAlignment(Pos.CENTER_LEFT);
        badgesBox.setPrefHeight(25); // Fixed height
        badgesBox.setMaxHeight(25);
        badgesBox.setMinHeight(25);
        
        // Language badge
        String languageText = (course.getLanguage() != null) ? course.getLanguage().toString() : "N/A";
        Label languageLabel = new Label(languageText);
        languageLabel.getStyleClass().add("course-language");
        languageLabel.setMaxWidth(50); // Fixed width to prevent overflow
        languageLabel.setStyle("-fx-text-overrun: clip; -fx-font-size: 10px;");
        
        // Technology badge
        String technologyText = (course.getTechnology() != null) ? course.getTechnology().toString() : "N/A";
        Label technologyLabel = new Label(technologyText);
        technologyLabel.getStyleClass().add("course-technology");
        technologyLabel.setMaxWidth(65); // Fixed width to prevent overflow
        technologyLabel.setStyle("-fx-text-overrun: clip; -fx-font-size: 10px;");
        
        // Level badge  
        String levelText = (course.getLevel() != null) ? course.getLevel().toString() : "N/A";
        Label levelLabel = new Label(levelText);
        levelLabel.getStyleClass().add("course-level");
        levelLabel.setMaxWidth(55); // Fixed width to prevent overflow
        levelLabel.setStyle("-fx-text-overrun: clip; -fx-font-size: 10px;");
        
        badgesBox.getChildren().addAll(languageLabel, technologyLabel, levelLabel);
        
        // Course rating - fixed height
        double avgRating = courseReviewService.getCourseAverageRating(course.getCourseID());
        int reviewCount = courseReviewService.getCourseReviewCount(course.getCourseID());
        
        String starDisplay = getStarDisplay(avgRating);
        String ratingDisplay = reviewCount > 0 ? 
            String.format("%s %.1f (%d)", starDisplay, avgRating, reviewCount) :
            "☆☆☆☆☆ No reviews";
            
        Label ratingLabel = new Label(ratingDisplay);
        ratingLabel.getStyleClass().add("course-rating");
        ratingLabel.setPrefHeight(20);  // Fixed height
        ratingLabel.setMaxHeight(20);
        ratingLabel.setMinHeight(20);
        ratingLabel.setMaxWidth(206);
        ratingLabel.setStyle("-fx-text-overrun: ellipsis;");

        // Course price - fixed height and position
        Label priceLabel = new Label(String.format("$%.2f", course.getPrice()));
        priceLabel.getStyleClass().add("course-price");
        priceLabel.setPrefHeight(22);  // Fixed height
        priceLabel.setMaxHeight(22);
        priceLabel.setMinHeight(22);
        
        // Create action icons container (cart and wishlist) - fixed height
        HBox actionsContainer = createCourseCardActions(course);
        actionsContainer.setPrefHeight(40);  // Fixed height for action buttons
        actionsContainer.setMaxHeight(40);
        actionsContainer.setMinHeight(40);
        
        // Add all content to the content container with fixed positioning
        contentContainer.getChildren().addAll(titleLabel, instructorLabel, badgesBox, ratingLabel, priceLabel, actionsContainer);
        
        // Add image container and content container to the main card
        card.getChildren().addAll(imageContainer, contentContainer);
          card.setOnMouseClicked(event -> {
            System.out.println("Clicked course: " + course.getCourseName());
            navigateToStudentCourseDetail(course);
        });
        
        return card;
    }    private void setDefaultCourseImage(ImageView imageView) {
        try {
            // Try to load default course image from resources folder
            Image defaultImage = ImageCache.loadImage("file:resources/images/default_course.png");
            if (defaultImage != null) {
                imageView.setImage(defaultImage);
            } else {
                // Try alternative default image
                Image altImage = ImageCache.loadImage("file:resources/images/testBanner.png");
                if (altImage != null) {
                    imageView.setImage(altImage);
                } else {
                    // Create a simple placeholder background
                    imageView.setImage(null);
                }
            }
        } catch (Exception ex) {
            System.err.println("Error loading default course image: " + ex.getMessage());
            imageView.setImage(null);
        }    }
      private String getInstructorName(int userId) {
        try {
            Users instructor = userService.GetUserByID(userId);
            if (instructor != null) {
                String firstName = instructor.getUserFirstName();
                String lastName = instructor.getUserLastName();
                
                if (firstName != null && lastName != null) {
                    return firstName + " " + lastName;
                } else if (firstName != null) {
                    return firstName;
                } else if (lastName != null) {
                    return lastName;
                } else {
                    return "Course Instructor";
                }
            } else {
                return "Course Instructor";
            }
        } catch (Exception e) {
            System.err.println("Error getting instructor name: " + e.getMessage());
            return "Course Instructor";
        }
    }
      private void loadUserProfileImage(int userId) {
        try {            // Load user profile image from user_data folder
            String userImagePath = "file:user_data/images/user_" + userId + ".png";
            Image userImage = ImageCache.loadImage(userImagePath);
            
            if (userImage != null) {
                profileIcon.setImage(userImage);
            } else {
                // Try alternative formats
                String userImagePathJpg = "file:user_data/images/user_" + userId + ".jpg";
                Image userImageJpg = ImageCache.loadImage(userImagePathJpg);
                
                if (userImageJpg != null) {
                    profileIcon.setImage(userImageJpg);
                } else {
                    // Use default profile icon
                    setDefaultProfileIcon();
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading user profile image: " + e.getMessage());
            setDefaultProfileIcon();
        }
    }    private void setDefaultProfileIcon() {
        try {
            // Load default profile icon from resources folder
            Image defaultProfileIcon = ImageCache.loadImage("file:resources/images/main_page/icon/MyProfile.png");
            if (defaultProfileIcon != null) {
                profileIcon.setImage(defaultProfileIcon);
            } else {
                // Try alternative default icon
                Image altIcon = ImageCache.loadImage("file:resources/images/default_profile.png");
                if (altIcon != null) {
                    profileIcon.setImage(altIcon);
                } else {
                    profileIcon.setImage(null);
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading default profile icon: " + e.getMessage());
            profileIcon.setImage(null);
        }
    }
    
    @FXML
    private void handleSearch() {
        String searchText = searchField.getText();
        if (searchText != null && !searchText.trim().isEmpty()) {
            System.out.println("Search for: " + searchText);
        }
    }    /**
     * Shows the profile context menu when profile button is clicked.
     * This method is called from the FXML onAction="#showProfileMenu"
     */
    @FXML
    private void showProfileMenu() {
        if (profileMenu != null) {
            try {
                // Method 1: Calculate screen coordinates relative to the profile button
                double xOffset = profileButton.localToScreen(profileButton.getBoundsInLocal()).getMinX();
                double yOffset = profileButton.localToScreen(profileButton.getBoundsInLocal()).getMaxY();
                
                // Show the menu at the calculated position (below the profile button)
                profileMenu.show(profileButton.getScene().getWindow(), xOffset, yOffset);
            } catch (Exception e) {
                // Fallback method: Use alternative positioning
                System.err.println("Primary positioning failed, using fallback method: " + e.getMessage());
                try {
                    // Alternative method using Side.BOTTOM
                    profileMenu.show(profileButton, javafx.geometry.Side.BOTTOM, 0, 0);
                } catch (Exception e2) {
                    // Final fallback
                    System.err.println("Fallback positioning also failed: " + e2.getMessage());
                    profileMenu.show(profileButton, 0, profileButton.getHeight());
                }
            }
        }
    }
    
    /**
     * Navigate to the user profile page
     */
    private void showProfile() {
        try {
            System.out.println("Navigating to User Profile page...");
            SceneManager.switchScene(
                "User Profile",
                "/frontend/view/userProfile/UserProfile.fxml"
            );
        } catch (Exception e) {
            System.err.println("Error navigating to profile page: " + e.getMessage());
            e.printStackTrace();
            
            // Show error alert
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Navigation Error");
            alert.setHeaderText("Failed to open Profile");
            alert.setContentText("Could not navigate to the profile page. Please try again.");
            alert.showAndWait();
        }
    }
    
    /**
     * Navigate to settings (placeholder for future implementation)
     */
    private void showSettings() {
        // Placeholder for settings functionality
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Settings");
        alert.setHeaderText("Settings Page");
        alert.setContentText("Settings functionality will be implemented in a future update.");
        alert.showAndWait();
    }
    
    /**
     * Handle user logout
     */
    private void logout() {
        try {
            // Show confirmation dialog
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Logout Confirmation");
            confirmAlert.setHeaderText("Are you sure you want to logout?");
            confirmAlert.setContentText("You will be redirected to the login page.");
            
            Optional<ButtonType> result = confirmAlert.showAndWait();            if (result.isPresent() && result.get() == ButtonType.OK) {
                System.out.println("User logging out...");
                
                // Clear session if there's a session management system
                // Session.clearSession(); // Uncomment if you have session management
                
                // Navigate to login page with proper window size reset
                SceneManager.switchToLoginScene(
                    "Login",
                    "/frontend/view/login/Login.fxml"
                );
            }
        } catch (Exception e) {
            System.err.println("Error during logout: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private HBox createCourseCardActions(Courses course) {
        HBox actionsContainer = new HBox(8);
        actionsContainer.getStyleClass().add("course-card-actions");
        actionsContainer.setAlignment(Pos.CENTER_RIGHT);
        
        // Create cart icon
        ImageView cartIconView = new ImageView();
        cartIconView.setFitWidth(20);
        cartIconView.setFitHeight(20);
        cartIconView.setPreserveRatio(true);
        
        Button cartButton = new Button();
        cartButton.setGraphic(cartIconView);
        cartButton.getStyleClass().addAll("course-card-icon", "cart-icon-default");
        
        // Create wishlist icon (heart)
        ImageView wishlistIconView = new ImageView();
        wishlistIconView.setFitWidth(20);
        wishlistIconView.setFitHeight(20);
        wishlistIconView.setPreserveRatio(true);
        
        Button wishlistButton = new Button();
        wishlistButton.setGraphic(wishlistIconView);
        wishlistButton.getStyleClass().addAll("course-card-icon", "wishlist-icon-default");        // Load icons (using existing shopping cart and wishlist icons)
        try {
            // Use existing cart icon
            Image cartImage = ImageCache.loadImage("file:resources/images/main_page/icon/MyCart.png");
            if (cartImage != null) {
                cartIconView.setImage(cartImage);
            }
            
            // Use existing wishlist icon  
            Image heartImage = ImageCache.loadImage("file:resources/images/main_page/icon/MyWishList.png");
            if (heartImage != null) {
                wishlistIconView.setImage(heartImage);
            }
        } catch (Exception e) {
            System.err.println("Error loading course action icons: " + e.getMessage());
        }
          // Get current user ID (mock for now, should come from session)
        int currentUserId = 1; // TODO: Get from actual user session
          // Check initial states using StudentStateManager
        StudentStateManager stateManager = StudentStateManager.getInstance();
        boolean isInCart = stateManager.isInCart(course.getCourseID());
        boolean isInWishlist = stateManager.isInWishlist(course.getCourseID());
        
        // Update initial icon states
        updateCartIconState(cartButton, isInCart);
        updateWishlistIconState(wishlistButton, isInWishlist);
        
        // Add event handlers
        cartButton.setOnAction(event -> {
            event.consume(); // Prevent triggering course card click
            handleCartToggle(currentUserId, course, cartButton);
        });
        
        wishlistButton.setOnAction(event -> {
            event.consume(); // Prevent triggering course card click
            handleWishlistToggle(currentUserId, course, wishlistButton);
        });
        
        actionsContainer.getChildren().addAll(cartButton, wishlistButton);
        return actionsContainer;
    }
    
    private void updateCartIconState(Button cartButton, boolean isInCart) {
        cartButton.getStyleClass().removeAll("cart-icon-default", "cart-icon-active");
        if (isInCart) {
            cartButton.getStyleClass().add("cart-icon-active");
        } else {
            cartButton.getStyleClass().add("cart-icon-default");
        }
    }
    
    private void updateWishlistIconState(Button wishlistButton, boolean isInWishlist) {
        wishlistButton.getStyleClass().removeAll("wishlist-icon-default", "wishlist-icon-active");
        if (isInWishlist) {
            wishlistButton.getStyleClass().add("wishlist-icon-active");
        } else {
            wishlistButton.getStyleClass().add("wishlist-icon-default");
        }
    }    private void handleCartToggle(int userId, Courses course, Button cartButton) {
        try {
            StudentStateManager stateManager = StudentStateManager.getInstance();
            boolean isCurrentlyInCart = stateManager.isInCart(course.getCourseID());
            System.out.println("StudentMainPage: Cart toggle for course " + course.getCourseID() + ", currently in cart: " + isCurrentlyInCart);
            
            if (isCurrentlyInCart) {
                // Remove from cart using StateManager
                boolean removed = stateManager.removeFromCart(course.getCourseID());
                if (removed) {
                    System.out.println("StudentMainPage: Successfully removed " + course.getCourseName() + " from cart");                    // Force immediate UI update for this button
                    javafx.application.Platform.runLater(() -> updateCartIconState(cartButton, false));
                } else {
                    System.err.println("StudentMainPage: Failed to remove " + course.getCourseName() + " from cart");
                }
            } else {
                // Add to cart using StateManager
                boolean added = stateManager.addToCart(course.getCourseID());
                if (added) {
                    System.out.println("StudentMainPage: Successfully added " + course.getCourseName() + " to cart");
                    // Force immediate UI update for this button
                    javafx.application.Platform.runLater(() -> updateCartIconState(cartButton, true));
                } else {
                    System.err.println("StudentMainPage: Failed to add " + course.getCourseName() + " to cart");
                }
            }
            
            // Cart badge feature disabled
            
        } catch (Exception e) {
            System.err.println("StudentMainPage: Error toggling cart state: " + e.getMessage());
            e.printStackTrace();
        }
    }private void handleWishlistToggle(int userId, Courses course, Button wishlistButton) {
        try {
            StudentStateManager stateManager = StudentStateManager.getInstance();
            boolean isCurrentlyInWishlist = stateManager.isInWishlist(course.getCourseID());
            System.out.println("StudentMainPage: Wishlist toggle for course " + course.getCourseID() + ", currently in wishlist: " + isCurrentlyInWishlist);
            
            if (isCurrentlyInWishlist) {
                // Remove from wishlist using StateManager
                boolean removed = stateManager.removeFromWishlist(course.getCourseID());                if (removed) {
                    System.out.println("StudentMainPage: Successfully removed " + course.getCourseName() + " from wishlist");
                    // Force immediate UI update for this button
                    javafx.application.Platform.runLater(() -> updateWishlistIconState(wishlistButton, false));
                } else {
                    System.err.println("StudentMainPage: Failed to remove " + course.getCourseName() + " from wishlist");
                }
            } else {
                // Add to wishlist using StateManager
                boolean added = stateManager.addToWishlist(course.getCourseID());
                if (added) {
                    System.out.println("StudentMainPage: Successfully added " + course.getCourseName() + " to wishlist");
                    // Force immediate UI update for this button
                    javafx.application.Platform.runLater(() -> updateWishlistIconState(wishlistButton, true));
                } else {
                    System.err.println("StudentMainPage: Failed to add " + course.getCourseName() + " to wishlist");
                }
            }
            
        } catch (Exception e) {
            System.err.println("StudentMainPage: Error toggling wishlist state: " + e.getMessage());
            e.printStackTrace();
        }
    }
      /**
     * Refresh all course card states (cart and wishlist icons) to sync with current database state
     */    private void refreshCourseStates() {        try {
            System.out.println("Refreshing StudentMainPage course states...");            // Reinitialize state manager to ensure sync with database
            StudentStateManager stateManager = StudentStateManager.getInstance();
            stateManager.initializeState();
            
            // Cart badge feature disabled
            
            // Refresh course card states by reloading all courses
            loadCourses();
            
            System.out.println("StudentMainPage course states refreshed successfully");
            
        } catch (Exception e) {
            System.err.println("Error refreshing StudentMainPage course states: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Method to be called when returning to this page from another page
     * to ensure cart/wishlist states are synchronized
     */    public void refreshPageData() {
        System.out.println("StudentMainPage refreshPageData called");
        
        // Refresh welcome message with current user
        setupWelcomeMessage();
        
        // Refresh course states
        refreshCourseStates();
    }
      /**
     * Navigate to StudentCourseDetailPage with the selected course
     */
    private void navigateToStudentCourseDetail(Courses course) {
        try {
            // Use SceneManager to navigate to StudentCourseDetailPage and pass course data
            SceneManager.switchSceneReloadWithData(
                "studentCourseDetailPage",
                "/frontend/view/studentCourseDetailPage/StudentCourseDetailPage.fxml",
                (controller, data) -> {
                    if (controller instanceof backend.controller.studentCourseDetailPage.StudentCourseDetailPageController) {
                        ((backend.controller.studentCourseDetailPage.StudentCourseDetailPageController) controller).setCourseData(data);
                    }
                },
                course
            );
        } catch (Exception e) {
            System.err.println("Error navigating to StudentCourseDetailPage: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Convert numeric rating to star display
     */
    private String getStarDisplay(double rating) {
        StringBuilder stars = new StringBuilder();
        int fullStars = (int) rating;
        boolean hasHalfStar = (rating - fullStars) >= 0.5;
        
        // Add full stars
        for (int i = 0; i < fullStars; i++) {
            stars.append("★");
        }
        
        // Add half star if applicable
        if (hasHalfStar && fullStars < 5) {
            stars.append("☆");
            fullStars++;
        }
        
        // Add empty stars to reach 5 total
        for (int i = fullStars; i < 5; i++) {
            stars.append("☆");
        }
        
        return stars.toString();
    }
}