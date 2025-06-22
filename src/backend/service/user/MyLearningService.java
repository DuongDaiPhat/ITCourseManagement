package backend.service.user;

import backend.repository.user.MyLearningRepository;
import model.user.MyLearning;
import model.user.CourseStatus;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class MyLearningService {
    private MyLearningRepository learningRepository;

    public MyLearningService() {
        this.learningRepository = new MyLearningRepository();
    }

    /**
     * Add course to MyLearning
     */
    public boolean addToMyLearning(int userId, int courseId) {
        try {
            // Check if course already exists in learning
            if (isInMyLearning(userId, courseId)) {
                return false; // Already in learning
            }

            MyLearning learning = new MyLearning(userId, courseId, CourseStatus.IN_PROGRESS, LocalDateTime.now());
            MyLearningRepository freshRepository = new MyLearningRepository();
            int result = freshRepository.Insert(learning);
            return result > 0;
        } catch (SQLException e) {
            System.err.println("Error adding to MyLearning: " + e.getMessage());
            return false;
        }
    }

    /**
     * Check if course is in MyLearning
     */
    public boolean isInMyLearning(int userId, int courseId) {
        try {
            MyLearningRepository freshRepository = new MyLearningRepository();
            ArrayList<MyLearning> learningItems = freshRepository.SelectByCondition(
                "userID = " + userId + " AND courseID = " + courseId
            );
            return !learningItems.isEmpty();
        } catch (SQLException e) {
            System.err.println("Error checking MyLearning: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get all learning items for user
     */
    public ArrayList<MyLearning> getUserLearningItems(int userId) {
        try {
            MyLearningRepository freshRepository = new MyLearningRepository();
            return freshRepository.SelectByCondition("userID = " + userId);
        } catch (SQLException e) {
            System.err.println("Error getting user learning items: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Update course status
     */
    public boolean updateCourseStatus(int userId, int courseId, CourseStatus status) {
        try {
            ArrayList<MyLearning> learningItems = getUserLearningItems(userId);
            for (MyLearning item : learningItems) {
                if (item.getCourseID() == courseId) {
                    item.setCourseStatus(status);
                    item.setLastAccessedAt(LocalDateTime.now());
                    MyLearningRepository freshRepository = new MyLearningRepository();
                    freshRepository.Update(item);
                    return true;
                }
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Error updating course status: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get learning count for user
     */
    public int getLearningCount(int userId) {
        try {
            return getUserLearningItems(userId).size();
        } catch (Exception e) {
            System.err.println("Error getting learning count: " + e.getMessage());
            return 0;
        }
    }
}
