package backend.controller.studentCart;

import backend.controller.scene.SceneManager;
import backend.service.course.CourseService;
import backend.service.user.UserService;
import backend.service.user.CartService;
import backend.service.user.MyLearningService;
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
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.course.Courses;
import model.user.MyCart;
import model.user.MyLearning;
import model.user.Session;
import model.user.Users;
import model.user.CourseStatus;
import model.user.UserStatus;
import backend.controller.payment.PaymentDialogController;
import backend.service.payment.PaymentService.PaymentResult;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;

public class StudentCartController implements Initializable {
	@FXML
	private Label homeLabel;
	@FXML
	private Label categoryLabel;
	@FXML
	private Label myLearningLabel;
	@FXML
	private TextField searchField;
	@FXML
	private Button searchButton;
	@FXML
	private Label cartLabel;
	@FXML
	private Button wishlistButton;
	@FXML
	private Button notificationButton;
	@FXML
	private Button profileButton;
	@FXML
	private ImageView searchIcon;
	@FXML
	private ImageView cartIcon;
	@FXML
	private ImageView wishlistIcon;
	@FXML
	private ImageView notificationIcon;
	@FXML
	private ImageView profileIcon;
	@FXML
	private Label selectAllLabel;
	@FXML
	private Label unselectAllLabel;
	@FXML
	private ScrollPane cartItemsScrollPane;
	@FXML
	private VBox cartItemsContainer;
	@FXML
	private Label selectedItemsLabel;
	@FXML
	private Label totalPriceLabel;
	@FXML
	private Button payButton;
	private CourseService courseService;
	private UserService userService;
	private CartService cartService;
	private MyLearningService learningService;
	private ContextMenu profileMenu;
	private List<CartItemData> cartItems;
	private Set<Integer> selectedCourseIds;
	private Users currentUser;

	private static class CartItemData {
		private Courses course;
		private CheckBox checkbox;
		private VBox itemContainer;

		public CartItemData(Courses course, CheckBox checkbox, VBox itemContainer) {
			this.course = course;
			this.checkbox = checkbox;
			this.itemContainer = itemContainer;
		}

		public Courses getCourse() {
			return course;
		}

		public CheckBox getCheckbox() {
			return checkbox;
		}

		public VBox getItemContainer() {
			return itemContainer;
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initializeServices();
		setupIcons();

		cartItems = new ArrayList<>();
		selectedCourseIds = new HashSet<>();
		currentUser = Session.getCurrentUser();

		Platform.runLater(() -> {
			setupEventHandlers();
			setupProfileMenu();
			loadCartItems();
			updatePaymentSummary();
		});
	}

	private void initializeServices() {
		courseService = new CourseService();
		userService = new UserService();
		cartService = new CartService();
		learningService = new MyLearningService();
	}

	private void setupIcons() {
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
	}

