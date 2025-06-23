package backend.controller.courseDetailPage;

import backend.controller.scene.SceneManager;
import backend.repository.course.CourseReviewRepository;
import backend.service.course.CourseReviewService;
import backend.service.course.CourseService;
import backend.service.lecture.LectureService;
import backend.service.user.UserService;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;
import model.course.Courses;
import model.course.CourseReview;
import model.lecture.Lecture;
import model.user.Session;
import model.user.Users;

import java.io.File;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class CourseDetailPageController implements Initializable {
    
    @FXML private TextField searchField;
    @FXML private Button searchButton;
    @FXML private Button profileButton;
    @FXML private Label exploreLabel;
    @FXML private Label myCoursesLabel;
    @FXML private Label createCourseLabel;
    
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
    @FXML private Label createdDate;
    @FXML private Label updatedDate;
    @FXML private Label totalLectures;
    @FXML private VBox lecturesContainer;
      // Rating and Review Elements
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
    
    // Main content container
    @FXML private VBox mainContent;    // Video Player Elements
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
    @FXML private Button fullscreenBtn;
    
    // Services and Data
    private CourseService courseService;
    private UserService userService;
    private LectureService lectureService;
    private CourseReviewService courseReviewService;
    private ContextMenu profileMenu;
    private Courses currentCourse;
    private boolean isOwnedByCurrentUser = false;
    
    // Video Player
    private MediaPlayer mediaPlayer;
    private MediaView mediaView;    private double[] playbackSpeeds = {0.5, 0.75, 1.0, 1.25, 1.5, 2.0};
    private int currentSpeedIndex = 2; // Default 1.0x
    private boolean isPlaying = false;
    private boolean isVideoFullscreen = false;
      @Override
    public void initialize(URL location, ResourceBundle resources) {
        courseService = new CourseService();
        userService = new UserService();
        lectureService = new LectureService();
        courseReviewService = new CourseReviewService();
        setupProfileMenu();
        setupNavigationEvents();
        initializeVideoPlayer();
        setupReviewFilterButtons();
    }    // Call this after setting currentCourse
    private void loadCourseReviews() {
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
    }private void setupReviewFilterButtons() {
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
        displayReviewsWithNames(filtered);
        setActiveFilterButton(star);
    }private void setActiveFilterButton(int star) {
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

    // Method to set course data when navigating from other pages
    public void setCourseData(Courses course) {
        this.currentCourse = course;
        
        // Check if this course is owned by current user
        try {
            Users currentUser = Session.getCurrentUser();
            if (currentUser != null && currentUser.getUserID() == course.getUserID()) {
                isOwnedByCurrentUser = true;
            }
        } catch (Exception e) {
            System.err.println("Error checking course ownership: " + e.getMessage());
        }
        
        loadCourseDetails();
        loadLectures();
        loadCourseReviews();
    }
    
    private void setupNavigationEvents() {
        exploreLabel.setOnMouseClicked(event -> goToExplorePage());
        myCoursesLabel.setOnMouseClicked(event -> goToMainPage());
        createCourseLabel.setOnMouseClicked(event -> goToCreateCourse());
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
    
    private void loadCourseDetails() {
        if (currentCourse == null) return;
        
        // Set course title and description
        courseTitle.setText(currentCourse.getCourseName());
        courseDescription.setText(currentCourse.getCourseDescription());
        
        // Set course properties
        courseCategory.setText(formatCategoryName(currentCourse.getCategory().name()));
        courseLevel.setText(currentCourse.getLevel() != null ? currentCourse.getLevel().name() : "Not specified");
        courseLanguage.setText(currentCourse.getLanguage() != null ? currentCourse.getLanguage().name() : "Not specified");
        courseTechnology.setText(currentCourse.getTechnology() != null ? currentCourse.getTechnology().name() : "Not specified");
        coursePrice.setText(String.format("$%.2f", currentCourse.getPrice()));
        
        // Set dates
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        if (currentCourse.getCreatedAt() != null) {
            createdDate.setText(currentCourse.getCreatedAt().format(formatter));
        }
        if (currentCourse.getUpdatedAt() != null) {
            updatedDate.setText(currentCourse.getUpdatedAt().format(formatter));
        }
        
        // Load instructor information
        loadInstructorInfo();
        
        // Load course thumbnail
        loadCourseThumbnail();
    }
    
    private void loadInstructorInfo() {
        try {
            Users instructor = userService.GetUserByID(currentCourse.getUserID());
            if (instructor != null) {
                instructorName.setText(instructor.getUserFirstName() + " " + instructor.getUserLastName());
            } else {
                instructorName.setText("Unknown Instructor");
            }
        } catch (Exception e) {
            System.err.println("Error loading instructor info: " + e.getMessage());
            instructorName.setText("Error loading instructor");
        }
    }
    
    private void loadCourseThumbnail() {
        try {
            String thumbnailURL = currentCourse.getThumbnailURL();
            Image image = null;
            
            if (thumbnailURL != null && !thumbnailURL.trim().isEmpty()) {
                // Use same image loading strategy as Explore page
                if (thumbnailURL.startsWith("/user_data/")) {
                    String projectRoot = System.getProperty("user.dir");
                    String relativePath = thumbnailURL.substring(1);
                    File imageFile = new File(projectRoot, relativePath);
                    
                    if (imageFile.exists() && imageFile.isFile()) {
                        image = new Image(imageFile.toURI().toString());
                    }
                } else if (thumbnailURL.startsWith("/") && thumbnailURL.contains("/images/")) {
                    var inputStream = getClass().getResourceAsStream(thumbnailURL);
                    if (inputStream != null) {
                        image = new Image(inputStream);
                    }
                } else {
                    File imageFile = new File(thumbnailURL);
                    if (imageFile.exists() && imageFile.isFile()) {
                        image = new Image(imageFile.toURI().toString());
                    }
                }
            }
            
            // Use default image if thumbnail not found
            if (image == null || image.isError()) {
                image = new Image(getClass().getResourceAsStream("/images/default_image.png"));
            }
            
            if (image != null && !image.isError()) {
                courseThumbnail.setImage(image);
            } else {
                courseThumbnail.setStyle("-fx-background-color: #e9ecef;");
            }
            
        } catch (Exception e) {
            System.err.println("Error loading course thumbnail: " + e.getMessage());
            courseThumbnail.setStyle("-fx-background-color: #e9ecef;");
        }
    }
      private void loadLectures() {
        lecturesContainer.getChildren().clear();
        
        try {
            // Get lectures for this course using LectureService
            ArrayList<Lecture> lectures = lectureService.getLecturesByCourseID(currentCourse.getCourseID());
            
            if (lectures.isEmpty()) {
                displayNoLecturesMessage();
                totalLectures.setText("0");
                return;
            }
            
            totalLectures.setText(String.valueOf(lectures.size()));
            
            // Create lecture cards
            for (int i = 0; i < lectures.size(); i++) {
                Lecture lecture = lectures.get(i);
                VBox lectureCard = createLectureCard(lecture, i + 1);
                lecturesContainer.getChildren().add(lectureCard);
            }
            
        } catch (Exception e) {
            System.err.println("Error loading lectures: " + e.getMessage());
            displayErrorMessage("Error loading lectures: " + e.getMessage());
            totalLectures.setText("Error");
        }
    }
      private VBox createLectureCard(Lecture lecture, int lectureNumber) {
        VBox lectureCard = new VBox();
        lectureCard.getStyleClass().add("lecture-card");
        
        // Add owned/restricted style
        if (isOwnedByCurrentUser) {
            lectureCard.getStyleClass().add("owned");
        } else {
            lectureCard.getStyleClass().add("restricted");
        }
        
        // Lecture number and title
        Label titleLabel = new Label("Lecture " + lectureNumber + ": " + lecture.getLectureName());
        titleLabel.getStyleClass().add("lecture-title");
        
        // Lecture description
        Label descriptionLabel = new Label(lecture.getLectureDescription());
        descriptionLabel.getStyleClass().add("lecture-description");
        
        // Duration
        Label durationLabel = new Label("Duration: " + lecture.getDuration() + " minutes");
        durationLabel.getStyleClass().add("lecture-duration");
        
        lectureCard.getChildren().addAll(titleLabel, descriptionLabel, durationLabel);
        
        // Video information or restriction message
        if (isOwnedByCurrentUser) {
            // Show video info for owned courses
            if (lecture.getVideoURL() != null && !lecture.getVideoURL().trim().isEmpty()) {
                Label videoInfoLabel = new Label("📹 Click to watch video");
                videoInfoLabel.getStyleClass().add("lecture-video-info");
                lectureCard.getChildren().add(videoInfoLabel);
                  // Add click handler for video playback
                lectureCard.setOnMouseClicked(event -> {
                    String videoURL = lecture.getVideoURL();
                    if (videoURL != null && !videoURL.trim().isEmpty()) {
                        playVideo(videoURL, "Lecture " + lectureNumber + ": " + lecture.getLectureName());
                    } else {
                        // Show demo video placeholder
                        showDemoVideoPlaceholder("Lecture " + lectureNumber + ": " + lecture.getLectureName());
                    }
                });
            } else {
                Label noVideoLabel = new Label("📹 No video uploaded yet");
                noVideoLabel.getStyleClass().add("lecture-video-info");
                lectureCard.getChildren().add(noVideoLabel);
            }
        } else {
            // Show restriction message for other users' courses
            Label restrictionLabel = new Label("🔒 Video content available after enrollment");
            restrictionLabel.getStyleClass().add("lecture-restricted-info");
            lectureCard.getChildren().add(restrictionLabel);
        }
        
        return lectureCard;
    }
    
    private void displayNoLecturesMessage() {
        VBox noLecturesBox = new VBox();
        noLecturesBox.getStyleClass().add("no-lectures-message");
        noLecturesBox.setAlignment(Pos.CENTER);
        
        Label noLecturesText = new Label("No lectures available for this course yet.");
        noLecturesText.getStyleClass().add("no-lectures-text");
        
        noLecturesBox.getChildren().add(noLecturesText);
        lecturesContainer.getChildren().add(noLecturesBox);
    }
    
    private void displayErrorMessage(String errorMessage) {
        VBox errorBox = new VBox();
        errorBox.getStyleClass().add("no-lectures-message");
        errorBox.setAlignment(Pos.CENTER);
        
        Label errorText = new Label(errorMessage);
        errorText.getStyleClass().add("no-lectures-text");
        
        errorBox.getChildren().add(errorText);
        lecturesContainer.getChildren().add(errorBox);
    }
    
    private String formatCategoryName(String categoryName) {
        return categoryName.replace("_", " ");
    }
    
    // Navigation methods
    @FXML
    private void handleSearch() {
        String searchKeyword = searchField.getText().trim();
        if (searchKeyword.isEmpty()) {
            goToExplorePage();
        } else {
            SceneManager.switchSceneReloadWithData(
                "Instructor Explore",
                "/frontend/view/instructorExplorePage/InstructorExplorePage.fxml",
                (controller, keyword) -> {
                    if (controller instanceof backend.controller.instructorExplorePage.instructorExplorePageController) {
                        ((backend.controller.instructorExplorePage.instructorExplorePageController) controller).setSearchKeyword((String) keyword);
                    }
                },
                searchKeyword
            );
        }
    }
    
    @FXML
    private void showProfileMenu() {
        profileMenu.show(profileButton, profileButton.localToScreen(0, profileButton.getHeight()).getX(), 
                     profileButton.localToScreen(0, profileButton.getHeight()).getY());
    }
      @FXML
    private void goToExplorePage() {
        // Use reload to ensure fresh course data is displayed
        SceneManager.switchSceneReloadWithData("Instructor Explore", "/frontend/view/instructorExplorePage/InstructorExplorePage.fxml", null, null);
    }
      @FXML
    private void goToMainPage() {
        // Use reload to ensure fresh data is displayed
        SceneManager.switchSceneReloadWithData("Instructor Main", "/frontend/view/instructorMainPage/instructorMainPage.fxml", null, null);
    }
    
    @FXML
    private void goToCreateCourse() {
        SceneManager.switchSceneReloadWithData("Create Course", "/frontend/view/instructorCreatePage/instructorCreatePage.fxml", null, null);
    }
    
    // Profile menu actions
    private void showProfileInfo() {
        SceneManager.switchScene("My Information", "/frontend/view/UserProfile/UserProfile.fxml");
    }
    
    private void showPaymentMethods() {
        System.out.println("Opening payment methods...");
    }
    
    private void logout() {
        SceneManager.clearSceneCache();
        SceneManager.switchScene("Login", "/frontend/view/login/Login.fxml");
    }
      private void initializeVideoPlayer() {
        // Initialize video player UI components
        videoOverlay.setVisible(false);
        videoOverlay.setManaged(false);
        videoTitle.setText("");
        closeVideoBtn.setVisible(true);
        videoPlaceholder.setText("No video selected");
        playPauseBtn.setText("▶");
        currentTimeLabel.setText("00:00");
        totalTimeLabel.setText("00:00");
        speedBtn.setText("1.0x");
        volumeSlider.setValue(0.5);
        volumeDownBtn.setVisible(true);
        volumeUpBtn.setVisible(true);
        fullscreenBtn.setVisible(true);
        
        // Set up keyboard shortcuts for video controls
        setupVideoKeyboardShortcuts();
        
        // MediaPlayer and MediaView are set up in playVideo()
    }
    
    private void setupVideoKeyboardShortcuts() {
        // This will be called when video overlay becomes visible
        // Space bar for play/pause, arrow keys for seek, etc.
    }private void playVideo(String videoURL, String title) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        
        // Clear any existing MediaView
        if (mediaView != null) {
            videoPlayerArea.getChildren().remove(mediaView);
        }
        
        try {
            Media media = null;
            
            // Handle different types of video URLs
            if (videoURL.startsWith("http://") || videoURL.startsWith("https://")) {
                // Online video URL - use directly
                media = new Media(videoURL);
            } else if (videoURL.startsWith("/")) {
                // Relative path from resources
                String resourcePath = videoURL;
                var resourceStream = getClass().getResourceAsStream(resourcePath);
                if (resourceStream != null) {
                    // For resources, we need to get the actual URL
                    var resourceURL = getClass().getResource(resourcePath);
                    if (resourceURL != null) {
                        media = new Media(resourceURL.toString());
                    } else {
                        throw new Exception("Resource not found: " + resourcePath);
                    }
                } else {
                    throw new Exception("Resource stream not found: " + resourcePath);
                }
            } else {
                // Local file path - convert to proper URI
                File videoFile = new File(videoURL);
                if (videoFile.exists() && videoFile.isFile()) {
                    // Convert file to URI to handle spaces and special characters properly
                    String fileURI = videoFile.toURI().toString();
                    media = new Media(fileURI);
                } else {
                    throw new Exception("Video file does not exist: " + videoURL);
                }
            }
            
            if (media == null) {
                throw new Exception("Could not create media from URL: " + videoURL);
            }
              mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setAutoPlay(true);
            
            mediaView = new MediaView(mediaPlayer);
            mediaView.setPreserveRatio(true);
              // Bind MediaView size to fill the video player area - adjusted to prevent control overlap
            mediaView.fitWidthProperty().bind(videoPlayerArea.widthProperty().multiply(0.90));
            mediaView.fitHeightProperty().bind(videoPlayerArea.heightProperty().multiply(0.85));
            
            // Add MediaView to the video player area and center it
            videoPlayerArea.getChildren().add(mediaView);
            StackPane.setAlignment(mediaView, javafx.geometry.Pos.CENTER);
            
            // Debug: Print video loading info
            System.out.println("DEBUG: Loading video from: " + videoURL);
            System.out.println("DEBUG: Media created: " + (media != null));
            System.out.println("DEBUG: MediaView created: " + (mediaView != null));
            
            mediaPlayer.setOnReady(() -> {
                System.out.println("DEBUG: MediaPlayer is ready, duration: " + mediaPlayer.getTotalDuration());
                totalTimeLabel.setText(formatDuration(mediaPlayer.getTotalDuration()));
                
                // Only show overlay when video is actually ready and playing
                videoTitle.setText(title);
                videoPlaceholder.setVisible(false);
                closeVideoBtn.setVisible(true);
                playPauseBtn.setVisible(true);
                currentTimeLabel.setVisible(true);
                totalTimeLabel.setVisible(true);
                speedBtn.setVisible(true);
                volumeSlider.setVisible(true);
                volumeDownBtn.setVisible(true);
                volumeUpBtn.setVisible(true);
                fullscreenBtn.setVisible(true);
                
                // Make video overlay visible
                videoOverlay.setVisible(true);
                videoOverlay.setManaged(true);
                
                // Bind video overlay size to scene size for true fullscreen experience
                if (videoOverlay.getScene() != null) {
                    videoOverlay.prefWidthProperty().bind(videoOverlay.getScene().widthProperty());
                    videoOverlay.prefHeightProperty().bind(videoOverlay.getScene().heightProperty());
                    videoOverlay.setMaxWidth(Double.MAX_VALUE);
                    videoOverlay.setMaxHeight(Double.MAX_VALUE);
                }
                
                // Add key event handler for ESC key to exit fullscreen
                videoOverlay.setOnKeyPressed(keyEvent -> {
                    if (keyEvent.getCode() == javafx.scene.input.KeyCode.ESCAPE) {
                        if (isVideoFullscreen) {
                            toggleFullscreen(); // Exit video fullscreen
                        }
                        keyEvent.consume();
                    }
                });
                
                // Request focus so key events work
                videoOverlay.setFocusTraversable(true);
                videoOverlay.requestFocus();
                
                playPauseBtn.setText("⏸");
                isPlaying = true;
                
                System.out.println("DEBUG: Video overlay shown and ready");
            });
            
            // Add error handling
            mediaPlayer.setOnError(() -> {
                System.err.println("DEBUG: MediaPlayer error: " + mediaPlayer.getError());
                videoPlaceholder.setText("Error loading video: " + mediaPlayer.getError().getMessage());
                videoPlaceholder.setVisible(true);
            });
            
            mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
                @Override
                public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
                    currentTimeLabel.setText(formatDuration(newValue));
                    if (mediaPlayer.getTotalDuration() != null) {
                        timeSlider.setValue(newValue.toMillis() / mediaPlayer.getTotalDuration().toMillis() * 100);
                    }
                }
            });
              timeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (timeSlider.isValueChanging() && mediaPlayer.getTotalDuration() != null) {
                    mediaPlayer.seek(Duration.millis(newValue.doubleValue() / 100 * mediaPlayer.getTotalDuration().toMillis()));
                }
            });
            
            // Add mouse click support for time slider - allows jumping to specific time
            timeSlider.setOnMousePressed(event -> {
                if (mediaPlayer != null && mediaPlayer.getTotalDuration() != null) {
                    // Calculate position based on mouse click
                    double sliderWidth = timeSlider.getWidth();
                    double clickPosition = event.getX();
                    double percentage = clickPosition / sliderWidth;
                    
                    // Ensure percentage is within bounds
                    percentage = Math.max(0, Math.min(1, percentage));
                    
                    // Set slider value and seek to position
                    timeSlider.setValue(percentage * 100);
                    Duration seekTime = Duration.millis(percentage * mediaPlayer.getTotalDuration().toMillis());
                    mediaPlayer.seek(seekTime);
                    
                    System.out.println("DEBUG: Clicked at " + String.format("%.1f%%", percentage * 100) + 
                                     " - Seeking to " + formatDuration(seekTime));
                }
            });
            
            // Also handle mouse click for precise seeking
            timeSlider.setOnMouseClicked(event -> {
                if (mediaPlayer != null && mediaPlayer.getTotalDuration() != null) {
                    // Calculate position based on mouse click
                    double sliderWidth = timeSlider.getWidth();
                    double clickPosition = event.getX();
                    double percentage = clickPosition / sliderWidth;
                    
                    // Ensure percentage is within bounds
                    percentage = Math.max(0, Math.min(1, percentage));
                    
                    // Set slider value and seek to position
                    timeSlider.setValue(percentage * 100);
                    Duration seekTime = Duration.millis(percentage * mediaPlayer.getTotalDuration().toMillis());
                    mediaPlayer.seek(seekTime);
                }
            });
            
            volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (mediaPlayer != null) {
                    mediaPlayer.setVolume(newValue.doubleValue());
                }
            });
              mediaPlayer.setOnEndOfMedia(() -> {
                playPauseBtn.setText("▶");
                isPlaying = false;
            });} catch (Exception e) {
            System.err.println("Error playing video: " + e.getMessage());
            e.printStackTrace(); // Print full stack trace for debugging
            
            String errorMessage = "Failed to load video";
            if (e instanceof java.net.URISyntaxException) {
                errorMessage = "Invalid video file path. Please check the file location.";
            } else if (e.getMessage().contains("does not exist")) {
                errorMessage = "Video file not found. Please check the file path.";            } else if (e.getMessage().contains("Resource not found")) {
                errorMessage = "📹 Video Demo Mode\n\nNo video file available for this lecture.\nThis is normal in the demo version.";
            } else {
                errorMessage = "📹 Video Demo Mode\n\nUnable to load video: " + e.getMessage();
            }
              videoPlaceholder.setText(errorMessage);
            videoPlaceholder.setVisible(true);
            closeVideoBtn.setVisible(true);
            
            // Hide video controls since there's no real video
            playPauseBtn.setVisible(false);
            currentTimeLabel.setVisible(false);
            totalTimeLabel.setVisible(false);
            timeSlider.setVisible(false);
            speedBtn.setVisible(false);
            volumeSlider.setVisible(false);
            volumeDownBtn.setVisible(false);
            volumeUpBtn.setVisible(false);
            fullscreenBtn.setVisible(false);
            
            // Still show the overlay for demo message
            videoOverlay.setVisible(true);
            videoOverlay.setManaged(true);
            
            // Bind overlay size
            if (videoOverlay.getScene() != null) {
                videoOverlay.prefWidthProperty().bind(videoOverlay.getScene().widthProperty());
                videoOverlay.prefHeightProperty().bind(videoOverlay.getScene().heightProperty());
                videoOverlay.setMaxWidth(Double.MAX_VALUE);
                videoOverlay.setMaxHeight(Double.MAX_VALUE);
            }
            
            // Add ESC key handler
            videoOverlay.setOnKeyPressed(keyEvent -> {
                if (keyEvent.getCode() == javafx.scene.input.KeyCode.ESCAPE) {
                    closeVideo();
                    keyEvent.consume();
                }
            });
            
            videoOverlay.setFocusTraversable(true);
            videoOverlay.requestFocus();
        }
    }
      private String formatDuration(Duration duration) {
        if (duration == null) return "0:00";
        long totalSeconds = (long) duration.toSeconds();
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }    @FXML
    private void closeVideo() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            mediaPlayer = null;
        }
        
        if (mediaView != null) {
            videoPlayerArea.getChildren().remove(mediaView);
            mediaView = null;
        }
        
        // Unbind size properties before hiding
        videoOverlay.prefWidthProperty().unbind();
        videoOverlay.prefHeightProperty().unbind();
        
        videoOverlay.setVisible(false);
        videoOverlay.setManaged(false);
        videoPlaceholder.setVisible(true);
        isPlaying = false;
        isVideoFullscreen = false; // Reset fullscreen state
        
        // Ensure video controls and title are visible for next video
        videoControlsBar.setVisible(true);
        videoTitle.setVisible(true);
        
        // Remove fullscreen CSS class
        videoOverlay.getStyleClass().remove("video-fullscreen-mode");
    }
      @FXML
    private void togglePlayPause() {
        if (mediaPlayer == null) return;
        
        if (isPlaying) {
            mediaPlayer.pause();
            playPauseBtn.setText("▶");
        } else {
            mediaPlayer.play();
            playPauseBtn.setText("⏸");        }
        
        isPlaying = !isPlaying;
    }
    
    @FXML
    private void setPlaybackSpeed() {
        if (mediaPlayer == null) return;
        
        currentSpeedIndex = (currentSpeedIndex + 1) % playbackSpeeds.length;
        double newRate = playbackSpeeds[currentSpeedIndex];
        mediaPlayer.setRate(newRate);
        speedBtn.setText(newRate + "x");
    }
    
    @FXML
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
    }    @FXML
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
            isVideoFullscreen = false;        } else {
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
    }
    
    private void showDemoVideoPlaceholder(String title) {
        // Show video overlay with demo content instead of real video
        videoTitle.setText(title);
        videoPlaceholder.setText("🎬 Demo Video\n\nThis is a placeholder for: " + title + "\n\nIn a real application, this would play the actual video content.");
        videoPlaceholder.setVisible(true);
        closeVideoBtn.setVisible(true);
        
        // Hide video controls since there's no real video
        playPauseBtn.setVisible(false);
        currentTimeLabel.setVisible(false);
        totalTimeLabel.setVisible(false);
        timeSlider.setVisible(false);
        speedBtn.setVisible(false);
        volumeSlider.setVisible(false);
        volumeDownBtn.setVisible(false);
        volumeUpBtn.setVisible(false);
        fullscreenBtn.setVisible(false);
        
        // Show video overlay
        videoOverlay.setVisible(true);
        videoOverlay.setManaged(true);
        
        // Bind video overlay size to scene size
        if (videoOverlay.getScene() != null) {
            videoOverlay.prefWidthProperty().bind(videoOverlay.getScene().widthProperty());
            videoOverlay.prefHeightProperty().bind(videoOverlay.getScene().heightProperty());
            videoOverlay.setMaxWidth(Double.MAX_VALUE);
            videoOverlay.setMaxHeight(Double.MAX_VALUE);
        }
        
        // Add key event handler for ESC key
        videoOverlay.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == javafx.scene.input.KeyCode.ESCAPE) {
                closeVideo();
                keyEvent.consume();
            }
        });
        
        videoOverlay.setFocusTraversable(true);
        videoOverlay.requestFocus();
        
        System.out.println("DEBUG: Showing demo video placeholder for: " + title);
    }
}
