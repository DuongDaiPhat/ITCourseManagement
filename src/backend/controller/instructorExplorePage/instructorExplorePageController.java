package backend.controller.instructorExplorePage;

import backend.controller.scene.SceneManager;
import backend.service.course.CourseService;
import backend.service.user.UserService;
import backend.service.course.CourseReviewService;
import backend.service.sample.SampleDataService;
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
import model.course.Category;
import model.course.Courses;
import model.course.Language;
import model.course.Technology;
import model.course.Level;
import model.user.Users;

import java.io.File;
import java.net.URL;
import java.util.*;

public class instructorExplorePageController implements Initializable {

    @FXML private TextField searchField;
    @FXML private Button searchButton;
    @FXML private Button profileButton;
    @FXML private ComboBox<String> categoryFilter;
    @FXML private Button clearFiltersButton;    @FXML private VBox coursesContainer;
    @FXML private ScrollPane myScrollPane;
      // Navigation Labels  
    @FXML private Label createCourseLabel;
    @FXML private Label myCoursesLabel;

    private ObservableList<Courses> allCourses;
    private Map<Category, List<Courses>> coursesByCategory;    private CourseService courseService;
    private UserService userService;
    private CourseReviewService courseReviewService;
    private ContextMenu profileMenu;
      // Method để nhận từ khóa tìm kiếm từ trang khác
    public void setSearchKeyword(String keyword) {
        System.out.println("DEBUG: setSearchKeyword called with: '" + keyword + "'");
        if (keyword != null && !keyword.trim().isEmpty()) {
            // Set text vào search field
            searchField.setText(keyword.trim());
            System.out.println("DEBUG: Search field set to: '" + searchField.getText() + "'");
            
            // Gọi handleSearch để thực hiện tìm kiếm
            handleSearch();
            System.out.println("DEBUG: handleSearch() called, search completed");
        } else {
            System.out.println("DEBUG: Keyword is null or empty, displaying all courses");
            displayAllCourses();
        }
    }    @Override    public void initialize(URL location, ResourceBundle resources) {
        courseService = new CourseService();
        userService = new UserService();
        courseReviewService = new CourseReviewService();
        setupProfileMenu();
        setupCategoryFilter();
        setupNavigationEvents();
        // debugImagePaths(); // Enable for debugging image issues
        loadCoursesFromDatabase();
        
        // Create sample lectures if needed
        SampleDataService sampleDataService = new SampleDataService();
        if (sampleDataService.needsampleLectures()) {
            System.out.println("Creating sample lectures...");
            sampleDataService.createSampleLectures();
        }
        
        displayAllCourses();
    }

