package Test;

import backend.service.course.CourseReviewService;
import model.course.CourseReview;
import java.util.ArrayList;

public class TestCourseReviews {
    
    public static void main(String[] args) {
        System.out.println("Testing Course Review Service...");
        
        CourseReviewService reviewService = new CourseReviewService();
        
        // Test getting reviews for course 1
        int testCourseId = 1;
        System.out.println("\n=== Testing Course ID: " + testCourseId + " ===");
        
        try {
            // Test average rating
            double avgRating = reviewService.getCourseAverageRating(testCourseId);
            System.out.println("Average Rating: " + avgRating);
            
            // Test review count
            int reviewCount = reviewService.getCourseReviewCount(testCourseId);
            System.out.println("Review Count: " + reviewCount);
            
            // Test getting all reviews
            ArrayList<CourseReview> reviews = reviewService.getCourseReviews(testCourseId);
            System.out.println("Reviews found: " + reviews.size());
            
            for (CourseReview review : reviews) {
                System.out.println("  - Rating: " + review.getRating() + 
                                 ", Comment: " + review.getComment());
            }
            
            // Test formatted rating
            String formattedRating = reviewService.getFormattedCourseRating(testCourseId);
            System.out.println("Formatted Rating: " + formattedRating);
            
        } catch (Exception e) {
            System.err.println("Error testing course reviews: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("\nTest completed.");
    }
}
