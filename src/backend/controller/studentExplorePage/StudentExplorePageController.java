package backend.controller.studentExplorePage;

import backend.controller.scene.SceneManager;
import backend.service.course.CourseService;
import backend.service.user.UserService;
import backend.service.sample.SampleDataService;
import backend.service.course.CourseReviewService;
import backend.service.state.StudentStateManager;
import backend.util.ImageCache;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.application.Platform;
import model.course.Category;
import model.course.Courses;
import model.user.Users;
import model.user.Session;

import java.net.URL;
import java.util.*;

public class StudentExplorePageController implements Initializable {

    // Navigation elements (same as StudentMainPage)
    @FXML private Label homeLabel;
    @FXML private Label categoryLabel;
    @FXML private Label myLearningLabel;
    @FXML private TextField searchField;
    @FXML private Button searchButton;
    @FXML private Button cartButton;
    @FXML private Button wishlistButton;
    @FXML private Button notificationButton;
    @FXML private Button profileButton;
    
    // Icon ImageViews
    @FXML private ImageView searchIcon;
    @FXML private ImageView cartIcon;
    @FXML private ImageView wishlistIcon;
    @FXML private ImageView notificationIcon;
    @FXML private ImageView profileIcon;
    
    // Content elements (similar to InstructorExplorePage)
    @FXML private ComboBox<String> categoryFilter;
    @FXML private Button clearFiltersButton;
    @FXML private VBox coursesContainer;
    @FXML private ScrollPane contentScrollPane;    // Image cache now handled by ImageCache utility class    
    private ObservableList<Courses> allCourses;    private Map<Category, List<Courses>> coursesByCategory;    private CourseService courseService;
    private UserService userService;
    private CourseReviewService courseReviewService;
    private backend.service.user.MyLearningService myLearningService;
    private ContextMenu profileMenu;private StudentStateManager stateManager;
    