    private void setupNavigationEvents() {
        createCourseLabel.setOnMouseClicked(event -> goToCreateCourse());
        myCoursesLabel.setOnMouseClicked(event -> goToMainPage());
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
        profileMenu.getStyleClass().add("ProfileMenu.css");
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
            // Load courses from database using CourseService
            List<Courses> coursesFromDB = courseService.getAllCourses();
            
            // Filter only approved and published courses
            allCourses = FXCollections.observableArrayList();
            coursesByCategory = new HashMap<>();
            
            // Initialize category map
            for (Category category : Category.values()) {
                coursesByCategory.put(category, new ArrayList<>());
            }            // Add courses to appropriate categories
            for (Courses course : coursesFromDB) {
                if (course.isApproved() && course.isPublished()) {
                    // Only set default if thumbnailURL is truly null or empty
                    // Don't override existing URLs (which might be absolute paths)
                    if (course.getThumbnailURL() == null || course.getThumbnailURL().trim().isEmpty()) {
                        course.setThumbnailURL("/images/default_image.png");
                        System.out.println("Course " + course.getCourseName() + " has no thumbnail, using default");
                    }
                    
                    allCourses.add(course);
                    coursesByCategory.get(course.getCategory()).add(course);
                }
            }
            
            // If no courses found, create sample data for demonstration
            if (allCourses.isEmpty()) {
                System.out.println("No courses found in database, using sample data...");
                createSampleCourses();
            }
            
        } catch (Exception e) {
            System.err.println("Error loading courses from database: " + e.getMessage());
            e.printStackTrace();
            
            // Fallback to sample data
            allCourses = FXCollections.observableArrayList();
            coursesByCategory = new HashMap<>();
            
            // Initialize category map
            for (Category category : Category.values()) {
                coursesByCategory.put(category, new ArrayList<>());
            }
            
            createSampleCourses();
        }
    }    private void createSampleCourses() {
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
        Language[] languages = {Language.English, Language.Vietnamese, Language.Japanese, Language.English, Language.Vietnamese, Language.English, Language.Japanese, Language.English, Language.Vietnamese, Language.English, Language.Japanese, Language.Vietnamese};
        Technology[] technologies = {Technology.Python, Technology.JavaScript, Technology.Python, Technology.Java, Technology.Python, Technology.Kotlin, Technology.CSharp, Technology.GoLang, Technology.Python, Technology.CSharp, Technology.JavaScript, Technology.Java};
        Level[] levels = {Level.BEGINNER, Level.INTERMEDIATE, Level.BEGINNER, Level.ADVANCED, Level.INTERMEDIATE, Level.INTERMEDIATE, Level.BEGINNER, Level.BEGINNER, Level.ADVANCED, Level.INTERMEDIATE, Level.BEGINNER, Level.INTERMEDIATE};
        
        for (int i = 0; i < courseNames.length; i++) {
            Courses course = new Courses();
            course.setCourseID(i + 1);
            course.setCourseName(courseNames[i]);
            course.setCategory(categories[i % categories.length]);
            course.setPrice(49.99f + (i * 10));
            course.setThumbnailURL(thumbnails[i]);
            course.setCourseDescription("A comprehensive course covering " + courseNames[i].toLowerCase());
            course.setApproved(true);
            course.setPublish(true);
            
            // Set language, technology, and level attributes
            course.setLanguage(languages[i]);
            course.setTechnology(technologies[i]);
            course.setLevel(levels[i]);
            
            allCourses.add(course);
            coursesByCategory.get(course.getCategory()).add(course);
        }
    }

    private void displayAllCourses() {
        coursesContainer.getChildren().clear();
        
        for (Category category : Category.values()) {
            List<Courses> coursesInCategory = coursesByCategory.get(category);
            if (!coursesInCategory.isEmpty()) {
                VBox categorySection = createCategorySection(category, coursesInCategory);
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
        
        // Set consistent dimensions for 4 cards per row layout
        courseCard.setPrefWidth(230);
        courseCard.setMaxWidth(230);
        courseCard.setMinWidth(230);
        courseCard.setPrefHeight(340);
        courseCard.setMaxHeight(340);
        courseCard.setAlignment(javafx.geometry.Pos.TOP_LEFT);        courseCard.setSpacing(0); // Remove spacing since we'll handle it in containers
        
        // Image container with proper padding and centering
        VBox imageContainer = new VBox();
        imageContainer.setAlignment(Pos.CENTER);
        imageContainer.setStyle("-fx-padding: 10 10 0 10; -fx-background-radius: 12 12 0 0;");
        imageContainer.setPrefHeight(150); // Fixed height for image section
        imageContainer.setMaxHeight(150);
        imageContainer.setMinHeight(150);
        
        // Course thumbnail with standardized aspect ratio matching StudentMainPage
        ImageView thumbnail = new ImageView();
        thumbnail.getStyleClass().add("course-thumbnail");
        thumbnail.setFitWidth(210);  // Standardized width to match StudentMainPage
        thumbnail.setFitHeight(130); // Standardized height to match StudentMainPage (210:130 = 1.62:1 ratio)
        thumbnail.setPreserveRatio(false); // Allow stretching to maintain consistent thumbnail aspect ratio
        thumbnail.setSmooth(true);try {
            Image image = null;
            String thumbnailURL = course.getThumbnailURL();
            
            // Check if thumbnailURL is valid
            if (thumbnailURL == null || thumbnailURL.trim().isEmpty()) {
                throw new Exception("No thumbnail URL provided");
            }
            
            // Try different loading strategies based on the URL format
            if (thumbnailURL.startsWith("/user_data/")) {
                // Strategy 1: Load from file system using project root + relative path
                try {
                    String projectRoot = System.getProperty("user.dir");
                    String relativePath = thumbnailURL.substring(1); // Remove leading slash
                    File imageFile = new File(projectRoot, relativePath);
                    
                    if (imageFile.exists() && imageFile.isFile()) {
                        String imageURI = imageFile.toURI().toString();
                        image = new Image(imageURI);
                        if (image.isError()) {
                            throw new Exception("JavaFX failed to load image from URI: " + imageURI);
                        }
                    } else {
                        throw new Exception("File does not exist: " + imageFile.getAbsolutePath());
                    }
                } catch (Exception fileException) {
                    throw fileException;
                }
            } else if (thumbnailURL.startsWith("/") && (thumbnailURL.contains("/images/") || thumbnailURL.contains("/resources/"))) {
                // Strategy 2: Load from classpath (resources folder)
                try {
                    var inputStream = getClass().getResourceAsStream(thumbnailURL);
                    if (inputStream == null) {
                        throw new Exception("Resource not found in classpath: " + thumbnailURL);
                    }
                    
                    image = new Image(inputStream);
                    if (image == null || image.isError()) {
                        throw new Exception("Failed to create Image from classpath resource: " + thumbnailURL);
                    }
                } catch (Exception classpathException) {
                    throw classpathException;
                }
            } else {
                // Strategy 3: Treat as absolute file path (like InstructorMainPage does)
                try {
                    File imageFile = new File(thumbnailURL);
                    if (imageFile.exists() && imageFile.isFile()) {
                        image = new Image(imageFile.toURI().toString());
                        if (image.isError()) {
                            throw new Exception("Failed to load from absolute path: " + thumbnailURL);
                        }
                    } else {
                        throw new Exception("Absolute path does not exist: " + thumbnailURL);
                    }
                } catch (Exception absolutePathException) {
                    throw absolutePathException;
                }
            }
            
            if (image != null && !image.isError()) {
                thumbnail.setImage(image);
            } else {
                throw new Exception("Image failed to load");
            }
        } catch (Exception e) {
            // Use default image if thumbnail not found - only show error for debugging
            // System.err.println("Failed to load thumbnail for " + course.getCourseName() + ": " + e.getMessage());
            try {
                Image defaultImage = new Image(getClass().getResourceAsStream("/images/default_image.png"));
                if (defaultImage != null && !defaultImage.isError()) {
                    thumbnail.setImage(defaultImage);
                } else {
                    // Create a placeholder if no image available
                    thumbnail.setStyle("-fx-background-color: #e9ecef;");
                }
            } catch (Exception ex) {
                // Create a placeholder if no image available
                thumbnail.setStyle("-fx-background-color: #e9ecef;");
            }        }
        
        // Add thumbnail to image container
        imageContainer.getChildren().add(thumbnail);
        
        // Course info container with fixed spacing and layout
        VBox courseInfo = new VBox();
        courseInfo.getStyleClass().add("course-info");
        courseInfo.setSpacing(8);
        courseInfo.setAlignment(Pos.TOP_LEFT);
        courseInfo.setPrefHeight(180); // Fixed height for content section
        courseInfo.setMaxHeight(180);
        courseInfo.setMinHeight(180);
        
        // Course title - fixed height with ellipsis for long text
        Label courseTitle = new Label(course.getCourseName());
        courseTitle.getStyleClass().add("course-title");
        courseTitle.setWrapText(false);  // Disable wrapping to use ellipsis
        courseTitle.setMaxWidth(206);
        courseTitle.setPrefHeight(20);  // Fixed height
        courseTitle.setMaxHeight(20);
        courseTitle.setMinHeight(20);
        courseTitle.setStyle("-fx-text-overrun: ellipsis;"); // Show ellipsis for long text
        
        // Author name - get real instructor name from database with fixed height
        String authorName = "By Instructor";
        try {
            Users instructor = userService.GetUserByID(course.getUserID());
            if (instructor != null) {
                authorName = "By " + instructor.getUserFirstName() + " " + instructor.getUserLastName();
            }
        } catch (Exception e) {
            System.err.println("Error getting instructor info: " + e.getMessage());
            authorName = "By Instructor " + course.getUserID();
        }
        
        Label authorLabel = new Label(authorName);
        authorLabel.getStyleClass().add("course-author");
        authorLabel.setPrefHeight(18);  // Fixed height
        authorLabel.setMaxHeight(18);
        authorLabel.setMinHeight(18);
        authorLabel.setMaxWidth(206);
        authorLabel.setStyle("-fx-text-overrun: ellipsis;");
        
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
        
        // Price and rating container - fixed position
        HBox priceRatingContainer = new HBox();
        priceRatingContainer.setAlignment(Pos.CENTER_LEFT);
        priceRatingContainer.setSpacing(10);
        priceRatingContainer.setPrefHeight(25); // Fixed height
        priceRatingContainer.setMaxHeight(25);
        priceRatingContainer.setMinHeight(25);
        
        Label price = new Label("$" + String.format("%.2f", course.getPrice()));
        price.getStyleClass().add("course-price");
        
        // Rating - Get real rating from database
        HBox ratingContainer = new HBox();
        ratingContainer.getStyleClass().add("course-rating");
        ratingContainer.setAlignment(Pos.CENTER_LEFT);
        
        double avgRating = courseReviewService.getCourseAverageRating(course.getCourseID());
        int reviewCount = courseReviewService.getCourseReviewCount(course.getCourseID());
        
        String ratingText = reviewCount > 0 ? 
            String.format("★ %.1f", avgRating) : 
            "☆ No rating";
        Label ratingLabel = new Label(ratingText);
        ratingLabel.getStyleClass().add("rating-text");
        
        String countText = reviewCount > 0 ? 
            String.format("(%d)", reviewCount) : 
            "(0)";
        Label ratingCount = new Label(countText);
        ratingCount.getStyleClass().add("rating-count");        
        ratingContainer.getChildren().addAll(ratingLabel, ratingCount);
        priceRatingContainer.getChildren().addAll(price, ratingContainer);
          courseInfo.getChildren().addAll(courseTitle, authorLabel, badgesBox, priceRatingContainer);
        courseCard.getChildren().addAll(imageContainer, courseInfo);
        
        return courseCard;
    }

    private void displayNoCoursesMessage() {
        VBox noCoursesBox = new VBox();
        noCoursesBox.getStyleClass().add("no-courses-message");
        noCoursesBox.setAlignment(Pos.CENTER);
        
        Label noCoursesText = new Label("No courses found matching your criteria.");
        noCoursesText.getStyleClass().add("no-courses-text");
        
        noCoursesBox.getChildren().add(noCoursesText);
        coursesContainer.getChildren().add(noCoursesBox);
    }    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().trim().toLowerCase();
        System.out.println("DEBUG: handleSearch called with text: '" + searchText + "'");
        
        if (searchText.isEmpty()) {
            System.out.println("DEBUG: Search text is empty, displaying all courses");
            displayAllCourses();
            return;
        }
        
        System.out.println("DEBUG: Starting search for: '" + searchText + "'");
        coursesContainer.getChildren().clear();
        Map<Category, List<Courses>> filteredCourses = new HashMap<>();
        
        for (Category category : Category.values()) {
            filteredCourses.put(category, new ArrayList<>());
        }
        
        int totalMatches = 0;
        for (Courses course : allCourses) {
            if (course.getCourseName().toLowerCase().contains(searchText) ||
                course.getCourseDescription().toLowerCase().contains(searchText) ||
                formatCategoryName(course.getCategory().name()).toLowerCase().contains(searchText)) {
                filteredCourses.get(course.getCategory()).add(course);
                totalMatches++;
            }
        }
        
        System.out.println("DEBUG: Found " + totalMatches + " courses matching '" + searchText + "'");
        
        for (Category category : Category.values()) {
            List<Courses> coursesInCategory = filteredCourses.get(category);
            if (!coursesInCategory.isEmpty()) {
                VBox categorySection = createCategorySection(category, coursesInCategory);
                coursesContainer.getChildren().add(categorySection);
                System.out.println("DEBUG: Added " + coursesInCategory.size() + " courses from category " + category.name());
            }
        }
        
        if (coursesContainer.getChildren().isEmpty()) {
            System.out.println("DEBUG: No matches found, displaying no courses message");
            displayNoCoursesMessage();
        } else {
            System.out.println("DEBUG: Search completed successfully");
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
                List<Courses> coursesInCategory = coursesByCategory.get(category);
                if (!coursesInCategory.isEmpty()) {
                    VBox categorySection = createCategorySection(category, coursesInCategory);
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
    }    @FXML
    private void handleCourseClick(Courses course) {
        System.out.println("Clicked on course: " + course.getCourseName());
        
        // Navigate to course detail page with course data
        SceneManager.switchSceneReloadWithData(
            "Course Detail",
            "/frontend/view/courseDetailPage/CourseDetailPage.fxml",
            (controller, courseData) -> {
                if (controller instanceof backend.controller.courseDetailPage.CourseDetailPageController) {
                    ((backend.controller.courseDetailPage.CourseDetailPageController) controller).setCourseData((Courses) courseData);
                }
            },
            course
        );
    }

    @FXML
    private void showProfileMenu() {
        profileMenu.show(profileButton, profileButton.localToScreen(0, profileButton.getHeight()).getX(), 
                     profileButton.localToScreen(0, profileButton.getHeight()).getY());
    }    @FXML
    private void goToMainPage() {
        // Use reload to ensure fresh data is displayed
        SceneManager.switchSceneReloadWithData("Instructor Main", "/frontend/view/instructorMainPage/instructorMainPage.fxml", null, null);
    }@FXML
    private void goToCreateCourse() {
        SceneManager.switchSceneReloadWithData("Create Course", "/frontend/view/instructorCreatePage/instructorCreatePage.fxml", null, null);
    }    @FXML
    private void goToMyCourses() {
        // Use reload to ensure fresh data is displayed
        SceneManager.switchSceneReloadWithData("Instructor Main", "/frontend/view/instructorMainPage/instructorMainPage.fxml", null, null);
    }

    // Methods to handle profile menu item actions
    private void showProfileInfo() {
        SceneManager.switchScene("My Information", "/frontend/view/UserProfile/UserProfile.fxml");
    }
    
    private void showPaymentMethods() {
        System.out.println("Opening payment methods...");
    }
      private void logout() {        SceneManager.clearSceneCache();
        SceneManager.switchToLoginScene("Login", "/frontend/view/login/Login.fxml");
    }
}
