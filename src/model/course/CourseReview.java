package model.course;

import java.time.LocalDateTime;

public class CourseReview {
    private int reviewId;
    private int userId;
    private int courseId;
    private int rating; // 1-5 stars
    private String comment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Default constructor
    public CourseReview() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Constructor with basic fields
    public CourseReview(int userId, int courseId, int rating, String comment) {
        this.userId = userId;
        this.courseId = courseId;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Constructor with all fields
    public CourseReview(int reviewId, int userId, int courseId, int rating, String comment, 
                       LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.reviewId = reviewId;
        this.userId = userId;
        this.courseId = courseId;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getters and Setters
    public int getReviewId() {
        return reviewId;
    }
    
    public void setReviewId(int reviewId) {
        this.reviewId = reviewId;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public int getCourseId() {
        return courseId;
    }
    
    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }
    
    public int getRating() {
        return rating;
    }
    
    public void setRating(int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        this.rating = rating;
    }
    
    public String getComment() {
        return comment;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    // Utility methods
    public boolean isValidRating() {
        return rating >= 1 && rating <= 5;
    }
    
    public String getFormattedRating() {
        return "★".repeat(rating) + "☆".repeat(5 - rating);
    }
    
    @Override
    public String toString() {
        return String.format("CourseReview{reviewId=%d, userId=%d, courseId=%d, rating=%d, comment='%s', createdAt=%s}", 
                           reviewId, userId, courseId, rating, comment, createdAt);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        CourseReview that = (CourseReview) obj;
        return userId == that.userId && courseId == that.courseId;
    }
    
    @Override
    public int hashCode() {
        return userId * 31 + courseId;
    }
}
