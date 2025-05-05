use it_course_management;
select * from users;
select * from courses;
select * from lesson;

ALTER TABLE USERS ADD COLUMN SALT VARCHAR(50);

ALTER TABLE COURSES ADD COLUMN ISPUBLISHED BOOLEAN DEFAULT FALSE;

CREATE TABLE lesson (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255),
    author VARCHAR(100),
    rating DOUBLE,
    total_ratings INT,
    price DOUBLE,
    image_path VARCHAR(255),
    category ENUM(
        'Artificial_Intelligence',
        'Business_Analysis',
        'Cloud_Computing',
        'Computer_Architecture',
        'Computer_Networks',
        'Cryptography',
        'Data_Science',
        'Data_structures_and_Algorithms',
        'Databases',
        'Deep_Learning',
        'Desktop_Applications',
        'DevOps',
        'Game_Development',
        'Machine_Learning',
        'Mobile_Development',
        'Project_Management',
        'Testing_and_QA',
        'UI_UX',
        'Web_Development',
        'Cybersecurity'
    )
);

INSERT INTO lesson (title, author, rating, total_ratings, price, image_path, category)
VALUES 
('Java developer for beginner', 'UTC2', 4.8, 1002, 10.99, 'images/main_page/images/Python.jpeg', 'Web_Development'),
('Python for Data Science', 'UTC2', 4.7, 980, 11.99, 'images/main_page/images/Python.jpeg', 'Artificial_Intelligence');

