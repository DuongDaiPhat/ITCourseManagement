-- Test data for Course Reviews
USE it_course_management;

-- Insert sample course reviews for testing
-- Course 1: "Python Libraries for Artificial Intelligence" has multiple reviews
INSERT INTO COURSE_REVIEWS (USERID, COURSEID, RATING, COMMENT) VALUES
(2, 1, 5, 'Excellent course! Very comprehensive and well-structured. The instructor explains complex AI concepts clearly.'),
(3, 1, 4, 'Good content but could use more practical examples. Overall a solid introduction to Python AI libraries.'),
(1, 1, 5, 'Outstanding instructor and great learning materials. Highly recommend for anyone starting with AI.'),

-- Course 2: "Chat GPT for IT Learning" has a few reviews
(1, 2, 4, 'Very useful prompts and techniques. Helped me improve my AI interactions significantly.'),
(3, 2, 5, 'Amazing course! The prompt engineering techniques are gold. Worth every penny.'),

-- Course 3: "Python for AI" has one review
(2, 3, 3, 'Basic course as described. Good for absolute beginners but nothing new for intermediate learners.');

-- Display the inserted data
SELECT 
    cr.REVIEWID,
    u.FIRSTNAME,
    u.LASTNAME,
    c.COURSENAME,
    cr.RATING,
    cr.COMMENT,
    cr.CREATEDAT
FROM COURSE_REVIEWS cr
JOIN USERS u ON cr.USERID = u.USERID
JOIN COURSES c ON cr.COURSEID = c.COURSEID
ORDER BY cr.COURSEID, cr.CREATEDAT DESC;
