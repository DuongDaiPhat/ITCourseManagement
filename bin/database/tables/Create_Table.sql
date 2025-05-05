DROP DATABASE it_course_management;
CREATE DATABASE it_course_management;
USE it_course_management;
CREATE TABLE ROLES(
	ROLEID INT PRIMARY KEY AUTO_INCREMENT,
    ROLENAME NVARCHAR(30)
);
CREATE TABLE USERS(
    USERID INT PRIMARY KEY AUTO_INCREMENT,
    ROLEID INT,
    USERFIRSTNAME NVARCHAR(50) NOT NULL,
    USERLASTNAME NVARCHAR(50) NOT NULL,
    USERNAME NVARCHAR(100) NOT NULL UNIQUE,
    PASSWORD CHAR(20) NOT NULL,
    SALT CHAR(20),
    PHONENUMBER VARCHAR(15) UNIQUE,
    EMAIL CHAR(50) NOT NULL UNIQUE,
    DESCRIPTION NVARCHAR(512),
    STATUS ENUM('online', 'offline', 'banned'),
    CREATEDAT DATE NOT NULL
);


CREATE TABLE COURSES(
	COURSEID INT PRIMARY KEY AUTO_INCREMENT,
    COURSENAME NVARCHAR(512) NOT NULL,
    LANGUAGE ENUM(
        'English', 'Vietnamese', 'Spanish', 'French', 'German', 'Chinese', 'Japanese', 'Korean',
        'Italian', 'Portuguese', 'Russian', 'Arabic', 'Hindi', 'Bengali', 'Urdu', 'Turkish',
        'Dutch', 'Greek', 'Polish', 'Swedish', 'Norwegian', 'Danish', 'Finnish', 'Czech',
        'Hungarian', 'Hebrew', 'Thai', 'Malay', 'Indonesian', 'Filipino', 'Ukrainian', 'Slovak',
        'Romanian', 'Bulgarian', 'Serbian', 'Croatian', 'Slovenian', 'Lithuanian', 'Latvian',
        'Estonian', 'Persian', 'Swahili', 'Zulu', 'Amharic', 'Punjabi', 'Sinhala', 'Nepali',
        'Burmese', 'Mongolian', 'Khmer'
    ) NOT NULL,
    TECHNOLOGY ENUM(
        'Java', 'C', 'Cpp', 'CSharp', 'Python', 'JavaScript', 'TypeScript',
		'GoLang', 'Rust', 'Kotlin', 'Swift', 'Ruby', 'PHP', 'Perl',
		'Scala', 'Objective_C', 'Haskell', 'Lua', 'RLang', 'Dart',
		'Shell', 'SQL', 'MATLAB', 'Assembly', 'Groovy', 'FSharp',
		'Elixir', 'Erlang', 'Fortran', 'COBOL', 'VB_NET',
		'Pascal', 'Prolog', 'Scheme', 'Lisp', 'Julia', 'Solidity',
		'VHDL', 'Ada', 'Tcl', 'Crystal', 'OCaml', 'ABAP',
		'SAS', 'Hack', 'Nim', 'Delphi', 'PL_SQL', 'Bash', 'HTML', 'CSS'
    ) NOT NULL,
    LEVEL ENUM('BEGINNER', 'INTERMEDIATE', 'ADVANCED', 'ALLLEVEL') NOT NULL,
    CATEGORY ENUM(
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
	) NOT NULL,
    USERID INT NOT NULL,
    FOREIGN KEY(USERID) REFERENCES USERS(USERID),
    THUMBNAILURL VARCHAR(255),
    PRICE FLOAT,
	COURSEDESCRIPTION VARCHAR(512),
    CREATEDAT DATETIME NOT NULL,
    UPDATEDAT DATETIME,
	is_rejected BOOLEAN DEFAULT FALSE,
    ISAPPROVED BOOLEAN DEFAULT FALSE,
    ISPUBLISHED BOOLEAN DEFAULT FALSE,
    CHECK(PRICE >= 0)
);
CREATE TABLE PERMISSIONS(
	PERMISSIONID INT PRIMARY KEY AUTO_INCREMENT,
    PERMISSIONNAME VARCHAR(256) NOT NULL
);
CREATE TABLE ROLEPERMISSION(
	ROLEID INT NOT NULL,
    PERMISSIONID INT NOT NULL,
    FOREIGN KEY (ROLEID) REFERENCES ROLES(ROLEID),
    FOREIGN KEY (PERMISSIONID) REFERENCES PERMISSIONS(PERMISSIONID),
    PRIMARY KEY (ROLEID, PERMISSIONID)
);

CREATE TABLE LECTURE (
    LECTUREID INT PRIMARY KEY AUTO_INCREMENT,
    LECTURENAME NVARCHAR(512) NOT NULL,
    COURSEID INT NOT NULL,
    VIDEOURL VARCHAR(1024) NOT NULL,
    DURATION SMALLINT NOT NULL CHECK(DURATION > 0),
    LECTUREDESCRIPTION NVARCHAR(512),
    FOREIGN KEY (COURSEID) REFERENCES COURSES(COURSEID) ON DELETE CASCADE
);

