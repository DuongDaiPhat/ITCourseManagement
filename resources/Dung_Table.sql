DROP DATABASE IF EXISTS ITCourseManagement;
CREATE DATABASE ITCourseManagement;
USE ITCourseManagement;

CREATE TABLE Lecture (
    LectureId INT PRIMARY KEY,
    LectureName VARCHAR(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    CourseId INT,
    VideoUrl VARCHAR(1024),
    Duration SMALLINT,
    LectureDescription VARCHAR(512)
);

CREATE TABLE LectureProgress (
    UserId INT,
    LectureId INT,
    Status ENUM('in-progress', 'done', 'unfinished'),
    PRIMARY KEY (UserId, LectureId)
);

CREATE TABLE Categories (
    CategoryId INT PRIMARY KEY,
    CategoryName VARCHAR(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci
);

CREATE TABLE CourseCategories (
    CourseId INT,
    CategoryId INT,
    PRIMARY KEY (CourseId, CategoryId)
);

CREATE TABLE MyLearning (
    UserId INT,
    CourseId INT,
    CourseStatus ENUM('in-progress', 'finished'),
    LastAccessedAt DATETIME,
    PRIMARY KEY (UserId, CourseId)
);

CREATE TABLE Certificate (
    CertificateId INT PRIMARY KEY,
    CertificateName VARCHAR(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    CourseId INT
);

CREATE TABLE UserCertificate (
    CertificateId INT,
    UserId INT,
    IssueDate DATE,
    PRIMARY KEY (CertificateId, UserId)
);

CREATE TABLE MyWishList (
    UserId INT,
    CourseId INT,
    AddedAt DATETIME,
    PRIMARY KEY (UserId, CourseId)
);