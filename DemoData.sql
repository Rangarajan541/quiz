-- MySQL dump 10.13  Distrib 5.7.15, for Win32 (AMD64)
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
  `username` varchar(50) DEFAULT NULL,
  `activity` varchar(250) DEFAULT NULL,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `activitylog`
--

LOCK TABLES `activitylog` WRITE;
/*!40000 ALTER TABLE `activitylog` DISABLE KEYS */;
INSERT INTO `activitylog` VALUES ('teach','Teacher application submitted','2016-09-18 12:01:23'),('teach','User logged in','2016-09-18 12:01:39'),('teach','User logged out','2016-09-18 12:09:36'),('teach','User logged in','2016-09-18 12:10:47'),('teach','User logged out','2016-09-18 12:11:44'),('amit','student registered','2016-09-18 12:11:56'),('teach','User logged in','2016-09-18 12:12:01'),('teach','User logged out','2016-09-18 12:12:05'),('amit','User logged in','2016-09-18 12:12:11'),('amit','User Started Test','2016-09-18 12:12:25'),('amit','User Finished Test','2016-09-18 12:17:42'),('amit','User logged out','2016-09-18 12:23:16'),('amit','student registered','2016-09-18 12:40:41'),('amit','User logged in','2016-09-18 12:40:44'),('amit','User Started Test','2016-09-18 12:40:47'),('amit','User Aborted Test','2016-09-18 12:40:53'),('amit','User Finished Test','2016-09-18 12:40:53'),('amit','User logged out','2016-09-18 12:41:42');
/*!40000 ALTER TABLE `activitylog` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `errorlog`
--

DROP TABLE IF EXISTS `errorlog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `errorlog` (
  `username` varchar(50) DEFAULT NULL,
  `particulars` varchar(2500) DEFAULT NULL,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `errorlog`
--

LOCK TABLES `errorlog` WRITE;
/*!40000 ALTER TABLE `errorlog` DISABLE KEYS */;
INSERT INTO `errorlog` VALUES ('teach','C:UsersElcotDocumentsip_1Question_1.jpg','2016-09-18 12:03:48');
/*!40000 ALTER TABLE `errorlog` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student_auth`
--

DROP TABLE IF EXISTS `student_auth`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `student_auth` (
  `name` varchar(50) NOT NULL,
  `password` varchar(512) DEFAULT NULL,
  `onlinestatus` int(1) DEFAULT NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student_auth`
--

LOCK TABLES `student_auth` WRITE;
/*!40000 ALTER TABLE `student_auth` DISABLE KEYS */;
INSERT INTO `student_auth` VALUES ('amit','1661548902539077689835780616605042428580731493960493799641588268547596371700',0);
/*!40000 ALTER TABLE `student_auth` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `studenthistorydatabase_amit`
--

DROP TABLE IF EXISTS `studenthistorydatabase_amit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `studenthistorydatabase_amit` (
  `testid` varchar(50) DEFAULT NULL,
  `marksearned` int(5) DEFAULT NULL,
  `aborted` int(1) DEFAULT NULL,
  `cheatwarnings` int(2) DEFAULT NULL,
  `datetaken` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `studenthistorydatabase_amit`
--

LOCK TABLES `studenthistorydatabase_amit` WRITE;
/*!40000 ALTER TABLE `studenthistorydatabase_amit` DISABLE KEYS */;
INSERT INTO `studenthistorydatabase_amit` VALUES ('ip_1',0,1,0,'2016-09-18 12:40:53');
/*!40000 ALTER TABLE `studenthistorydatabase_amit` ENABLE KEYS */;
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
INSERT INTO `systemsettings` VALUES ('totalcheatseconds','25'),('totalallowedwarnings','0'),('wakeupseconds','0'),('flashwarningseconds','60'),('loglocation','C:/Users/Elcot/Documents/Error Log.txt'),('reslocation','C:/Users/Elcot/Documents/');
/*!40000 ALTER TABLE `systemsettings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `teacher_auth`
--

DROP TABLE IF EXISTS `teacher_auth`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `teacher_auth` (
  `name` varchar(50) NOT NULL,
  `password` varchar(512) DEFAULT NULL,
  `subject` varchar(30) DEFAULT NULL,
  `status` int(1) DEFAULT NULL,
  `onlinestatus` int(1) DEFAULT NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `teacher_auth`
--

LOCK TABLES `teacher_auth` WRITE;
/*!40000 ALTER TABLE `teacher_auth` DISABLE KEYS */;
INSERT INTO `teacher_auth` VALUES ('teach','1661548902539077689835780616605042428580731493960493799641588268547596371700','Informatics Practices',1,0);
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
  `username` varchar(50) DEFAULT NULL,
  `description` varchar(50) DEFAULT NULL,
  `subject` varchar(50) DEFAULT NULL,
  `points` int(2) DEFAULT NULL,
  `status` int(1) DEFAULT NULL,
  `datecreated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `seconds` int(11) DEFAULT NULL,
  PRIMARY KEY (`testid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `testlist`
--

LOCK TABLES `testlist` WRITE;
/*!40000 ALTER TABLE `testlist` DISABLE KEYS */;
INSERT INTO `testlist` VALUES ('ip_1','teach','asd','Informatics Practices',1,1,'2016-09-18 12:12:03',1800);
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
INSERT INTO `testquestions_ip_1` VALUES (1,'asjdkajsdkjahlsdjkhasljdhajshdjasdasdas','a','C:/Users/Elcot/Documents/ip_1/Question_1.jpg',0),(2,'aksjd;kasj;dkajs;ldkja s;lkdja;skdjajksh','b','C:/Users/Elcot/Documents/ip_1/Question_2.jpg',0),(3,'aksdlakjsdhajshdlajshdjashdlkjashdljkashdjkasda','c','C:/Users/Elcot/Documents/ip_1/Question_3.jpg',0),(4,'ashdjh,sajdhl,aksda','d','',0);
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

-- Dump completed on 2016-09-18 22:07:38
