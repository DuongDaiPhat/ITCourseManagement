package backend.controller.studentCart;

import backend.controller.scene.SceneManager;
import backend.service.course.CourseService;
import backend.service.user.UserService;
import backend.service.user.CartService;
import backend.service.user.MyLearningService;
import backend.service.course.CourseReviewService;
import backend.util.ImageCache;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import model.course.Courses;
import model.user.MyCart;
import model.user.MyLearning;
import model.user.Session;
import model.user.Users;
import model.user.CourseStatus;
import backend.controller.payment.PaymentDialogController;
import backend.service.payment.PaymentService.PaymentResult;
import backend.repository.notification.NotificationRepository;
import model.notification.UserNotification;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class StudentCartController implements Initializable {    // Header Navigation Elements
    @FXML private Label homeLabel;
    @FXML private Label categoryLabel;
    @FXML private Label myLearningLabel;
    @FXML private TextField searchField;
    @FXML private Button searchButton;
    @FXML private Label cartLabel;
    @FXML private Button wishlistButton;
    @FXML private Button notificationButton;
    @FXML private Button profileButton;
    
    // Icon ImageViews
    @FXML private ImageView searchIcon;
    @FXML private ImageView cartIcon;
    @FXML private ImageView wishlistIcon;
    @FXML private ImageView notificationIcon;
    @FXML private ImageView profileIcon;
      // Cart Section Elements
    @FXML private Label selectAllLabel;
    @FXML private Label unselectAllLabel;
    @FXML private ScrollPane cartItemsScrollPane;
    @FXML private VBox cartItemsContainer;
      // Payment Summary Elements
    @FXML private Label selectedItemsLabel;
    @FXML private Label totalPriceLabel;
    @FXML private Button payButton;      // Services
    private CourseService courseService;
    private UserService userService;
    private CartService cartService;
    private MyLearningService learningService;
    private CourseReviewService courseReviewService;
    private ContextMenu profileMenu;
    
    // Data
    private List<CartItemData> cartItems;
    private Set<Integer> selectedCourseIds;
    private Users currentUser;
    
    // Notification system fields
    private Circle notificationBadge;
    private Popup notificationPopup;
    private VBox notificationPopupContent;
    private NotificationRepository notificationRepository = new NotificationRepository();
    
    // Cart Item Data Class
    private static class CartItemData {
        private Courses course;
        private CheckBox checkbox;
        private VBox itemContainer;
        
        public CartItemData(Courses course, CheckBox checkbox, VBox itemContainer) {
            this.course = course;
            this.checkbox = checkbox;
            this.itemContainer = itemContainer;
        }
        
        public Courses getCourse() { return course; }
        public CheckBox getCheckbox() { return checkbox; }
        public VBox getItemContainer() { return itemContainer; }
    }    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeServices();
        setupIcons();
        setupNotificationButton();
        
        cartItems = new ArrayList<>();
        selectedCourseIds = new HashSet<>();
        currentUser = Session.getCurrentUser();
        
        // Setup event handlers after FXML injection is complete
        Platform.runLater(() -> {
            setupEventHandlers();
            setupProfileMenu();
            loadCartItems();
            updatePaymentSummary();
            loadNotifications();
        });
    }      private void initializeServices() {
        courseService = new CourseService();
        userService = new UserService();
        cartService = new CartService();
        learningService = new MyLearningService();
        courseReviewService = new CourseReviewService();
    }
      private void setupIcons() {
        // Load icons with error handling using file: protocol - same as StudentMainPage
        loadIconSafely(searchIcon, "file:resources/images/main_page/icon/WhiteMagnifier.png");
        loadIconSafely(cartIcon, "file:resources/images/main_page/icon/MyCart.png");
        loadIconSafely(wishlistIcon, "file:resources/images/main_page/icon/MyWishList.png");
        loadIconSafely(notificationIcon, "file:resources/images/main_page/icon/Notification.png");
        loadIconSafely(profileIcon, "file:resources/images/main_page/icon/MyProfile.png");
    }
    
    private void loadIconSafely(ImageView imageView, String iconPath) {
        try {
            System.out.println("Attempting to load icon from: " + iconPath);
            Image icon = ImageCache.loadImage(iconPath);
            if (icon != null) {
                imageView.setImage(icon);
                System.out.println("Successfully loaded icon: " + iconPath);
            } else {
                System.out.println("Icon not found or error loading: " + iconPath);
            }
        } catch (Exception e) {
            System.out.println("Exception loading icon " + iconPath + ": " + e.getMessage());
        }
    }private void setupEventHandlers() {
        // Navigation handlers
        homeLabel.setOnMouseClicked(event -> navigateToMainPage());
        categoryLabel.setOnMouseClicked(event -> navigateToExplorePage());        myLearningLabel.setOnMouseClicked(event -> navigateToMyLearning());
        
        // Cart is now a label (current page indicator) - no action needed
        
        wishlistButton.setOnAction(event -> navigateToWishlist());
        
        // Select all/unselect all handlers
        selectAllLabel.setOnMouseClicked(event -> selectAllItems());
        unselectAllLabel.setOnMouseClicked(event -> unselectAllItems());
    }
    
    private void setupProfileMenu() {
        profileMenu = new ContextMenu();
        
        MenuItem profileItem = new MenuItem("View Profile");
        profileItem.setOnAction(event -> navigateToProfile());
        
        MenuItem settingsItem = new MenuItem("Settings");
        settingsItem.setOnAction(event -> navigateToSettings());
        
        MenuItem logoutItem = new MenuItem("Logout");
        logoutItem.setOnAction(event -> handleLogout());
        
        profileMenu.getItems().addAll(profileItem, settingsItem, new SeparatorMenuItem(), logoutItem);
    }
      @FXML
    private void showProfileMenu() {
        // Calculate position to show menu below the profile button
        double x = profileButton.localToScreen(profileButton.getBoundsInLocal()).getMinX();
        double y = profileButton.localToScreen(profileButton.getBoundsInLocal()).getMaxY();
        
        // Show menu at calculated position
        profileMenu.show(profileButton.getScene().getWindow(), x, y);
    }
    
    @FXML
    private void handleSearch() {
        String searchTerm = searchField.getText().trim();
        if (!searchTerm.isEmpty()) {
            navigateToSearchResults(searchTerm);
        }
    }
    
    private void loadCartItems() {
        if (currentUser == null) {
            showEmptyCartMessage();
            return;
        }
        
        try {            // Get cart items from database
            ArrayList<MyCart> cartEntries = cartService.getUserCartItems(currentUser.getUserID());
            cartItems.clear();
            cartItemsContainer.getChildren().clear();
            
            if (cartEntries.isEmpty()) {
                showEmptyCartMessage();
                return;
            }
              // Load course details for each cart item
            for (MyCart cartEntry : cartEntries) {
                try {
                    Courses course = courseService.GetCourseByID(cartEntry.getCourseId());
                    if (course != null) {
                        VBox itemContainer = createCartItemUI(course);
                        CheckBox checkbox = findCheckboxInContainer(itemContainer);
                        
                        CartItemData itemData = new CartItemData(course, checkbox, itemContainer);
                        cartItems.add(itemData);
                        cartItemsContainer.getChildren().add(itemContainer);
                    }
                } catch (Exception e) {
                    System.err.println("Error loading course " + cartEntry.getCourseId() + ": " + e.getMessage());
                }
            }
            
            updatePaymentSummary();
            
        } catch (Exception e) {
            System.err.println("Error loading cart items: " + e.getMessage());
            showAlert("Error", "Failed to load cart items. Please try again.");
        }
    }
    
    private void showEmptyCartMessage() {
        cartItemsContainer.getChildren().clear();
        
        Label emptyMessage = new Label("Your cart is empty");
        emptyMessage.getStyleClass().add("empty-cart-message");
        
        VBox emptyContainer = new VBox(emptyMessage);
        emptyContainer.setAlignment(Pos.CENTER);
        emptyContainer.setPadding(new Insets(50));
        
        cartItemsContainer.getChildren().add(emptyContainer);
          payButton.setDisable(true);
        selectedItemsLabel.setText("0");
        totalPriceLabel.setText("$0.00");
    }
    
    private VBox createCartItemUI(Courses course) {
        VBox itemContainer = new VBox();
        itemContainer.getStyleClass().add("cart-item");
        itemContainer.setSpacing(15);
        
        HBox mainContent = new HBox();
        mainContent.setSpacing(20);
        mainContent.setAlignment(Pos.CENTER_LEFT);
        
        // Checkbox
        CheckBox checkbox = new CheckBox();
        checkbox.setOnAction(event -> onItemSelectionChanged());
          // Course Image
        ImageView courseImage = new ImageView();
        courseImage.getStyleClass().add("course-image");
        courseImage.setFitWidth(120);
        courseImage.setFitHeight(80);
        courseImage.setPreserveRatio(true);
        
        try {
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
                    courseImage.setImage(image);
                    System.out.println("Successfully loaded course image: " + thumbnailPath);
                } else {
                    // Try alternative paths if original failed
                    String altPath = "file:resources/images/" + course.getThumbnailURL().replace("user_data/images/", "");
                    System.out.println("Retrying with alternative path: " + altPath);
                    Image altImage = ImageCache.loadImage(altPath);
                    if (altImage != null) {
                        courseImage.setImage(altImage);
                        System.out.println("Successfully loaded alternative image: " + altPath);
                    } else {
                        courseImage.setImage(ImageCache.loadImage("/images/default_course.png"));
                    }
                }
            } else {
                courseImage.setImage(ImageCache.loadImage("/images/default_course.png"));
            }
        } catch (Exception e) {
            courseImage.setImage(ImageCache.loadImage("/images/default_course.png"));
        }
        
        // Course Details
        VBox courseDetails = new VBox();
        courseDetails.setSpacing(8);
        HBox.setHgrow(courseDetails, Priority.ALWAYS);
        
        Label titleLabel = new Label(course.getCourseName());
        titleLabel.getStyleClass().add("course-title");
        
        Label instructorLabel = new Label("By " + getInstructorName(course.getUserID()));        instructorLabel.getStyleClass().add("course-instructor");
        
        // Rating - Get real rating from database
        HBox ratingBox = new HBox();
        ratingBox.setSpacing(5);
        ratingBox.setAlignment(Pos.CENTER_LEFT);
        
        double avgRating = courseReviewService.getCourseAverageRating(course.getCourseID());
        int reviewCount = courseReviewService.getCourseReviewCount(course.getCourseID());
        
        String ratingText = reviewCount > 0 ? 
            String.format("%.1f ★", avgRating) :
            "No rating yet ☆";
        Label ratingLabel = new Label(ratingText);
        ratingLabel.getStyleClass().add("course-rating");
        
        String reviewText = reviewCount > 0 ? 
            String.format("(%d review%s)", reviewCount, reviewCount == 1 ? "" : "s") :
            "(0 reviews)";
        Label reviewsLabel = new Label(reviewText);
        reviewsLabel.getStyleClass().add("course-instructor");
        
        ratingBox.getChildren().addAll(ratingLabel, reviewsLabel);
        
        courseDetails.getChildren().addAll(titleLabel, instructorLabel, ratingBox);
        
        // Price and Actions
        VBox priceActions = new VBox();
        priceActions.setSpacing(10);
        priceActions.setAlignment(Pos.CENTER_RIGHT);
        
        Label priceLabel = new Label(String.format("$%.2f", course.getPrice()));
        priceLabel.getStyleClass().add("course-price");
        
        Button removeButton = new Button("Remove");
        removeButton.getStyleClass().add("remove-button");
        removeButton.setOnAction(event -> removeFromCart(course));
        
        priceActions.getChildren().addAll(priceLabel, removeButton);
          mainContent.getChildren().addAll(checkbox, courseImage, courseDetails, priceActions);
        itemContainer.getChildren().add(mainContent);
        
        // Add click handler to navigate to course detail (excluding checkbox and remove button)
        itemContainer.setOnMouseClicked(event -> {
            // Only navigate if click is not on checkbox or remove button
            if (event.getTarget() != checkbox && event.getTarget() != removeButton) {
                navigateToCourseDetail(course);
            }
        });
        
        return itemContainer;
    }
    
    private CheckBox findCheckboxInContainer(VBox container) {
        if (container.getChildren().isEmpty()) return null;
        
        HBox mainContent = (HBox) container.getChildren().get(0);
        return (CheckBox) mainContent.getChildren().get(0);
    }
      private String getInstructorName(int userId) {
        try {
            Users instructor = userService.GetUserByID(userId);
            return instructor != null ? instructor.getUserFirstName() + " " + instructor.getUserLastName() : "Unknown Instructor";
        } catch (Exception e) {
            return "Unknown Instructor";
        }
    }
      private void onItemSelectionChanged() {
        selectedCourseIds.clear();
        
        for (CartItemData item : cartItems) {
            if (item.getCheckbox().isSelected()) {
                selectedCourseIds.add(item.getCourse().getCourseID());
                // Remove first to avoid duplicates, then add
                item.getItemContainer().getStyleClass().removeAll("cart-item-selected");
                item.getItemContainer().getStyleClass().add("cart-item-selected");
            } else {
                // Ensure style class is completely removed
                item.getItemContainer().getStyleClass().removeAll("cart-item-selected");
            }
        }
        
        updatePaymentSummary();
    }
    
    private void selectAllItems() {
        for (CartItemData item : cartItems) {
            item.getCheckbox().setSelected(true);
        }
        onItemSelectionChanged();
    }
    
    private void unselectAllItems() {
        for (CartItemData item : cartItems) {
            item.getCheckbox().setSelected(false);
        }
        onItemSelectionChanged();
    }
      private void updatePaymentSummary() {
        if (selectedItemsLabel == null || totalPriceLabel == null || payButton == null) {
            return; // FXML elements not yet initialized
        }
        
        int selectedCount = selectedCourseIds.size();
        double totalPrice = 0.0;
        
        for (CartItemData item : cartItems) {
            if (item.getCheckbox().isSelected()) {
                totalPrice += item.getCourse().getPrice();
            }        }
        
        selectedItemsLabel.setText(String.valueOf(selectedCount));
        totalPriceLabel.setText(String.format("$%.2f", totalPrice));
        payButton.setDisable(selectedCount == 0);
    }
    
    private void removeFromCart(Courses course) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Remove from Cart");
        confirmation.setHeaderText("Remove Course");
        confirmation.setContentText("Are you sure you want to remove '" + course.getCourseName() + "' from your cart?");
        
        Optional<ButtonType> result = confirmation.showAndWait();        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                cartService.removeFromCart(currentUser.getUserID(), course.getCourseID());
                loadCartItems(); // Refresh the cart
                showAlert("Success", "Course removed from cart successfully.");
            } catch (Exception e) {
                System.err.println("Error removing from cart: " + e.getMessage());
                showAlert("Error", "Failed to remove course from cart. Please try again.");
            }
        }
    }
      @FXML
    private void handlePayment() {
        if (selectedCourseIds.isEmpty()) {
            showAlert("No Selection", "Please select at least one course to proceed with payment.");
            return;
        }
        
        try {
            // Show payment dialog for multiple courses
            showPaymentDialog();
        } catch (Exception e) {
            System.err.println("Error opening payment dialog: " + e.getMessage());
            showAlert("Error", "Failed to open payment dialog. Please try again.");
        }
    }
    
    private void showPaymentDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/frontend/view/payment/PaymentDialog.fxml"));
            VBox paymentRoot = loader.load();
            
            PaymentDialogController paymentController = loader.getController();
            
            // Calculate total for selected courses
            double totalAmount = 0.0;
            List<Courses> selectedCourses = new ArrayList<>();
            
            for (CartItemData item : cartItems) {
                if (item.getCheckbox().isSelected()) {
                    selectedCourses.add(item.getCourse());
                    totalAmount += item.getCourse().getPrice();
                }
            }
            
            // Create a virtual course for multiple course payment
            Courses virtualCourse = new Courses();
            virtualCourse.setCourseName(selectedCourses.size() + " Selected Courses");
            virtualCourse.setPrice((float) totalAmount);
            
            paymentController.setPaymentData(currentUser, virtualCourse);
            
            Stage paymentStage = new Stage();
            paymentStage.setTitle("Payment");
            paymentStage.setScene(new Scene(paymentRoot));
            paymentStage.initModality(Modality.APPLICATION_MODAL);
            paymentStage.setResizable(false);
            
            paymentStage.showAndWait();
            
            // Check if payment was successful
            if (paymentController.isPaymentCompleted()) {
                handlePaymentSuccess(selectedCourses, paymentController.getPaymentResult());
            }
            
        } catch (IOException e) {
            System.err.println("Error loading payment dialog: " + e.getMessage());
            showAlert("Error", "Failed to load payment dialog.");
        }
    }
    
    private void handlePaymentSuccess(List<Courses> purchasedCourses, PaymentResult paymentResult) {
        try {            // Add courses to MyLearning
            for (Courses course : purchasedCourses) {
                MyLearning learning = new MyLearning(
                    currentUser.getUserID(),
                    course.getCourseID(),
                    CourseStatus.IN_PROGRESS,
                    LocalDateTime.now()
                );
                
                learningService.addToMyLearning(learning.getUserID(), learning.getCourseID());
                
                // Remove from cart
                cartService.removeFromCart(currentUser.getUserID(), course.getCourseID());
            }
            
            // Refresh cart
            loadCartItems();
            
            // Show success message
            showAlert("Payment Successful", 
                "Payment completed successfully!\n" +
                "Transaction ID: " + paymentResult.getTransactionId() + "\n" +
                "Courses have been added to your learning library.");
            
        } catch (Exception e) {
            System.err.println("Error processing payment success: " + e.getMessage());
            showAlert("Error", "Payment was successful but there was an error updating your account. Please contact support.");
        }
    }
      // Notification system setup
    private void setupNotificationButton() {
        // Create notification badge
        notificationBadge = new Circle(5);
        notificationBadge.setFill(Color.TRANSPARENT); // Hidden by default
        notificationBadge.setStroke(Color.TRANSPARENT);
        notificationBadge.setTranslateX(8);
        notificationBadge.setTranslateY(-8);
        notificationBadge.getStyleClass().add("notification-badge");

        // Combine icon and badge
        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(notificationIcon, notificationBadge);

        // Assign to notification button
        notificationButton.setGraphic(stackPane);
        notificationButton.getStyleClass().add("notification-button");

        // Create notification popup
        notificationPopup = new Popup();
        notificationPopup.setAutoHide(true); // Auto-hide when clicking outside

        // Popup content
        notificationPopupContent = new VBox();
        notificationPopupContent.setStyle(
                "-fx-background-color: white; -fx-border-color: #cccccc; -fx-border-width: 1px; -fx-padding: 10px;");
        notificationPopupContent.setPrefWidth(300);
        notificationPopupContent.setMaxHeight(400);

        // Add scroll pane
        ScrollPane scrollPane = new ScrollPane(notificationPopupContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: white; -fx-background-color: white;");

        // Popup title
        Label titleLabel = new Label("Notifications");
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 0 0 10 0;");

        // Main container
        VBox popupContainer = new VBox(titleLabel, scrollPane);
        popupContainer.setStyle("-fx-background-color: white; -fx-padding: 10px;");
        popupContainer.setPrefWidth(300);

        notificationPopup.getContent().add(popupContainer);

        // Set action handler for notification button
        notificationButton.setOnAction(event -> toggleNotificationPopup());
    }

    private void toggleNotificationPopup() {
        if (notificationPopup.isShowing()) {
            notificationPopup.hide();
        } else {
            notificationPopup.show(notificationButton.getScene().getWindow());
        }
    }

    private void loadNotifications() {
        try {
            List<UserNotification> notifications = notificationRepository.getUserNotifications(currentUser.getUserID());
            notificationPopupContent.getChildren().clear();
            if (notifications == null || notifications.isEmpty()) {
                Label emptyLabel = new Label("No new notifications.");
                emptyLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #999999;");
                notificationPopupContent.getChildren().add(emptyLabel);
            } else {
                for (UserNotification notification : notifications) {
                    VBox notificationItem = createNotificationItem(notification);
                    notificationPopupContent.getChildren().add(notificationItem);
                }
            }
            int unreadCount = (int) notifications.stream().filter(n -> !n.isRead()).count();
            updateNotificationBadge(unreadCount);
        } catch (SQLException e) {
            Platform.runLater(() -> showAlert("Database Error", "Failed to load notifications: " + e.getMessage()));
        } catch (Exception e) {
            // handle error
        }
    }

    private VBox createNotificationItem(UserNotification notification) {
        VBox container = new VBox();
        container.setStyle(
                "-fx-background-color: #f9f9f9; -fx-border-color: #eeeeee; -fx-border-width: 0 0 1 0; -fx-padding: 10px;");
        container.setSpacing(5);
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(5);
        Label iconLabel = new Label(notification.getIcon());
        iconLabel.setStyle("-fx-font-size: 16px;");
        Label titleLabel = new Label(notification.getNotificationName());
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
        Label categoryLabel = new Label("(" + notification.getCategory() + ")");
        categoryLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666666;");
        header.getChildren().addAll(iconLabel, titleLabel, categoryLabel);
        Label contentLabel = new Label(notification.getContent());
        contentLabel.setStyle("-fx-font-size: 12px; -fx-wrap-text: true;");
        contentLabel.setMaxWidth(280);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");
        String timeText = notification.getNotifiedAt() != null ? notification.getNotifiedAt().format(formatter)
                : "Unknown time";
        Label timeLabel = new Label(timeText);
        timeLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #999999; -fx-padding: 5 0 0 0;");
        container.getChildren().addAll(header, contentLabel, timeLabel);
        container.setOnMouseClicked(e -> {
            // TODO: Navigate to notification detail
        });
        return container;
    }

    private void updateNotificationBadge(int count) {
        if (count > 0) {
            notificationBadge.setFill(Color.RED);
            notificationBadge.setStroke(Color.WHITE);
            notificationBadge.setRadius(10);
            notificationBadge.setVisible(true);
        } else {
            notificationBadge.setVisible(false);
        }
    }
      // Navigation Methods
    private void navigateToMainPage() {
        SceneManager.switchScene("Student Main Page", "/frontend/view/studentMainPage/studentMainPage.fxml");
    }
    
    private void navigateToExplorePage() {
        SceneManager.switchScene("Student Explore Page", "/frontend/view/studentExplorePage/studentExplorePage.fxml");
    }
      private void navigateToMyLearning() {
        try {
            System.out.println("Navigating to My Learning Page...");
            SceneManager.switchSceneWithRefresh(
                "My Learning",
                "/frontend/view/myLearning/MyLearning.fxml"
            );
        } catch (Exception e) {
            System.err.println("Error navigating to My Learning Page: " + e.getMessage());
            e.printStackTrace();
            showAlert("Navigation Error", "Could not navigate to the My Learning page. Please try again.");
        }
    }
      private void navigateToWishlist() {
        try {
            SceneManager.switchSceneWithRefresh(
                "My Wishlist", 
                "/frontend/view/myWishList/MyWishList.fxml"
            );
        } catch (Exception e) {
            System.err.println("Error navigating to wishlist: " + e.getMessage());
            showAlert("Error", "Failed to open wishlist page. Please try again.");
        }
    }
      private void navigateToProfile() {
        try {
            System.out.println("Navigating to User Profile...");
            SceneManager.switchScene(
                "User Profile",
                "/frontend/view/userProfile/UserProfile.fxml"
            );
        } catch (Exception e) {
            System.err.println("Error navigating to User Profile: " + e.getMessage());
            e.printStackTrace();
            showAlert("Navigation Error", "Could not navigate to the profile page. Please try again.");
        }
    }
    
    private void navigateToSettings() {
        // TODO: Implement navigation to Settings page
        showAlert("Coming Soon", "Settings page is under development.");
    }
    
    private void navigateToSearchResults(String searchTerm) {
        // TODO: Implement navigation to Search Results page
        showAlert("Search", "Search functionality is under development.\nSearched for: " + searchTerm);
    }
    
    private void handleLogout() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Logout");
        confirmation.setHeaderText("Confirm Logout");
        confirmation.setContentText("Are you sure you want to logout?");          Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Session.setCurrentUser(null);
            SceneManager.switchToLoginScene("Login Page", "/frontend/view/login/Login.fxml");
        }
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Refresh method called by SceneManager to ensure fresh cart data
     */
    public void refreshPageData() {
        System.out.println("Refreshing StudentCart page data...");
        Platform.runLater(() -> {
            // Clear previous data
            cartItems.clear();
            selectedCourseIds.clear();
            cartItemsContainer.getChildren().clear();
            
            // Reload cart items
            loadCartItems();
            updatePaymentSummary();
        });
    }
    
    private void navigateToCourseDetail(Courses course) {
        try {
            // Set course data in session for StudentCourseDetailPage
            Session.setCurrentCourse(course, false); // Cart courses are not purchased yet
            
            SceneManager.switchSceneWithRefresh(
                "Course Details", 
                "/frontend/view/studentCourseDetailPage/StudentCourseDetailPage.fxml"
            );
        } catch (Exception e) {
            System.err.println("Error navigating to course detail: " + e.getMessage());
            e.printStackTrace();
            showAlert("Navigation Error", "Could not open course details. Please try again.");
        }
    }
}