	private void setupEventHandlers() {
		homeLabel.setOnMouseClicked(event -> navigateToMainPage());
		categoryLabel.setOnMouseClicked(event -> navigateToExplorePage());
		myLearningLabel.setOnMouseClicked(event -> navigateToMyLearning());
		wishlistButton.setOnAction(event -> navigateToWishlist());
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
		double x = profileButton.localToScreen(profileButton.getBoundsInLocal()).getMinX();
		double y = profileButton.localToScreen(profileButton.getBoundsInLocal()).getMaxY();
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

		try {
			ArrayList<MyCart> cartEntries = cartService.getUserCartItems(currentUser.getUserID());
			cartItems.clear();
			cartItemsContainer.getChildren().clear();

			if (cartEntries.isEmpty()) {
				showEmptyCartMessage();
				return;
			}

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

		CheckBox checkbox = new CheckBox();
		checkbox.setOnAction(event -> onItemSelectionChanged());

		ImageView courseImage = new ImageView();
		courseImage.getStyleClass().add("course-image");
		courseImage.setFitWidth(120);
		courseImage.setFitHeight(80);
		courseImage.setPreserveRatio(true);

		try {
			if (course.getThumbnailURL() != null && !course.getThumbnailURL().isEmpty()) {
				String thumbnailPath = course.getThumbnailURL();
				if (!thumbnailPath.startsWith("file:") && !thumbnailPath.startsWith("http")) {
					thumbnailPath = "file:" + thumbnailPath;
				}
				System.out.println("Loading course image from URL: " + thumbnailPath);
				Image image = ImageCache.loadImage(thumbnailPath);
				if (image != null) {
					courseImage.setImage(image);
					System.out.println("Successfully loaded course image: " + thumbnailPath);
				} else {
					String altPath = "file:resources/images/"
							+ course.getThumbnailURL().replace("user_data/images/", "");
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

		VBox courseDetails = new VBox();
		courseDetails.setSpacing(8);
		HBox.setHgrow(courseDetails, Priority.ALWAYS);

		Label titleLabel = new Label(course.getCourseName());
		titleLabel.getStyleClass().add("course-title");

		Label instructorLabel = new Label("By " + getInstructorName(course.getUserID()));
		instructorLabel.getStyleClass().add("course-instructor");

		HBox ratingBox = new HBox();
		ratingBox.setSpacing(5);
		ratingBox.setAlignment(Pos.CENTER_LEFT);

		Label ratingLabel = new Label("4.5 ★★★★☆");
		ratingLabel.getStyleClass().add("course-rating");

		Label reviewsLabel = new Label("(123 reviews)");
		reviewsLabel.getStyleClass().add("course-instructor");

		ratingBox.getChildren().addAll(ratingLabel, reviewsLabel);
		courseDetails.getChildren().addAll(titleLabel, instructorLabel, ratingBox);

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

		itemContainer.setOnMouseClicked(event -> {
			if (event.getTarget() != checkbox && event.getTarget() != removeButton) {
				navigateToCourseDetail(course);
			}
		});

		return itemContainer;
	}

	private CheckBox findCheckboxInContainer(VBox container) {
		if (container.getChildren().isEmpty())
			return null;
		HBox mainContent = (HBox) container.getChildren().get(0);
		return (CheckBox) mainContent.getChildren().get(0);
	}

	private String getInstructorName(int userId) {
		try {
			Users instructor = userService.GetUserByID(userId);
			return instructor != null ? instructor.getUserFirstName() + " " + instructor.getUserLastName()
					: "Unknown Instructor";
		} catch (Exception e) {
			return "Unknown Instructor";
		}
	}

	private void onItemSelectionChanged() {
		selectedCourseIds.clear();
		for (CartItemData item : cartItems) {
			if (item.getCheckbox().isSelected()) {
				selectedCourseIds.add(item.getCourse().getCourseID());
				item.getItemContainer().getStyleClass().removeAll("cart-item-selected");
				item.getItemContainer().getStyleClass().add("cart-item-selected");
			} else {
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
			return;
		}

		int selectedCount = selectedCourseIds.size();
		double totalPrice = 0.0;

		for (CartItemData item : cartItems) {
			if (item.getCheckbox().isSelected()) {
				totalPrice += item.getCourse().getPrice();
			}
		}

		selectedItemsLabel.setText(String.valueOf(selectedCount));
		totalPriceLabel.setText(String.format("$%.2f", totalPrice));
		payButton.setDisable(selectedCount == 0);
	}

	private void removeFromCart(Courses course) {
		Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
		confirmation.setTitle("Remove from Cart");
		confirmation.setHeaderText("Remove Course");
		confirmation.setContentText("Are you sure you want to remove '" + course.getCourseName() + "' from your cart?");

		Optional<ButtonType> result = confirmation.showAndWait();
		if (result.isPresent() && result.get() == ButtonType.OK) {
			try {
				cartService.removeFromCart(currentUser.getUserID(), course.getCourseID());
				loadCartItems();
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

		if (currentUser.getStatus() == UserStatus.banned) {
			showAlert("Account Banned", "Your account has been banned. You cannot make purchases.");
			return;
		}

		try {
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

			double totalAmount = 0.0;
			List<Courses> selectedCourses = new ArrayList<>();

			for (CartItemData item : cartItems) {
				if (item.getCheckbox().isSelected()) {
					selectedCourses.add(item.getCourse());
					totalAmount += item.getCourse().getPrice();
				}
			}

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

			if (paymentController.isPaymentCompleted()) {
				handlePaymentSuccess(selectedCourses, paymentController.getPaymentResult());
			}
		} catch (IOException e) {
			System.err.println("Error loading payment dialog: " + e.getMessage());
			showAlert("Error", "Failed to load payment dialog.");
		}
	}

	private void handlePaymentSuccess(List<Courses> purchasedCourses, PaymentResult paymentResult) {
		try {
			for (Courses course : purchasedCourses) {
				MyLearning learning = new MyLearning(currentUser.getUserID(), course.getCourseID(),
						CourseStatus.IN_PROGRESS, LocalDateTime.now());

				learningService.addToMyLearning(learning.getUserID(), learning.getCourseID());
				cartService.removeFromCart(currentUser.getUserID(), course.getCourseID());
			}

			loadCartItems();

			showAlert("Payment Successful", "Payment completed successfully!\n" + "Transaction ID: "
					+ paymentResult.getTransactionId() + "\n" + "Courses have been added to your learning library.");
		} catch (Exception e) {
			System.err.println("Error processing payment success: " + e.getMessage());
			showAlert("Error",
					"Payment was successful but there was an error updating your account. Please contact support.");
		}
	}

	private void navigateToMainPage() {
		SceneManager.switchScene("Student Main Page", "/frontend/view/studentMainPage/studentMainPage.fxml");
	}

	private void navigateToExplorePage() {
		SceneManager.switchScene("Student Explore Page", "/frontend/view/studentExplorePage/studentExplorePage.fxml");
	}

	private void navigateToMyLearning() {
		try {
			System.out.println("Navigating to My Learning Page...");
			SceneManager.switchSceneWithRefresh("My Learning", "/frontend/view/myLearning/MyLearning.fxml");
		} catch (Exception e) {
			System.err.println("Error navigating to My Learning Page: " + e.getMessage());
			e.printStackTrace();
			showAlert("Navigation Error", "Could not navigate to the My Learning page. Please try again.");
		}
	}

	private void navigateToWishlist() {
		try {
			SceneManager.switchSceneWithRefresh("My Wishlist", "/frontend/view/myWishList/MyWishList.fxml");
		} catch (Exception e) {
			System.err.println("Error navigating to wishlist: " + e.getMessage());
			showAlert("Error", "Failed to open wishlist page. Please try again.");
		}
	}

	private void navigateToProfile() {
		try {
			System.out.println("Navigating to User Profile...");
			SceneManager.switchScene("User Profile", "/frontend/view/userProfile/UserProfile.fxml");
		} catch (Exception e) {
			System.err.println("Error navigating to User Profile: " + e.getMessage());
			e.printStackTrace();
			showAlert("Navigation Error", "Could not navigate to the profile page. Please try again.");
		}
	}

	private void navigateToSettings() {
		showAlert("Coming Soon", "Settings page is under development.");
	}

	private void navigateToSearchResults(String searchTerm) {
		showAlert("Search", "Search functionality is under development.\nSearched for: " + searchTerm);
	}

	private void navigateToCourseDetail(Courses course) {
		try {
			Session.setCurrentCourse(course, false);
			SceneManager.switchSceneWithRefresh("Course Details",
					"/frontend/view/studentCourseDetailPage/StudentCourseDetailPage.fxml");
		} catch (Exception e) {
			System.err.println("Error navigating to course detail: " + e.getMessage());
			e.printStackTrace();
			showAlert("Navigation Error", "Could not open course details. Please try again.");
		}
	}

	private void handleLogout() {
		Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
		confirmation.setTitle("Logout");
		confirmation.setHeaderText("Confirm Logout");
		confirmation.setContentText("Are you sure you want to logout?");

		Optional<ButtonType> result = confirmation.showAndWait();
		if (result.isPresent() && result.get() == ButtonType.OK) {
			Session.setCurrentUser(null);
			SceneManager.switchScene("Login Page", "/frontend/view/login/Login.fxml");
		}
	}

	private void showAlert(String title, String message) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

	public void refreshPageData() {
		System.out.println("Refreshing StudentCart page data...");
		Platform.runLater(() -> {
			cartItems.clear();
			selectedCourseIds.clear();
			cartItemsContainer.getChildren().clear();
			loadCartItems();
			updatePaymentSummary();
		});
	}
}