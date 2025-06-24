package backend.service.course;

import backend.repository.course.CourseReviewRepository;
import model.course.CourseReview;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class CourseReviewService {
    
    private CourseReviewRepository reviewRepository;
    
    public CourseReviewService() {
        this.reviewRepository = new CourseReviewRepository();
    }
    
    /**
     * Submit a new course review
     */
    public boolean submitReview(int userId, int courseId, int rating, String comment) {
        try {
            // Check if user has already reviewed this course
            if (reviewRepository.hasUserReviewedCourse(userId, courseId)) {
                System.err.println("User has already reviewed this course");
                return false;
            }
            
            // Validate rating
            if (rating < 1 || rating > 5) {
                System.err.println("Invalid rating. Must be between 1 and 5");
                return false;
            }
            
            // Create and insert new review
            CourseReview review = new CourseReview(userId, courseId, rating, comment);
            int result = reviewRepository.Insert(review);
            
            if (result > 0) {
                System.out.println("Review submitted successfully for course " + courseId + " by user " + userId);
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error submitting review: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Update an existing review
     */
    public boolean updateReview(int userId, int courseId, int newRating, String newComment) {
        try {
            // Get existing review
            CourseReview existingReview = reviewRepository.getReviewByUserAndCourse(userId, courseId);
            if (existingReview == null) {
                System.err.println("No existing review found for user " + userId + " and course " + courseId);
                return false;
            }
            
            // Validate rating
            if (newRating < 1 || newRating > 5) {
                System.err.println("Invalid rating. Must be between 1 and 5");
                return false;
            }
            
            // Update review
            existingReview.setRating(newRating);
            existingReview.setComment(newComment);
            existingReview.setUpdatedAt(LocalDateTime.now());
            
            int result = reviewRepository.Update(existingReview);
            
            if (result > 0) {
                System.out.println("Review updated successfully for course " + courseId + " by user " + userId);
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error updating review: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Delete a review
     */
    public boolean deleteReview(int userId, int courseId) {
        try {
            CourseReview existingReview = reviewRepository.getReviewByUserAndCourse(userId, courseId);
            if (existingReview == null) {
                System.err.println("No review found to delete");
                return false;
            }
            
            int result = reviewRepository.Delete(existingReview);
            
            if (result > 0) {
                System.out.println("Review deleted successfully for course " + courseId + " by user " + userId);
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error deleting review: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Get user's review for a specific course
     */
    public CourseReview getUserReviewForCourse(int userId, int courseId) {
        try {
            return reviewRepository.getReviewByUserAndCourse(userId, courseId);
        } catch (SQLException e) {
            System.err.println("Error getting user review: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Get all reviews for a course
     */
    public ArrayList<CourseReview> getCourseReviews(int courseId) {
        try {
            return reviewRepository.getReviewsByCourseId(courseId);
        } catch (SQLException e) {
            System.err.println("Error getting course reviews: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Get all reviews for a course with reviewer names
     */
    public ArrayList<CourseReviewRepository.CourseReviewWithName> getCourseReviewsWithNames(int courseId) {
        try {
            return reviewRepository.getReviewsWithNamesByCourseId(courseId);
        } catch (SQLException e) {
            System.err.println("Error getting course reviews with names: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Get average rating for a course
     */
    public double getCourseAverageRating(int courseId) {
        try {
            return reviewRepository.getAverageRatingByCourseId(courseId);
        } catch (SQLException e) {
            System.err.println("Error getting average rating: " + e.getMessage());
            e.printStackTrace();
            return 0.0;
        }
    }
    
    /**
     * Get review count for a course
     */
    public int getCourseReviewCount(int courseId) {
        try {
            return reviewRepository.getReviewCountByCourseId(courseId);
        } catch (SQLException e) {
            System.err.println("Error getting review count: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }
    
    /**
     * Check if user has already reviewed a course
     */
    public boolean hasUserReviewedCourse(int userId, int courseId) {
        try {
            return reviewRepository.hasUserReviewedCourse(userId, courseId);
        } catch (SQLException e) {
            System.err.println("Error checking user review status: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get all reviews by a user
     */
    public ArrayList<CourseReview> getUserReviews(int userId) {
        try {
            return reviewRepository.getReviewsByUserId(userId);
        } catch (SQLException e) {
            System.err.println("Error getting user reviews: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Get formatted rating string (e.g., "4.5 (123 reviews)")
     */
    public String getFormattedCourseRating(int courseId) {
        try {
            double avgRating = getCourseAverageRating(courseId);
            int reviewCount = getCourseReviewCount(courseId);
            
            if (reviewCount == 0) {
                return "No reviews yet";
            }
            
            return String.format("%.1f (%d review%s)", avgRating, reviewCount, reviewCount == 1 ? "" : "s");
            
        } catch (Exception e) {
            System.err.println("Error formatting course rating: " + e.getMessage());
            return "No reviews yet";
        }
    }
}
