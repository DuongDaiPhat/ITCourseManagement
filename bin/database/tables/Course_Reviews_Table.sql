-- Course Reviews Table for IT Course Management System
USE it_course_management;
-- Create Course Reviews Table
CREATE TABLE COURSE_REVIEWS (
    REVIEWID INT PRIMARY KEY AUTO_INCREMENT,
    USERID INT NOT NULL,
    COURSEID INT NOT NULL,
    RATING TINYINT NOT NULL CHECK (RATING >= 1 AND RATING <= 5),
    COMMENT TEXT,
    CREATEDAT DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UPDATEDAT DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Foreign key constraints
    FOREIGN KEY (USERID) REFERENCES USERS(USERID) ON DELETE CASCADE,
    FOREIGN KEY (COURSEID) REFERENCES COURSES(COURSEID) ON DELETE CASCADE,
    
    -- Ensure one review per user per course
    UNIQUE KEY unique_user_course_review (USERID, COURSEID),
    
    -- Index for better performance
    INDEX idx_course_reviews (COURSEID),
    INDEX idx_user_reviews (USERID),
    INDEX idx_rating (RATING)
);

-- Add sample data (optional for testing)
-- Note: Only add if you have existing users and courses in MYLEARNING table
/*
INSERT INTO COURSE_REVIEWS (USERID, COURSEID, RATING, COMMENT) VALUES
(1, 1, 5, 'Excellent course! Very comprehensive and well-structured.'),
(1, 2, 4, 'Good content but could use more practical examples.'),
(2, 1, 5, 'Outstanding instructor and great learning materials.');
*/
