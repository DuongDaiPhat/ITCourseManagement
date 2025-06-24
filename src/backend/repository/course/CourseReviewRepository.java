package backend.repository.course;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;

import backend.repository.RepositoryInterface;
import backend.repository.DatabaseConnection;
import model.course.CourseReview;

public class CourseReviewRepository implements RepositoryInterface<CourseReview> {
    
    public CourseReviewRepository() {
        // Constructor không cần lưu connection
    }

    private Connection getConnection() throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            throw new SQLException("Unable to establish database connection");
        }
        return conn;
    }

    @Override
    public int Insert(CourseReview review) throws SQLException {
        Connection connection = getConnection();
        String sql = "INSERT INTO COURSE_REVIEWS (USERID, COURSEID, RATING, COMMENT, CREATEDAT, UPDATEDAT) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        
        statement.setInt(1, review.getUserId());
        statement.setInt(2, review.getCourseId());
        statement.setInt(3, review.getRating());
        statement.setString(4, review.getComment());
        statement.setTimestamp(5, Timestamp.valueOf(review.getCreatedAt()));
        statement.setTimestamp(6, Timestamp.valueOf(review.getUpdatedAt()));

        int result = statement.executeUpdate();
        
        // Get the generated review ID
        if (result > 0) {
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                review.setReviewId(generatedKeys.getInt(1));
            }
        }
        
        return result;
    }    @Override
    public int Update(CourseReview review) throws SQLException {
        Connection connection = getConnection();
        String sql = "UPDATE COURSE_REVIEWS SET RATING = ?, COMMENT = ?, UPDATEDAT = ? WHERE REVIEWID = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        
        statement.setInt(1, review.getRating());
        statement.setString(2, review.getComment());
        statement.setTimestamp(3, Timestamp.valueOf(review.getUpdatedAt()));
        statement.setInt(4, review.getReviewId());

        return statement.executeUpdate();
    }

    @Override
    public int Delete(CourseReview review) throws SQLException {
        Connection connection = getConnection();
        String sql = "DELETE FROM COURSE_REVIEWS WHERE REVIEWID = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, review.getReviewId());

        return statement.executeUpdate();
    }    @Override
    public ArrayList<CourseReview> SelectAll() throws SQLException {
        Connection connection = getConnection();
        ArrayList<CourseReview> reviews = new ArrayList<>();
        String sql = "SELECT * FROM COURSE_REVIEWS ORDER BY CREATEDAT DESC";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);

        while (resultSet.next()) {
            CourseReview review = mapResultSetToReview(resultSet);
            reviews.add(review);
        }
        return reviews;
    }    @Override
    public CourseReview SelectByID(int reviewId) throws SQLException {
        Connection connection = getConnection();
        String sql = "SELECT * FROM COURSE_REVIEWS WHERE REVIEWID = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, reviewId);
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            return mapResultSetToReview(resultSet);
        }
        return null;
    }

    @Override
    public ArrayList<CourseReview> SelectByCondition(String condition) throws SQLException {
        Connection connection = getConnection();
        ArrayList<CourseReview> reviews = new ArrayList<>();
        String sql = "SELECT * FROM COURSE_REVIEWS WHERE " + condition + " ORDER BY CREATEDAT DESC";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);

        while (resultSet.next()) {
            CourseReview review = mapResultSetToReview(resultSet);
            reviews.add(review);
        }
        return reviews;
    }
    
    /**
     * Get all reviews for a specific course
     */
    public ArrayList<CourseReview> getReviewsByCourseId(int courseId) throws SQLException {
        return SelectByCondition("COURSEID = " + courseId);
    }
      /**
     * Get review by user and course (since each user can only review once per course)
     */
    public CourseReview getReviewByUserAndCourse(int userId, int courseId) throws SQLException {
        Connection connection = getConnection();
        String sql = "SELECT * FROM COURSE_REVIEWS WHERE USERID = ? AND COURSEID = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, userId);
        statement.setInt(2, courseId);
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            return mapResultSetToReview(resultSet);
        }
        return null;
    }
      /**
     * Get average rating for a course
     */
    public double getAverageRatingByCourseId(int courseId) throws SQLException {
        Connection connection = getConnection();
        String sql = "SELECT AVG(RATING) as avg_rating FROM COURSE_REVIEWS WHERE COURSEID = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, courseId);
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            return resultSet.getDouble("avg_rating");
        }
        return 0.0;
    }
    
    /**
     * Get total review count for a course
     */
    public int getReviewCountByCourseId(int courseId) throws SQLException {
        Connection connection = getConnection();
        String sql = "SELECT COUNT(*) as review_count FROM COURSE_REVIEWS WHERE COURSEID = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, courseId);
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            return resultSet.getInt("review_count");
        }
        return 0;
    }
    
    /**
     * Check if user has already reviewed a course
     */
    public boolean hasUserReviewedCourse(int userId, int courseId) throws SQLException {
        CourseReview existingReview = getReviewByUserAndCourse(userId, courseId);
        return existingReview != null;
    }
    
    /**
     * Get all reviews by a specific user
     */
    public ArrayList<CourseReview> getReviewsByUserId(int userId) throws SQLException {
        return SelectByCondition("USERID = " + userId);
    }
      /**
     * Get all reviews for a course with reviewer names
     */
    public ArrayList<CourseReviewWithName> getReviewsWithNamesByCourseId(int courseId) throws SQLException {
        Connection connection = getConnection();
        ArrayList<CourseReviewWithName> reviews = new ArrayList<>();
        String sql = "SELECT cr.*, u.USERFIRSTNAME, u.USERLASTNAME FROM COURSE_REVIEWS cr " +
                    "JOIN USERS u ON cr.USERID = u.USERID " +
                    "WHERE cr.COURSEID = ? ORDER BY cr.CREATEDAT DESC";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, courseId);
        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {
            CourseReview review = mapResultSetToReview(resultSet);
            String reviewerName = resultSet.getString("USERFIRSTNAME") + " " + resultSet.getString("USERLASTNAME");
            CourseReviewWithName reviewWithName = new CourseReviewWithName(review, reviewerName);
            reviews.add(reviewWithName);
        }
        return reviews;
    }
    
    /**
     * Inner class to hold review with reviewer name
     */
    public static class CourseReviewWithName {
        private CourseReview review;
        private String reviewerName;
        
        public CourseReviewWithName(CourseReview review, String reviewerName) {
            this.review = review;
            this.reviewerName = reviewerName;
        }
        
        public CourseReview getReview() {
            return review;
        }
        
        public String getReviewerName() {
            return reviewerName;
        }
    }
    
    /**
     * Utility method to map ResultSet to CourseReview object
     */
    private CourseReview mapResultSetToReview(ResultSet resultSet) throws SQLException {
        return new CourseReview(
            resultSet.getInt("REVIEWID"),
            resultSet.getInt("USERID"),
            resultSet.getInt("COURSEID"),
            resultSet.getInt("RATING"),
            resultSet.getString("COMMENT"),
            resultSet.getTimestamp("CREATEDAT").toLocalDateTime(),
            resultSet.getTimestamp("UPDATEDAT").toLocalDateTime()
        );
    }
}
