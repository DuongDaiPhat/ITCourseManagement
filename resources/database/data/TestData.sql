USE it_course_management;

INSERT INTO ROLES(ROLEID, ROLENAME) VALUES
('1', 'Instructor'),
('2', 'Student'),
('3', 'Admin');
INSERT INTO USERS(USERID, ROLEID, USERFIRSTNAME, USERLASTNAME, USERNAME, PASSWORD, PHONENUMBER, EMAIL, DESCRIPTION, STATUS, CREATEDAT) VALUES
('1','1', 'Dương Hoàng', 'Nam', 'NamDayIT', 'NamhoangduongIT11@', '0379233412', 'namduong@gmail.com', 'Tôi dạy IT', 'online', now()),
('2','2', 'Nguyễn Thị', 'Link', 'LinhHocSinh', 'LinhCute2712@', '0977544321', 'linhnguyen@gmail.com', 'Đam mê IT', 'online', now()),
('3','2', 'Micheal', 'Jackson', 'MusicianLoveIT', 'Heaven1980@', '0879233412', 'michealjackson@gmail.com', 'I love Music, I Love IT', 'online', now()),
('4','1', 'Emily', 'Presis', 'TeachingAI', 'Emily4457@', '0978233412', 'emilypresis@gmail.com', 'Hi I\'m Emily Presis, and I\'m teaching AI check my courses', 'online', now());
INSERT INTO USERS(USERID, ROLEID, USERFIRSTNAME, USERLASTNAME, USERNAME, PASSWORD, PHONENUMBER, EMAIL, DESCRIPTION, STATUS, CREATEDAT) VALUES
('5','1', 'Micheal', 'Jackson', '1', '1', '0879232312', 'micheaaljackson@gmail.com', 'I love Music, I Love IT', 'online', now());
SELECT * FROM USERS;
SELECT * FROM LECTURE;
DELETE FROM COURSES WHERE COURSEID = 1;