    // Cart badge feature disabled    @Override
    public void initialize(URL location, ResourceBundle resources) {        courseService = new CourseService();
        userService = new UserService();
        courseReviewService = new CourseReviewService();
        myLearningService = new backend.service.user.MyLearningService();
        stateManager = StudentStateManager.getInstance();
        
        setupProfileMenu();
        setupNavigationEvents();
        setupIcons();
        setupCategoryFilter();
        loadCoursesFromDatabase();
        
        // Create sample lectures if needed
        SampleDataService sampleDataService = new SampleDataService();
        if (sampleDataService.needsampleLectures()) {
            sampleDataService.createSampleLectures();
        }
          
        // Setup cart badge and state manager BEFORE displaying courses
        setupCartBadge();
        setupStateManager();
        
        // Initialize state from database BEFORE creating course cards
        if (model.user.Session.getCurrentUser() != null) {
            stateManager.initializeState();
        }
          // Now display courses with correct initial state
        displayAllCourses();
        
        // Check for pending category filter from main page
        String pendingFilter = backend.controller.scene.SceneManager.getPendingCategoryFilter();
        if (pendingFilter != null) {
            applyCategoryFilter(pendingFilter);
        }
    }private void setupNavigationEvents() {
        homeLabel.setOnMouseClicked(event -> goToMainPage());
        categoryLabel.setOnMouseClicked(event -> {
            // Already on category page
        });        myLearningLabel.setOnMouseClicked(event -> {
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

    private void setupProfileMenu() {
        profileMenu = new ContextMenu();
        
        MenuItem profileItem = new MenuItem("My Profile");
        profileItem.setOnAction(e -> showProfile());
        
        MenuItem settingsItem = new MenuItem("Settings");
        settingsItem.setOnAction(e -> showSettings());
        
        MenuItem logoutItem = new MenuItem("Logout");
        logoutItem.setOnAction(e -> logout());
        
        profileMenu.getItems().addAll(profileItem, settingsItem, new SeparatorMenuItem(), logoutItem);
    }    private void setupIcons() {
        // Load icons with error handling using file: protocol
        loadIconSafely(searchIcon, "file:resources/images/main_page/icon/WhiteMagnifier.png");
        loadIconSafely(cartIcon, "file:resources/images/main_page/icon/MyCart.png");
        loadIconSafely(wishlistIcon, "file:resources/images/main_page/icon/MyWishList.png");
        loadIconSafely(notificationIcon, "file:resources/images/main_page/icon/Notification.png");
        loadIconSafely(profileIcon, "file:resources/images/main_page/icon/MyProfile.png");
    }private void setupCartBadge() {
        // Cart badge feature disabled
        // No badge display for cart count
    }
    
    // Cart count badge feature disabled
    
    /**
     * Load image with caching to prevent memory leaks
     */    private Image loadImageSafely(String imagePath) {
        return ImageCache.loadImage(imagePath);
    }
      private void loadIconSafely(ImageView imageView, String iconPath) {
        try {
            Image image = loadImageSafely(iconPath);
            if (image != null) {
                imageView.setImage(image);
                System.out.println("Successfully loaded icon: " + iconPath);
            } else {
                System.err.println("Failed to load icon: " + iconPath);
            }
        } catch (Exception e) {
            System.err.println("Error loading icon " + iconPath + ": " + e.getMessage());
        }
    }

    private void setupCategoryFilter() {
        ObservableList<String> categories = FXCollections.observableArrayList();
        categories.add("All Categories");
        
        for (Category category : Category.values()) {
            categories.add(formatCategoryName(category.name()));
        }
        
        categoryFilter.setItems(categories);
        categoryFilter.setValue("All Categories");
    }

    private String formatCategoryName(String categoryName) {
        return categoryName.replace("_", " ");
    }    private void loadCoursesFromDatabase() {
        try {
            List<Courses> coursesFromDB = courseService.getAllCourses();
            if (coursesFromDB != null && !coursesFromDB.isEmpty()) {
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
                
                // Filter out purchased courses
                List<Courses> availableCourses = new ArrayList<>();
                for (Courses course : coursesFromDB) {
                    if (!purchasedCourseIds.contains(course.getCourseID())) {
                        availableCourses.add(course);
                    }
                }
                
                allCourses = FXCollections.observableArrayList(availableCourses);
                System.out.println("Loaded " + allCourses.size() + " available courses from database");
                System.out.println("Filtered out " + purchasedCourseIds.size() + " purchased courses");
            } else {
                System.out.println("No courses found in database, creating sample data");
                createSampleCourses();
            }
            
            // Group courses by category
            coursesByCategory = new HashMap<>();
            for (Category category : Category.values()) {
                coursesByCategory.put(category, new ArrayList<>());
            }
            
            for (Courses course : allCourses) {
                coursesByCategory.get(course.getCategory()).add(course);
            }
            
        } catch (Exception e) {
            System.err.println("Error loading courses from database: " + e.getMessage());
            e.printStackTrace();
            createSampleCourses();
        }
    }

    private void createSampleCourses() {
        // Sample data - replace with actual database data
        String[] courseNames = {
            "Introduction to Machine Learning", "Web Development Bootcamp", "Python for Beginners",
            "Advanced Java Programming", "Data Science Fundamentals", "Mobile App Development",
            "Cybersecurity Essentials", "Cloud Computing Basics", "AI and Deep Learning",
            "Game Development with Unity", "UI/UX Design Principles", "DevOps Fundamentals"
        };
        
        // Use real thumbnail images from user_data
        String[] thumbnails = {
            "/user_data/images/602e4edc-f79f-45f9-a1b2-2f164b864f43_Python.png",
            "/user_data/images/aaf67bfb-2256-4cdc-b9ca-165f71624d3a_Acer_Wallpaper_01_5000x2814.jpg",
            "/user_data/images/3d11874b-0732-4796-b72a-58def05b4035_Python.png",
            "/user_data/images/befe3d55-a143-4fb2-bbc0-31af817ae751_Java.jpg",
            "/user_data/images/9cc4ca8d-8ac2-45f8-8ce1-fccb6f8feccb_SDL2.png",
            "/user_data/images/d34c8ae1-3555-477b-aa1f-c038a2b11246_SDL2.png",
            "/images/default_image.png",
            "/images/default_image.png",
            "/user_data/images/602e4edc-f79f-45f9-a1b2-2f164b864f43_Python.png",
            "/user_data/images/befe3d55-a143-4fb2-bbc0-31af817ae751_Java.jpg",
            "/images/default_image.png",
            "/images/default_image.png"
        };
        
        Category[] categories = Category.values();
        
        allCourses = FXCollections.observableArrayList();
        coursesByCategory = new HashMap<>();
        
        for (int i = 0; i < courseNames.length; i++) {
            Courses course = new Courses();
            course.setCourseID(i + 1);
            course.setCourseName(courseNames[i]);
            course.setCategory(categories[i % categories.length]);            course.setCourseDescription("Sample description for " + courseNames[i]);
            course.setPrice((float)(99.99 + (i * 10)));
            course.setThumbnailURL(thumbnails[i]);
            course.setUserID(1); // Sample instructor ID
            
            allCourses.add(course);
        }
        
        // Group courses by category
        for (Category category : Category.values()) {
            coursesByCategory.put(category, new ArrayList<>());
        }
        
        for (Courses course : allCourses) {
            coursesByCategory.get(course.getCategory()).add(course);
        }
    }

    private void displayAllCourses() {
        coursesContainer.getChildren().clear();
        
        for (Category category : Category.values()) {
            List<Courses> courses = coursesByCategory.get(category);
            if (courses != null && !courses.isEmpty()) {
                VBox categorySection = createCategorySection(category, courses);
                coursesContainer.getChildren().add(categorySection);
            }
        }
        
        if (coursesContainer.getChildren().isEmpty()) {
            displayNoCoursesMessage();
        }
    }

    private VBox createCategorySection(Category category, List<Courses> courses) {
        VBox categorySection = new VBox();
        categorySection.getStyleClass().add("category-section");
        
        // Category header
        VBox header = new VBox();
        header.getStyleClass().add("category-header");
        
        Label categoryTitle = new Label(formatCategoryName(category.name()));
        categoryTitle.getStyleClass().add("category-title");
        
        Label categorySubtitle = new Label(courses.size() + " courses available");
        categorySubtitle.getStyleClass().add("category-subtitle");
        
        header.getChildren().addAll(categoryTitle, categorySubtitle);
          // Course grid with proper layout for 4 courses per row
        FlowPane courseGrid = new FlowPane();
        courseGrid.getStyleClass().add("course-grid");
        courseGrid.setHgap(20);
        courseGrid.setVgap(20);
        courseGrid.setPrefWrapLength(980); // Set wrap length to fit 4 cards (4 * 230 + 3 * 20 = 980)
        courseGrid.setAlignment(javafx.geometry.Pos.TOP_LEFT);
        
        for (Courses course : courses) {
            VBox courseCard = createCourseCard(course);
            courseGrid.getChildren().add(courseCard);
        }
        
        categorySection.getChildren().addAll(header, courseGrid);
        return categorySection;
    }    private VBox createCourseCard(Courses course) {
        VBox courseCard = new VBox();
        courseCard.getStyleClass().add("course-card");
        courseCard.setOnMouseClicked(event -> handleCourseClick(course));
        
        // Set consistent fixed dimensions for all cards
        courseCard.setPrefWidth(230);
        courseCard.setMaxWidth(230);
        courseCard.setMinWidth(230);
        courseCard.setPrefHeight(360);  // Increased height to accommodate all attributes
        courseCard.setMaxHeight(360);
        courseCard.setMinHeight(360);
        courseCard.setAlignment(javafx.geometry.Pos.TOP_LEFT);
        courseCard.setSpacing(0);
        
        // Set course ID as user data for state synchronization
        courseCard.setUserData(course.getCourseID());
        
        // Course image container - fixed dimensions
        VBox imageContainer = new VBox();
        imageContainer.setAlignment(javafx.geometry.Pos.CENTER);
        imageContainer.setStyle("-fx-padding: 10 10 0 10; -fx-background-radius: 12 12 0 0;");
        imageContainer.setPrefHeight(150);
        imageContainer.setMaxHeight(150);
        imageContainer.setMinHeight(150);
        
        ImageView thumbnail = new ImageView();
        thumbnail.getStyleClass().add("course-thumbnail");
        thumbnail.setFitWidth(210);  // Fixed width
        thumbnail.setFitHeight(130); // Fixed height
        thumbnail.setPreserveRatio(false); // Allow stretching for proper thumbnail ratio
        thumbnail.setSmooth(true);
        
        try {
            String imagePath = course.getThumbnailURL();
            if (imagePath != null && !imagePath.isEmpty()) {
                // Ensure we use file: protocol for local files
                if (!imagePath.startsWith("file:") && !imagePath.startsWith("http")) {
                    imagePath = "file:" + imagePath;
                }
                  Image image = loadImageSafely(imagePath);
                if (image != null) {
                    thumbnail.setImage(image);
                } else {
                    setDefaultCourseImage(thumbnail);
                }
            } else {
                setDefaultCourseImage(thumbnail);
            }
        } catch (Exception e) {
            System.err.println("Error loading course thumbnail: " + e.getMessage());
            setDefaultCourseImage(thumbnail);
        }
        
        imageContainer.getChildren().add(thumbnail);
        
        // Course info container with fixed layout
        VBox courseInfo = new VBox();
        courseInfo.getStyleClass().add("course-info");
        courseInfo.setSpacing(8);
        courseInfo.setPrefHeight(210);
        courseInfo.setMaxHeight(210);
        courseInfo.setMinHeight(210);
        courseInfo.setStyle("-fx-padding: 8 12 12 12;");
        
        // Course title - fixed height with ellipsis
        Label courseTitle = new Label(course.getCourseName());
        courseTitle.getStyleClass().add("course-title");
        courseTitle.setWrapText(false);
        courseTitle.setMaxWidth(206);
        courseTitle.setPrefHeight(20);
        courseTitle.setMaxHeight(20);
        courseTitle.setMinHeight(20);
        courseTitle.setStyle("-fx-text-overrun: ellipsis;");
          // Author name - fixed height
        String authorName = "By Instructor";
        try {
            Users instructor = userService.GetUserByID(course.getUserID());
            if (instructor != null) {
                authorName = "By " + instructor.getUserFirstName() + " " + instructor.getUserLastName();
            }
        } catch (Exception e) {
            System.err.println("Error getting instructor name: " + e.getMessage());
        }
        
        Label authorLabel = new Label(authorName);
        authorLabel.getStyleClass().add("course-author");
        authorLabel.setPrefHeight(18);
        authorLabel.setMaxHeight(18);
        authorLabel.setMinHeight(18);
        authorLabel.setMaxWidth(206);
        authorLabel.setStyle("-fx-text-overrun: ellipsis;");
        
        // Language, Technology, Level badges in one row - fixed height
        HBox badgesContainer = new HBox(5);
        badgesContainer.setAlignment(Pos.CENTER_LEFT);
        badgesContainer.setPrefHeight(25);
        badgesContainer.setMaxHeight(25);
        badgesContainer.setMinHeight(25);
        
        // Language badge
        if (course.getLanguage() != null) {
            Label languageTag = new Label(course.getLanguage().toString());
            languageTag.getStyleClass().add("course-language");
            languageTag.setMaxWidth(50);
            languageTag.setStyle("-fx-text-overrun: clip; -fx-font-size: 10px;");
            badgesContainer.getChildren().add(languageTag);
        }
        
        // Technology badge
        if (course.getTechnology() != null) {
            Label technologyTag = new Label(course.getTechnology().toString());
            technologyTag.getStyleClass().add("course-technology");
            technologyTag.setMaxWidth(65);
            technologyTag.setStyle("-fx-text-overrun: clip; -fx-font-size: 10px;");
            badgesContainer.getChildren().add(technologyTag);
        }
        
        // Level badge
        if (course.getLevel() != null) {
            Label levelTag = new Label(course.getLevel().toString());
            levelTag.getStyleClass().add("course-level");
            levelTag.setMaxWidth(55);
            levelTag.setStyle("-fx-text-overrun: clip; -fx-font-size: 10px;");
            badgesContainer.getChildren().add(levelTag);
        }
        
        // Rating - fixed height
        HBox ratingContainer = new HBox();
        ratingContainer.getStyleClass().add("course-rating");
        ratingContainer.setAlignment(Pos.CENTER_LEFT);
        ratingContainer.setPrefHeight(20);
        ratingContainer.setMaxHeight(20);
        ratingContainer.setMinHeight(20);
        
        double avgRating = courseReviewService.getCourseAverageRating(course.getCourseID());
        int reviewCount = courseReviewService.getCourseReviewCount(course.getCourseID());
        
        String ratingText = reviewCount > 0 ? 
            String.format("★ %.1f (%d)", avgRating, reviewCount) : 
            "☆ No reviews";
        Label ratingLabel = new Label(ratingText);
        ratingLabel.getStyleClass().add("rating-text");
        ratingLabel.setMaxWidth(206);
        ratingLabel.setStyle("-fx-text-overrun: ellipsis;");
        
        ratingContainer.getChildren().add(ratingLabel);
        
        // Price - fixed height
        Label price = new Label("$" + String.format("%.2f", course.getPrice()));
        price.getStyleClass().add("course-price");
        price.setPrefHeight(22);
        price.setMaxHeight(22);
        price.setMinHeight(22);
        
        // Course card actions (Cart and Wishlist buttons) - fixed height
        HBox actionsContainer = createCourseCardActions(course);
        actionsContainer.setPrefHeight(40);
        actionsContainer.setMaxHeight(40);
        actionsContainer.setMinHeight(40);
        
        courseInfo.getChildren().addAll(courseTitle, authorLabel, badgesContainer, ratingContainer, price, actionsContainer);
        courseCard.getChildren().addAll(imageContainer, courseInfo);
        
        return courseCard;
    }
      private void setDefaultCourseImage(ImageView imageView) {
        try {
            Image defaultImage = loadImageSafely("file:resources/images/default_image.png");
            if (defaultImage != null) {
                imageView.setImage(defaultImage);
            }
        } catch (Exception e) {
            System.err.println("Could not load default course image: " + e.getMessage());
        }
    }
    
    private HBox createCourseCardActions(Courses course) {
        HBox actionsContainer = new HBox();
        actionsContainer.getStyleClass().add("course-card-actions");        actionsContainer.setSpacing(8);
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
        wishlistButton.getStyleClass().addAll("course-card-icon", "wishlist-icon-default");
            // Load icons (using cached image loading to prevent memory leaks)
        try {
            // Use existing cart icon
            Image cartImage = loadImageSafely("file:resources/images/main_page/icon/MyCart.png");
            if (cartImage != null) {
                cartIconView.setImage(cartImage);
            }
            
            // Use existing wishlist icon  
            Image heartImage = loadImageSafely("file:resources/images/main_page/icon/MyWishList.png");
            if (heartImage != null) {
                wishlistIconView.setImage(heartImage);
            }
        } catch (Exception e) {
            System.err.println("Error loading course action icons: " + e.getMessage());
        }try {
            // Get current user ID (consistent with StudentMainPage approach)
            final int userId;
            Users currentUser = Session.getCurrentUser();
            if (currentUser != null) {
                userId = currentUser.getUserID();
            } else {
                userId = 1; // TODO: Get from actual user session
            }
            
            // Check initial states using StudentStateManager
            boolean isInCart = stateManager.isInCart(course.getCourseID());
            boolean isInWishlist = stateManager.isInWishlist(course.getCourseID());
            
            // Update initial icon states
            updateCartIconState(cartButton, isInCart);
            updateWishlistIconState(wishlistButton, isInWishlist);
            
            // Add event handlers
            cartButton.setOnAction(event -> {
                event.consume(); // Prevent triggering course card click
                handleCartToggle(userId, course, cartButton);
            });
            
            wishlistButton.setOnAction(event -> {
                event.consume(); // Prevent triggering course card click
                handleWishlistToggle(userId, course, wishlistButton);
            });
            
            actionsContainer.getChildren().addAll(cartButton, wishlistButton);
        } catch (Exception e) {
            System.err.println("Error setting up course card actions: " + e.getMessage());
        }
        
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
        }    }      private void handleCartToggle(int userId, Courses course, Button cartButton) {
        try {
            StudentStateManager stateManager = StudentStateManager.getInstance();
            boolean isCurrentlyInCart = stateManager.isInCart(course.getCourseID());
            System.out.println("StudentExplorePage: Cart toggle for course " + course.getCourseID() + ", currently in cart: " + isCurrentlyInCart);
            
            if (isCurrentlyInCart) {
                // Remove from cart using StateManager
                boolean removed = stateManager.removeFromCart(course.getCourseID());                if (removed) {
                    System.out.println("StudentExplorePage: Successfully removed " + course.getCourseName() + " from cart");
                    // Force immediate UI update for this button
                    Platform.runLater(() -> updateCartIconState(cartButton, false));
                } else {
                    System.err.println("StudentExplorePage: Failed to remove " + course.getCourseName() + " from cart");
                }
            } else {
                // Add to cart using StateManager
                boolean added = stateManager.addToCart(course.getCourseID());
                if (added) {
                    System.out.println("StudentExplorePage: Successfully added " + course.getCourseName() + " to cart");
                    // Force immediate UI update for this button
                    Platform.runLater(() -> updateCartIconState(cartButton, true));
                } else {
                    System.err.println("StudentExplorePage: Failed to add " + course.getCourseName() + " to cart");
                }
            }
            
        } catch (Exception e) {
            System.err.println("StudentExplorePage: Error toggling cart state: " + e.getMessage());
            e.printStackTrace();
        }
    }      private void handleWishlistToggle(int userId, Courses course, Button wishlistButton) {
        try {
            StudentStateManager stateManager = StudentStateManager.getInstance();
            boolean isCurrentlyInWishlist = stateManager.isInWishlist(course.getCourseID());
            System.out.println("StudentExplorePage: Wishlist toggle for course " + course.getCourseID() + ", currently in wishlist: " + isCurrentlyInWishlist);
            
            if (isCurrentlyInWishlist) {
                // Remove from wishlist using StateManager
                boolean removed = stateManager.removeFromWishlist(course.getCourseID());                if (removed) {
                    System.out.println("StudentExplorePage: Successfully removed " + course.getCourseName() + " from wishlist");
                    // Force immediate UI update for this button
                    Platform.runLater(() -> updateWishlistIconState(wishlistButton, false));
                } else {
                    System.err.println("StudentExplorePage: Failed to remove " + course.getCourseName() + " from wishlist");
                }
            } else {
                // Add to wishlist using StateManager
                boolean added = stateManager.addToWishlist(course.getCourseID());
                if (added) {
                    System.out.println("StudentExplorePage: Successfully added " + course.getCourseName() + " to wishlist");
                    // Force immediate UI update for this button
                    Platform.runLater(() -> updateWishlistIconState(wishlistButton, true));
                } else {
                    System.err.println("StudentExplorePage: Failed to add " + course.getCourseName() + " to wishlist");
                }
            }
            
        } catch (Exception e) {
            System.err.println("StudentExplorePage: Error toggling wishlist state: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void displayNoCoursesMessage() {
        VBox noCoursesBox = new VBox();
        noCoursesBox.getStyleClass().add("no-courses-message");
        noCoursesBox.setAlignment(Pos.CENTER);
        
        Label noCoursesText = new Label("No courses found matching your criteria.");
        noCoursesText.getStyleClass().add("no-courses-text");
        
        noCoursesBox.getChildren().add(noCoursesText);
        coursesContainer.getChildren().add(noCoursesBox);
    }

    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().trim().toLowerCase();
        System.out.println("DEBUG: handleSearch called with text: '" + searchText + "'");
        
        if (searchText.isEmpty()) {
            displayAllCourses();
            return;
        }
        
        System.out.println("DEBUG: Starting search for: '" + searchText + "'");
        coursesContainer.getChildren().clear();
        Map<Category, List<Courses>> filteredCourses = new HashMap<>();
        
        for (Category category : Category.values()) {
            filteredCourses.put(category, new ArrayList<>());
        }
        
        int totalMatches = 0;        for (Courses course : allCourses) {
            if (course.getCourseName().toLowerCase().contains(searchText) ||
                course.getCourseDescription().toLowerCase().contains(searchText) ||
                course.getCategory().name().toLowerCase().replace("_", " ").contains(searchText)) {
                
                filteredCourses.get(course.getCategory()).add(course);
                totalMatches++;
            }
        }
        
        System.out.println("DEBUG: Found " + totalMatches + " courses matching '" + searchText + "'");
        
        for (Category category : Category.values()) {
            List<Courses> courses = filteredCourses.get(category);
            if (courses != null && !courses.isEmpty()) {
                VBox categorySection = createCategorySection(category, courses);
                coursesContainer.getChildren().add(categorySection);
            }
        }
        
        if (coursesContainer.getChildren().isEmpty()) {
            displayNoCoursesMessage();
        } else {
            System.out.println("DEBUG: Displayed filtered courses successfully");
        }
    }

    @FXML
    private void handleCategoryFilter() {
        String selectedCategory = categoryFilter.getValue();
        if (selectedCategory == null || selectedCategory.equals("All Categories")) {
            displayAllCourses();
            return;
        }
        
        coursesContainer.getChildren().clear();
        
        // Find the matching category
        for (Category category : Category.values()) {
            if (formatCategoryName(category.name()).equals(selectedCategory)) {
                List<Courses> courses = coursesByCategory.get(category);
                if (courses != null && !courses.isEmpty()) {
                    VBox categorySection = createCategorySection(category, courses);
                    coursesContainer.getChildren().add(categorySection);
                }
                break;
            }
        }
        
        if (coursesContainer.getChildren().isEmpty()) {
            displayNoCoursesMessage();
        }
    }

    @FXML
    private void clearFilters() {
        categoryFilter.setValue("All Categories");
        searchField.clear();
        displayAllCourses();
    }    @FXML    private void handleCourseClick(Courses course) {
        try {
            System.out.println("Course clicked: " + course.getCourseName());
            // Navigate to StudentCourseDetailPage instead of instructor CourseDetailPage
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
            System.err.println("Error navigating to course detail: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
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
    }    @FXML
    private void goToMainPage() {
        try {
            SceneManager.switchSceneWithRefresh("Student Main Page", "/frontend/view/studentMainPage/studentMainPage.fxml");
        } catch (Exception e) {
            System.err.println("Error navigating to main page: " + e.getMessage());
        }
    }

    // Methods to handle profile menu item actions
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
    
    private void showSettings() {
        // Placeholder for settings functionality
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Settings");
        alert.setHeaderText("Settings Page");
        alert.setContentText("Settings functionality will be implemented in a future update.");
        alert.showAndWait();
    }
    
    private void logout() {
        try {
            // Show confirmation dialog
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Logout Confirmation");
            confirmAlert.setHeaderText("Are you sure you want to logout?");
            confirmAlert.setContentText("You will be redirected to the login page.");
            
            Optional<ButtonType> result = confirmAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                System.out.println("User logging out...");
                
                // Clear session if there's a session management system
                // Session.clearSession(); // Uncomment if you have session management
                System.out.println("Session cleared"); // Debugging line
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
    }    /**     * Refresh all course card states (cart and wishlist icons) to sync with current database state
     */    private void refreshCourseStates() {
        try {
            System.out.println("Refreshing StudentExplorePage course states...");
              // Re-initialize state from database to ensure synchronization
            stateManager.initializeState();
            
            // Cart badge feature disabled
            
            // Refresh course card states by redisplaying all courses
            displayAllCourses();
            
            System.out.println("StudentExplorePage course states refreshed successfully");
            
        } catch (Exception e) {
            System.err.println("Error refreshing StudentExplorePage course states: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Method to be called when returning to this page from another page
     * to ensure cart/wishlist states are synchronized
     */
    public void refreshPageData() {
        System.out.println("StudentExplorePage refreshPageData called");
        refreshCourseStates();
    }    /**
     * Setup StudentStateManager listeners for synchronization across pages
     */
    private void setupStateManager() {
        try {
            // Register listener for cart state changes
            stateManager.addCartStateListener(cartIds -> {
                Platform.runLater(() -> updateCourseCardStates());
            });
            
            // Register listener for wishlist state changes
            stateManager.addWishlistStateListener(wishlistIds -> {
                Platform.runLater(() -> updateCourseCardStates());
            });
            
            System.out.println("StudentExplorePageController: State manager listeners registered");
            
        } catch (Exception e) {
            System.err.println("Error setting up state manager in StudentExplorePageController: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Update all course card states by refreshing the display
     */
    private void updateCourseCardStates() {
        try {
            displayAllCourses();
        } catch (Exception e) {            System.err.println("Error updating course card states in StudentExplorePage: " + e.getMessage());
        }
    }
    
    /**
     * Apply category filter when coming from main page View All links
     */
    private void applyCategoryFilter(String filterValue) {
        try {
            // Map the filter value to display text for the combo box
            String displayValue = null;
            
            if ("AI_ML".equals(filterValue)) {
                // For AI & Machine Learning, we need to find the matching category
                displayValue = "Artificial Intelligence"; // or find the best match
            } else if ("Game_Development".equals(filterValue)) {
                displayValue = "Game Development";
            }
            
            if (displayValue != null && categoryFilter != null) {
                // Set the combo box value
                categoryFilter.setValue(displayValue);
                // Trigger the filter action
                handleCategoryFilter();
                System.out.println("Applied category filter: " + displayValue);
            }
        } catch (Exception e) {
            System.err.println("Error applying category filter: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
