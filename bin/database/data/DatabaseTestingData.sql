-- MySQL dump 10.13  Distrib 8.0.33, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: it_course_management
-- ------------------------------------------------------
-- Server version	9.1.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `certificate`
--

DROP TABLE IF EXISTS `certificate`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `certificate` (
  `CERTIFICATEID` int NOT NULL AUTO_INCREMENT,
  `CERTIFICATENAME` varchar(512) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `COURSEID` int NOT NULL,
  PRIMARY KEY (`CERTIFICATEID`),
  KEY `COURSEID` (`COURSEID`),
  CONSTRAINT `certificate_ibfk_1` FOREIGN KEY (`COURSEID`) REFERENCES `courses` (`COURSEID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `certificate`
--

LOCK TABLES `certificate` WRITE;
/*!40000 ALTER TABLE `certificate` DISABLE KEYS */;
/*!40000 ALTER TABLE `certificate` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `courses`
--

DROP TABLE IF EXISTS `courses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `courses` (
  `COURSEID` int NOT NULL AUTO_INCREMENT,
  `COURSENAME` varchar(512) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `LANGUAGE` enum('English','Vietnamese','Spanish','French','German','Chinese','Japanese','Korean','Italian','Portuguese','Russian','Arabic','Hindi','Bengali','Urdu','Turkish','Dutch','Greek','Polish','Swedish','Norwegian','Danish','Finnish','Czech','Hungarian','Hebrew','Thai','Malay','Indonesian','Filipino','Ukrainian','Slovak','Romanian','Bulgarian','Serbian','Croatian','Slovenian','Lithuanian','Latvian','Estonian','Persian','Swahili','Zulu','Amharic','Punjabi','Sinhala','Nepali','Burmese','Mongolian','Khmer') NOT NULL,
  `PROGRAMMINGLANGUAGE` enum('Java','C','Cpp','CSharp','Python','JavaScript','TypeScript','GoLang','Rust','Kotlin','Swift','Ruby','PHP','Perl','Scala','Objective_C','Haskell','Lua','RLang','Dart','Shell','SQL','MATLAB','Assembly','Groovy','FSharp','Elixir','Erlang','Fortran','COBOL','VB_NET','Pascal','Prolog','Scheme','Lisp','Julia','Solidity','VHDL','Ada','Tcl','Crystal','OCaml','ABAP','SAS','Hack','Nim','Delphi','PL_SQL','Bash','VietnamesePseudoCode') NOT NULL,
  `LEVEL` enum('BEGINNER','INTERMEDIATE','ADVANCED','ALLLEVEL') NOT NULL,
  `CATEGORY` enum('Artificial_Intelligence','Business_Analysis','Cloud_Computing','Computer_Architecture','Computer_Networks','Cryptography','Data_Science','Data_structures_and_Algorithms','Databases','Deep_Learning','Desktop_Applications','DevOps','Game_Development','Machine_Learning','Mobile_Development','Project_Management','Testing_and_QA','UI_UX','Web_Development','Cybersecurity') NOT NULL,
  `USERID` int NOT NULL,
  `THUMBNAILURL` varchar(255) DEFAULT NULL,
  `PRICE` float DEFAULT NULL,
  `COURSEDESCRIPTION` varchar(512) DEFAULT NULL,
  `CREATEDAT` datetime NOT NULL,
  `UPDATEDAT` datetime DEFAULT NULL,
  PRIMARY KEY (`COURSEID`),
  KEY `USERID` (`USERID`),
  CONSTRAINT `courses_ibfk_1` FOREIGN KEY (`USERID`) REFERENCES `users` (`USERID`),
  CONSTRAINT `courses_chk_1` CHECK ((`PRICE` >= 0))
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `courses`
--

LOCK TABLES `courses` WRITE;
/*!40000 ALTER TABLE `courses` DISABLE KEYS */;
INSERT INTO `courses` VALUES (1,'Python Libraries for Artificial Intelligence','English','Python','BEGINNER','Artificial_Intelligence',4,'user_data/images/df9d0d42-4b83-4341-ac86-79c9329bcabb_Python.png',5.99,'This course will provide for you the best, most modern and most using widely Python librabries for AI','2025-04-12 00:54:30','2025-04-12 00:54:30'),(2,'Chat GPT for IT Learning','English','Python','BEGINNER','Artificial_Intelligence',4,'user_data/images/1646a3da-14ff-4c2b-8bdf-3931b7e10a0a_Acer_Wallpaper_01_5000x2814.jpg',10.99,'This Course provides for you the best prompt to make AI performance more effectively','2025-04-12 01:00:46','2025-04-12 01:00:46'),(3,'Python for AI','English','Python','BEGINNER','Artificial_Intelligence',4,'user_data/images/027d2c83-6994-461d-903a-5a3277b55ce9_Acer_Wallpaper_02_5000x2813.jpg',8.99,'Basic AI with Python','2025-04-12 01:14:03','2025-04-12 01:14:03');
/*!40000 ALTER TABLE `courses` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `invoicedetail`
--

DROP TABLE IF EXISTS `invoicedetail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `invoicedetail` (
  `InvoiceID` int NOT NULL,
  `CourseID` int NOT NULL,
  PRIMARY KEY (`InvoiceID`,`CourseID`),
  CONSTRAINT `invoicedetail_ibfk_1` FOREIGN KEY (`InvoiceID`) REFERENCES `invoices` (`InvoiceID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `invoicedetail`
--

LOCK TABLES `invoicedetail` WRITE;
/*!40000 ALTER TABLE `invoicedetail` DISABLE KEYS */;
/*!40000 ALTER TABLE `invoicedetail` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `invoices`
--

DROP TABLE IF EXISTS `invoices`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `invoices` (
  `InvoiceID` int NOT NULL AUTO_INCREMENT,
  `UserID` int NOT NULL,
  `TotalPrice` float NOT NULL,
  `BoughtAt` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`InvoiceID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `invoices`
--

LOCK TABLES `invoices` WRITE;
/*!40000 ALTER TABLE `invoices` DISABLE KEYS */;
/*!40000 ALTER TABLE `invoices` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lecture`
--

DROP TABLE IF EXISTS `lecture`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lecture` (
  `LECTUREID` int NOT NULL AUTO_INCREMENT,
  `LECTURENAME` varchar(512) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `COURSEID` int NOT NULL,
  `VIDEOURL` varchar(1024) NOT NULL,
  `DURATION` smallint NOT NULL,
  `LECTUREDESCRIPTION` varchar(512) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  PRIMARY KEY (`LECTUREID`),
  KEY `COURSEID` (`COURSEID`),
  CONSTRAINT `lecture_ibfk_1` FOREIGN KEY (`COURSEID`) REFERENCES `courses` (`COURSEID`) ON DELETE CASCADE,
  CONSTRAINT `lecture_chk_1` CHECK ((`DURATION` > 0))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lecture`
--

LOCK TABLES `lecture` WRITE;
/*!40000 ALTER TABLE `lecture` DISABLE KEYS */;
/*!40000 ALTER TABLE `lecture` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lectureprogress`
--

DROP TABLE IF EXISTS `lectureprogress`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lectureprogress` (
  `USERID` int NOT NULL,
  `LECTUREID` int NOT NULL,
  `STATUS` enum('in-progress','done','unfinished') NOT NULL DEFAULT 'unfinished',
  PRIMARY KEY (`USERID`,`LECTUREID`),
  KEY `LECTUREID` (`LECTUREID`),
  CONSTRAINT `lectureprogress_ibfk_1` FOREIGN KEY (`USERID`) REFERENCES `users` (`USERID`) ON DELETE CASCADE,
  CONSTRAINT `lectureprogress_ibfk_2` FOREIGN KEY (`LECTUREID`) REFERENCES `lecture` (`LECTUREID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lectureprogress`
--

LOCK TABLES `lectureprogress` WRITE;
/*!40000 ALTER TABLE `lectureprogress` DISABLE KEYS */;
/*!40000 ALTER TABLE `lectureprogress` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mycart`
--

DROP TABLE IF EXISTS `mycart`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mycart` (
  `UserID` int NOT NULL,
  `CourseID` int NOT NULL,
  `IsBuy` bit(1) DEFAULT b'0',
  `AddedAt` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`UserID`,`CourseID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mycart`
--

LOCK TABLES `mycart` WRITE;
/*!40000 ALTER TABLE `mycart` DISABLE KEYS */;
/*!40000 ALTER TABLE `mycart` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mylearning`
--

DROP TABLE IF EXISTS `mylearning`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mylearning` (
  `USERID` int NOT NULL,
  `COURSEID` int NOT NULL,
  `COURSESTATUS` enum('in-progress','finished') NOT NULL DEFAULT 'in-progress',
  `LASTACCESSEDAT` datetime DEFAULT NULL,
  PRIMARY KEY (`USERID`,`COURSEID`),
  KEY `COURSEID` (`COURSEID`),
  CONSTRAINT `mylearning_ibfk_1` FOREIGN KEY (`USERID`) REFERENCES `users` (`USERID`) ON DELETE CASCADE,
  CONSTRAINT `mylearning_ibfk_2` FOREIGN KEY (`COURSEID`) REFERENCES `courses` (`COURSEID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mylearning`
--

LOCK TABLES `mylearning` WRITE;
/*!40000 ALTER TABLE `mylearning` DISABLE KEYS */;
/*!40000 ALTER TABLE `mylearning` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mywishlist`
--

DROP TABLE IF EXISTS `mywishlist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mywishlist` (
  `USERID` int NOT NULL,
  `COURSEID` int NOT NULL,
  `ADDEDAT` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`USERID`,`COURSEID`),
  KEY `COURSEID` (`COURSEID`),
  CONSTRAINT `mywishlist_ibfk_1` FOREIGN KEY (`USERID`) REFERENCES `users` (`USERID`) ON DELETE CASCADE,
  CONSTRAINT `mywishlist_ibfk_2` FOREIGN KEY (`COURSEID`) REFERENCES `courses` (`COURSEID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mywishlist`
--

LOCK TABLES `mywishlist` WRITE;
/*!40000 ALTER TABLE `mywishlist` DISABLE KEYS */;
/*!40000 ALTER TABLE `mywishlist` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notification`
--

DROP TABLE IF EXISTS `notification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notification` (
  `NotificationID` int NOT NULL AUTO_INCREMENT,
  `NotificationName` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `Content` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `NotifiedAt` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`NotificationID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notification`
--

LOCK TABLES `notification` WRITE;
/*!40000 ALTER TABLE `notification` DISABLE KEYS */;
/*!40000 ALTER TABLE `notification` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notificationdetail`
--

DROP TABLE IF EXISTS `notificationdetail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notificationdetail` (
  `NotificationID` int NOT NULL,
  `UserID` int NOT NULL,
  `Status` enum('read','unread') NOT NULL DEFAULT 'unread',
  PRIMARY KEY (`NotificationID`,`UserID`),
  CONSTRAINT `notificationdetail_ibfk_1` FOREIGN KEY (`NotificationID`) REFERENCES `notification` (`NotificationID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notificationdetail`
--

LOCK TABLES `notificationdetail` WRITE;
/*!40000 ALTER TABLE `notificationdetail` DISABLE KEYS */;
/*!40000 ALTER TABLE `notificationdetail` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payments`
--

DROP TABLE IF EXISTS `payments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payments` (
  `PaymentID` int NOT NULL AUTO_INCREMENT,
  `PaymentName` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  PRIMARY KEY (`PaymentID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payments`
--

LOCK TABLES `payments` WRITE;
/*!40000 ALTER TABLE `payments` DISABLE KEYS */;
/*!40000 ALTER TABLE `payments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `permissions`
--

DROP TABLE IF EXISTS `permissions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `permissions` (
  `PERMISSIONID` int NOT NULL AUTO_INCREMENT,
  `PERMISSIONNAME` varchar(256) NOT NULL,
  PRIMARY KEY (`PERMISSIONID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `permissions`
--

LOCK TABLES `permissions` WRITE;
/*!40000 ALTER TABLE `permissions` DISABLE KEYS */;
/*!40000 ALTER TABLE `permissions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rolepermission`
--

DROP TABLE IF EXISTS `rolepermission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rolepermission` (
  `ROLEID` int NOT NULL,
  `PERMISSIONID` int NOT NULL,
  PRIMARY KEY (`ROLEID`,`PERMISSIONID`),
  KEY `PERMISSIONID` (`PERMISSIONID`),
  CONSTRAINT `rolepermission_ibfk_1` FOREIGN KEY (`ROLEID`) REFERENCES `roles` (`ROLEID`),
  CONSTRAINT `rolepermission_ibfk_2` FOREIGN KEY (`PERMISSIONID`) REFERENCES `permissions` (`PERMISSIONID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rolepermission`
--

LOCK TABLES `rolepermission` WRITE;
/*!40000 ALTER TABLE `rolepermission` DISABLE KEYS */;
/*!40000 ALTER TABLE `rolepermission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
  `ROLEID` int NOT NULL AUTO_INCREMENT,
  `ROLENAME` varchar(30) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  PRIMARY KEY (`ROLEID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usercertificate`
--

DROP TABLE IF EXISTS `usercertificate`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usercertificate` (
  `CERTIFICATEID` int NOT NULL,
  `USERID` int NOT NULL,
  `ISSUEDATE` date NOT NULL,
  PRIMARY KEY (`CERTIFICATEID`,`USERID`),
  KEY `USERID` (`USERID`),
  CONSTRAINT `usercertificate_ibfk_1` FOREIGN KEY (`CERTIFICATEID`) REFERENCES `certificate` (`CERTIFICATEID`) ON DELETE CASCADE,
  CONSTRAINT `usercertificate_ibfk_2` FOREIGN KEY (`USERID`) REFERENCES `users` (`USERID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usercertificate`
--

LOCK TABLES `usercertificate` WRITE;
/*!40000 ALTER TABLE `usercertificate` DISABLE KEYS */;
/*!40000 ALTER TABLE `usercertificate` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `userpayment`
--

DROP TABLE IF EXISTS `userpayment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `userpayment` (
  `PaymentID` int NOT NULL,
  `UserID` int NOT NULL,
  `Balance` float NOT NULL,
  PRIMARY KEY (`PaymentID`,`UserID`),
  CONSTRAINT `userpayment_ibfk_1` FOREIGN KEY (`PaymentID`) REFERENCES `payments` (`PaymentID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `userpayment`
--

LOCK TABLES `userpayment` WRITE;
/*!40000 ALTER TABLE `userpayment` DISABLE KEYS */;
/*!40000 ALTER TABLE `userpayment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `USERID` int NOT NULL AUTO_INCREMENT,
  `ROLEID` int DEFAULT NULL,
  `USERFIRSTNAME` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `USERLASTNAME` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `USERNAME` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `PASSWORD` char(20) NOT NULL,
  `PHONENUMBER` varchar(15) DEFAULT NULL,
  `EMAIL` char(50) NOT NULL,
  `DESCRIPTION` varchar(512) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `STATUS` enum('online','offline','banned') DEFAULT NULL,
  `CREATEDAT` date NOT NULL,
  PRIMARY KEY (`USERID`),
  UNIQUE KEY `USERNAME` (`USERNAME`),
  UNIQUE KEY `EMAIL` (`EMAIL`),
  UNIQUE KEY `PHONENUMBER` (`PHONENUMBER`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,1,'Dương Hoàng','Nam','NamDayIT','NamhoangduongIT11@','0379233412','namduong@gmail.com','Tôi dạy IT','online','2025-04-12'),(2,2,'Nguyễn Thị','Link','LinhHocSinh','LinhCute2712@','0977544321','linhnguyen@gmail.com','Đam mê IT','online','2025-04-12'),(3,2,'Micheal','Jackson','MusicianLoveIT','Heaven1980@','0879233412','michealjackson@gmail.com','I love Music, I Love IT','online','2025-04-12'),(4,1,'Emily','Presis','TeachingAI','Emily4457@','0978233412','emilypresis@gmail.com','Hi I\'m Emily Presis, and I\'m teaching AI check my courses','online','2025-04-12');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-04-12  1:55:47
