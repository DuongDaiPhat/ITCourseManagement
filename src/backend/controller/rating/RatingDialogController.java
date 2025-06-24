package backend.controller.rating;

import backend.service.course.CourseReviewService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import model.course.Courses;
import model.course.CourseReview;
import model.user.Users;

import java.net.URL;
import java.util.ResourceBundle;

public class RatingDialogController implements Initializable {
    
    @FXML private Label courseTitleLabel;
    @FXML private HBox starContainer;
    @FXML private TextArea commentArea;
    @FXML private Button cancelButton;
    @FXML private Button submitButton;
    
    private CourseReviewService courseReviewService;
    private Courses course;
    private Users currentUser;
    private Button[] stars;
    private int currentRating = 0;
    private boolean isUpdate = false;
    private boolean ratingSubmitted = false;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        courseReviewService = new CourseReviewService();
        setupStarRating();
    }
    
    /**
     * Set the course and user data for rating
     */
    public void setRatingData(Users user, Courses course) {
        this.currentUser = user;
        this.course = course;
        
        // Set course title
        courseTitleLabel.setText(course.getCourseName());
        
        // Check if user has existing review
        CourseReview existingReview = courseReviewService.getUserReviewForCourse(user.getUserID(), course.getCourseID());
        if (existingReview != null) {
            isUpdate = true;
            currentRating = existingReview.getRating();
            updateStarDisplay(currentRating);
            commentArea.setText(existingReview.getComment() != null ? existingReview.getComment() : "");
            submitButton.setText("Update Rating");
        }
    }
    
    /**
     * Setup star rating buttons
     */
    private void setupStarRating() {
        stars = new Button[5];
        
        for (int i = 0; i < 5; i++) {
            Button star = new Button("☆");
            star.getStyleClass().add("star-button");
            final int starIndex = i;
            
            // Click handler
            star.setOnAction(event -> {
                currentRating = starIndex + 1;
                updateStarDisplay(currentRating);
            });
            
            // Hover effects
            star.setOnMouseEntered(event -> updateStarPreview(starIndex + 1));
            star.setOnMouseExited(event -> updateStarDisplay(currentRating));
            
            stars[i] = star;
            starContainer.getChildren().add(star);
        }
    }
    
    /**
     * Update star display based on rating
     */
    private void updateStarDisplay(int rating) {
        for (int i = 0; i < stars.length; i++) {
            if (i < rating) {
                stars[i].setText("★");
                stars[i].getStyleClass().removeAll("star-button-empty", "star-button-preview");
                stars[i].getStyleClass().add("star-button-filled");
            } else {
                stars[i].setText("☆");
                stars[i].getStyleClass().removeAll("star-button-filled", "star-button-preview");
                stars[i].getStyleClass().add("star-button-empty");
            }
        }
    }
    
    /**
     * Update star preview on hover
     */
    private void updateStarPreview(int rating) {
        for (int i = 0; i < stars.length; i++) {
            if (i < rating) {
                stars[i].setText("★");
                stars[i].getStyleClass().removeAll("star-button-empty", "star-button-filled");
                stars[i].getStyleClass().add("star-button-preview");
            } else {
                stars[i].setText("☆");
                stars[i].getStyleClass().removeAll("star-button-filled", "star-button-preview");
                stars[i].getStyleClass().add("star-button-empty");
            }
        }
    }
    
    /**
     * Handle cancel button
     */
    @FXML
    private void handleCancel() {
        closeDialog();
    }
    
    /**
     * Handle submit button
     */
    @FXML
    private void handleSubmit() {
        if (currentRating == 0) {
            showAlert("Rating Required", "Please select a star rating before submitting.");
            return;
        }
        
        try {
            String comment = commentArea.getText().trim();
            boolean success;
            
            if (isUpdate) {
                success = courseReviewService.updateReview(currentUser.getUserID(), course.getCourseID(), currentRating, comment);
            } else {
                success = courseReviewService.submitReview(currentUser.getUserID(), course.getCourseID(), currentRating, comment);
            }
            
            if (success) {
                ratingSubmitted = true;
                showAlert("Success", isUpdate ? "Rating updated successfully!" : "Rating submitted successfully!");
                closeDialog();
            } else {
                showAlert("Error", "Failed to submit rating. Please try again.");
            }
            
        } catch (Exception e) {
            System.err.println("Error submitting rating: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "An error occurred while submitting your rating. Please try again.");
        }
    }
    
    /**
     * Check if rating was submitted
     */
    public boolean isRatingSubmitted() {
        return ratingSubmitted;
    }
    
    /**
     * Close the dialog
     */
    private void closeDialog() {
        Stage stage = (Stage) submitButton.getScene().getWindow();
        stage.close();
    }
    
    /**
     * Show alert dialog
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
