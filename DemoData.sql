-- MySQL dump 10.13  Distrib 5.7.15, for Win64 (x86_64)
--
-- Host: localhost    Database: quiz
-- ------------------------------------------------------
-- Server version	5.7.15-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `activitylog`
--

DROP TABLE IF EXISTS `activitylog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `activitylog` (
  `userid` varchar(50) DEFAULT NULL,
  `activity` varchar(250) DEFAULT NULL,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `activitylog`
--

LOCK TABLES `activitylog` WRITE;
/*!40000 ALTER TABLE `activitylog` DISABLE KEYS */;
INSERT INTO `activitylog` VALUES ('topTeacher1!','Teacher application submitted','2016-09-27 05:49:52'),('topTeacher1!','User logged in','2016-09-27 05:50:18'),('topTeacher1!','User logged out','2016-09-27 05:56:56'),('Rangarajan A','student registered','2016-09-27 05:57:13'),('rajanranga541','User logged in','2016-09-27 05:57:17'),('rajanranga541','User logged out','2016-09-27 05:57:26'),('topTeacher1!','User logged in','2016-09-27 05:57:46'),('topTeacher1!','User logged out','2016-09-27 05:58:01'),('rajanranga541','User logged in','2016-09-27 05:58:11'),('rajanranga541','User logged out','2016-09-27 06:03:15'),('topteacher1!','User logged in','2016-09-27 06:04:35'),('topteacher1!','User logged out','2016-09-27 06:06:13'),('topteacher1!','User logged in','2016-09-27 06:06:38'),('topteacher1!','User logged in','2016-09-27 06:08:14'),('topteacher1!','User logged out','2016-09-27 06:08:26'),('topteacher1!','User logged out','2016-09-27 06:08:31'),('topteacher1!','User logged in','2016-09-27 06:08:40'),('topteacher1!','User logged out','2016-09-27 06:10:25'),('topteacher1!','User logged in','2016-09-27 06:10:41'),('topteacher1!','User logged out','2016-09-27 06:14:03'),('topteacher1!','User logged in','2016-09-27 06:14:22'),('topteacher1!','User logged out','2016-09-27 06:14:31'),('rajanranga541','User logged in','2016-09-27 06:14:40'),('rajanranga541','User Started Test','2016-09-27 06:14:52'),('rajanranga541','User Finished Test','2016-09-27 06:16:14'),('rajanranga541','User logged out','2016-09-27 06:22:29'),('rajanranga541','User logged in','2016-09-27 06:22:44'),('rajanranga541','User logged out','2016-09-27 06:22:50'),('topteacher1!','User logged in','2016-09-27 06:23:09'),('topteacher1!','User logged out','2016-09-27 06:25:25'),('topteacher1!','User logged in','2016-09-27 06:57:50'),('topteacher1!','User logged out','2016-09-27 06:58:03');
/*!40000 ALTER TABLE `activitylog` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `errorlog`
--

DROP TABLE IF EXISTS `errorlog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `errorlog` (
  `userid` varchar(50) DEFAULT NULL,
  `particulars` varchar(2500) DEFAULT NULL,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `errorlog`
--

LOCK TABLES `errorlog` WRITE;
/*!40000 ALTER TABLE `errorlog` DISABLE KEYS */;
INSERT INTO `errorlog` VALUES ('topteacher1!','Unknown column \'username\' in \'where clause\'','2016-09-27 06:23:34');
/*!40000 ALTER TABLE `errorlog` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `instantcheatalarm`
--

DROP TABLE IF EXISTS `instantcheatalarm`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `instantcheatalarm` (
  `logno` int(11) NOT NULL,
  `testid` varchar(50) DEFAULT NULL,
  `username` varchar(50) DEFAULT NULL,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`logno`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `instantcheatalarm`
--

LOCK TABLES `instantcheatalarm` WRITE;
/*!40000 ALTER TABLE `instantcheatalarm` DISABLE KEYS */;
/*!40000 ALTER TABLE `instantcheatalarm` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student_auth`
--

DROP TABLE IF EXISTS `student_auth`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `student_auth` (
  `userid` varchar(50) NOT NULL,
  `name` varchar(50) NOT NULL,
  `password` varchar(512) DEFAULT NULL,
  `onlinestatus` int(1) DEFAULT NULL,
  `standard` int(2) DEFAULT NULL,
  PRIMARY KEY (`userid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student_auth`
--

LOCK TABLES `student_auth` WRITE;
/*!40000 ALTER TABLE `student_auth` DISABLE KEYS */;
INSERT INTO `student_auth` VALUES ('rajanranga541','Rangarajan A','1661548902539077689835780616605042428580731493960493799641588268547596371700',0,12);
/*!40000 ALTER TABLE `student_auth` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `studenthistorydatabase_rajanranga541`
--

DROP TABLE IF EXISTS `studenthistorydatabase_rajanranga541`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `studenthistorydatabase_rajanranga541` (
  `testid` varchar(50) DEFAULT NULL,
  `marksearned` int(5) DEFAULT NULL,
  `aborted` int(1) DEFAULT NULL,
  `cheatwarnings` int(2) DEFAULT NULL,
  `datetaken` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `question_2` varchar(1) DEFAULT 'x',
  `question_3` varchar(1) DEFAULT 'x',
  `question_4` varchar(1) DEFAULT 'x',
  `question_1` varchar(1) DEFAULT 'x'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `studenthistorydatabase_rajanranga541`
--

LOCK TABLES `studenthistorydatabase_rajanranga541` WRITE;
/*!40000 ALTER TABLE `studenthistorydatabase_rajanranga541` DISABLE KEYS */;
/*!40000 ALTER TABLE `studenthistorydatabase_rajanranga541` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `systemsettings`
--

DROP TABLE IF EXISTS `systemsettings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `systemsettings` (
  `identifier` varchar(50) DEFAULT NULL,
  `data` varchar(5000) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `systemsettings`
--

LOCK TABLES `systemsettings` WRITE;
/*!40000 ALTER TABLE `systemsettings` DISABLE KEYS */;
INSERT INTO `systemsettings` VALUES ('totalcheatseconds','10'),('totalallowedwarnings','2'),('wakeupseconds','300'),('flashwarningseconds','60'),('loglocation','C:/Quiz/Error log.txt'),('reslocation','C:/Quiz/Resources/'),('instantcheatalarm','1'),('studentregistrationsallowed','1');
/*!40000 ALTER TABLE `systemsettings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `teacher_auth`
--

DROP TABLE IF EXISTS `teacher_auth`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `teacher_auth` (
  `userid` varchar(50) NOT NULL,
  `name` varchar(50) NOT NULL,
  `password` varchar(512) DEFAULT NULL,
  `subject` varchar(30) DEFAULT NULL,
  `status` int(1) DEFAULT NULL,
  `onlinestatus` int(1) DEFAULT NULL,
  PRIMARY KEY (`userid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `teacher_auth`
--

LOCK TABLES `teacher_auth` WRITE;
/*!40000 ALTER TABLE `teacher_auth` DISABLE KEYS */;
INSERT INTO `teacher_auth` VALUES ('topTeacher1!','Mr.Table Electric Socket','61889384266393409576963907938984124852536365869469708533778895900682962015625','Informatics Practices',1,0);
/*!40000 ALTER TABLE `teacher_auth` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `testlist`
--

DROP TABLE IF EXISTS `testlist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `testlist` (
  `testid` varchar(50) NOT NULL,
  `userid` varchar(50) DEFAULT NULL,
  `description` varchar(50) DEFAULT NULL,
  `subject` varchar(50) DEFAULT NULL,
  `points` int(2) DEFAULT NULL,
  `status` int(1) DEFAULT NULL,
  `datecreated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `seconds` int(11) DEFAULT NULL,
  `standard` int(2) DEFAULT NULL,
  PRIMARY KEY (`testid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `testlist`
--

LOCK TABLES `testlist` WRITE;
/*!40000 ALTER TABLE `testlist` DISABLE KEYS */;
INSERT INTO `testlist` VALUES ('ip_1','topTeacher1!','Unit 5','Informatics Practices',5,1,'2016-09-26 17:44:27',120,12);
/*!40000 ALTER TABLE `testlist` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `testquestions_ip_1`
--

DROP TABLE IF EXISTS `testquestions_ip_1`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `testquestions_ip_1` (
  `sno` int(11) DEFAULT NULL,
  `question` varchar(2500) DEFAULT NULL,
  `answer` varchar(5) DEFAULT NULL,
  `imagesource` varchar(2500) DEFAULT NULL,
  `reserve` int(1) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `testquestions_ip_1`
--

LOCK TABLES `testquestions_ip_1` WRITE;
/*!40000 ALTER TABLE `testquestions_ip_1` DISABLE KEYS */;
INSERT INTO `testquestions_ip_1` VALUES (1,'This is question 4. The answer is d.','d','C:/Quiz/Resources/ip_1/Question_1.jpg',0),(2,'This is question 3. The answer to this question is c.','c','',0),(3,'This is question 2. This question does not feature an image.','b','',0),(4,'This is question 1. It should display an image and all you have to do is select the appropriate answer.','A','C:/Quiz/Resources/ip_1/Question_4.JPG',0);
/*!40000 ALTER TABLE `testquestions_ip_1` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-09-26 23:59:17