CREATE TABLE LECTUREPROGRESS (
    USERID INT NOT NULL,
    LECTUREID INT NOT NULL,
    STATUS ENUM('in-progress', 'done', 'unfinished') NOT NULL DEFAULT 'unfinished',
    PRIMARY KEY (USERID, LECTUREID),
    FOREIGN KEY (USERID) REFERENCES USERS(USERID) ON DELETE CASCADE,
    FOREIGN KEY (LECTUREID) REFERENCES LECTURE(LECTUREID) ON DELETE CASCADE
);

CREATE TABLE MYLEARNING (
    USERID INT NOT NULL,
    COURSEID INT NOT NULL,
    COURSESTATUS ENUM('in-progress', 'finished') NOT NULL DEFAULT 'in-progress',
    LASTACCESSEDAT DATETIME,
    PRIMARY KEY (USERID, COURSEID),
    FOREIGN KEY (USERID) REFERENCES USERS(USERID) ON DELETE CASCADE,
    FOREIGN KEY (COURSEID) REFERENCES COURSES(COURSEID) ON DELETE CASCADE
);

CREATE TABLE CERTIFICATE (
    CERTIFICATEID INT PRIMARY KEY AUTO_INCREMENT,
    CERTIFICATENAME NVARCHAR(512) NOT NULL,
    COURSEID INT NOT NULL,
    FOREIGN KEY (COURSEID) REFERENCES COURSES(COURSEID) ON DELETE CASCADE
);

CREATE TABLE USERCERTIFICATE (
    CERTIFICATEID INT NOT NULL,
    USERID INT NOT NULL,
    ISSUEDATE DATE NOT NULL,
    PRIMARY KEY (CERTIFICATEID, USERID),
    FOREIGN KEY (CERTIFICATEID) REFERENCES CERTIFICATE(CERTIFICATEID) ON DELETE CASCADE,
    FOREIGN KEY (USERID) REFERENCES USERS(USERID) ON DELETE CASCADE
);

CREATE TABLE MYWISHLIST (
    USERID INT NOT NULL,
    COURSEID INT NOT NULL,
    ADDEDAT DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (USERID, COURSEID),
    FOREIGN KEY (USERID) REFERENCES USERS(USERID) ON DELETE CASCADE,
    FOREIGN KEY (COURSEID) REFERENCES COURSES(COURSEID) ON DELETE CASCADE
);

-- Bảng MyCart
CREATE TABLE MyCart (
    UserID INT NOT NULL,
    CourseID INT NOT NULL,
    IsBuy BIT DEFAULT 0,
    AddedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (UserID, CourseID) 
);


-- Bảng Payments
CREATE TABLE Payments (
    PaymentID INT PRIMARY KEY AUTO_INCREMENT,
    PaymentName NVARCHAR(255) NOT NULL
);


-- Bảng UserPayment
CREATE TABLE UserPayment (
    PaymentID INT,
    UserID INT,
    Balance FLOAT NOT NULL,
    PRIMARY KEY (PaymentID, UserID),
    FOREIGN KEY (PaymentID) REFERENCES Payments(PaymentID)
);


-- Bảng Invoices
CREATE TABLE Invoices (
    InvoiceID INT PRIMARY KEY AUTO_INCREMENT,
    UserID INT NOT NULL,
    TotalPrice FLOAT NOT NULL,
    BoughtAt DATETIME DEFAULT CURRENT_TIMESTAMP
);


-- Bảng InvoiceDetail
CREATE TABLE InvoiceDetail (
    InvoiceID INT,
    CourseID INT,
    PRIMARY KEY (InvoiceID, CourseID),
    FOREIGN KEY (InvoiceID) REFERENCES Invoices(InvoiceID)
);

-- Bảng Notification
CREATE TABLE Notification (
    NotificationID INT PRIMARY KEY AUTO_INCREMENT,
    NotificationName VARCHAR(255) CHARACTER SET utf8mb4 NOT NULL,
    Content VARCHAR(512) CHARACTER SET utf8mb4 NOT NULL,
    NotifiedAt DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Bảng NotificationDetail
CREATE TABLE NotificationDetail (
    NotificationID INT,
    UserID INT,
    Status ENUM('read', 'unread') NOT NULL DEFAULT 'unread',
    PRIMARY KEY (NotificationID, UserID),
    FOREIGN KEY (NotificationID) REFERENCES Notification(NotificationID)
);

INSERT INTO ROLES (ROLENAME) VALUES ('instructor'), ('student'), ('admin');

-- Thêm tài khoản admin (sử dụng ROLEID = 3)
INSERT INTO USERS (
    ROLEID, USERFIRSTNAME, USERLASTNAME, USERNAME, PASSWORD, 
    PHONENUMBER, EMAIL, DESCRIPTION, STATUS, CREATEDAT
) VALUES (
    3, 'System', 'Admin', 'admin', 'Admin123', 
    '0123456789', 'admin@example.com', 'Quản trị viên hệ thống', 'online', CURDATE()
);
