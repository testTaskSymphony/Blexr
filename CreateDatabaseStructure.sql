CREATE DATABASE  IF NOT EXISTS `blexr` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */;
USE `blexr`;
-- MySQL dump 10.13  Distrib 5.7.17, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: blexr
-- ------------------------------------------------------
-- Server version	8.0.11

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
-- Table structure for table `brand`
--

DROP TABLE IF EXISTS `brand`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `brand` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `NAME` varchar(65) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `ID_UNIQUE` (`ID`),
  UNIQUE KEY `NAME_UNIQUE` (`NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `game`
--

DROP TABLE IF EXISTS `game`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `game` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `NAME` varchar(100) DEFAULT NULL,
  `DETAILS` varchar(500) DEFAULT NULL,
  `URL` varchar(200) DEFAULT NULL,
  `IMAGE_ID` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `ID_UNIQUE` (`ID`),
  KEY `ID_idx` (`IMAGE_ID`),
  CONSTRAINT `ID` FOREIGN KEY (`IMAGE_ID`) REFERENCES `image` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3793 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `game_brand`
--

DROP TABLE IF EXISTS `game_brand`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `game_brand` (
  `GAME_ID` int(11) DEFAULT NULL,
  `BRAND_NAME` varchar(65) DEFAULT NULL,
  UNIQUE KEY `unique_index` (`GAME_ID`,`BRAND_NAME`),
  KEY `GAME_ID_idx` (`GAME_ID`),
  KEY `GAME_ID_idxx` (`GAME_ID`),
  KEY `BRAND_ID_idxx` (`BRAND_NAME`),
  CONSTRAINT `GAME_ID_BRAND` FOREIGN KEY (`GAME_ID`) REFERENCES `game` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `game_jurisdiction`
--

DROP TABLE IF EXISTS `game_jurisdiction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `game_jurisdiction` (
  `GAME_ID` int(11) NOT NULL,
  `JURISDICTION_ID` int(11) NOT NULL,
  UNIQUE KEY `unique_index` (`GAME_ID`,`JURISDICTION_ID`),
  KEY `GAME_ID_idx` (`GAME_ID`),
  KEY `JURISDICTION_ID_idx` (`JURISDICTION_ID`),
  CONSTRAINT `GAME_ID` FOREIGN KEY (`GAME_ID`) REFERENCES `game` (`id`),
  CONSTRAINT `JURISDICTION_ID` FOREIGN KEY (`JURISDICTION_ID`) REFERENCES `jurisdiction` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `game_platform`
--

DROP TABLE IF EXISTS `game_platform`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `game_platform` (
  `GAME_ID` int(11) DEFAULT NULL,
  `PLATFORM_NAME` varchar(65) DEFAULT NULL,
  UNIQUE KEY `unique_index` (`GAME_ID`,`PLATFORM_NAME`),
  KEY `GAME_ID_PLATFORM_idx` (`GAME_ID`),
  CONSTRAINT `GAME_ID_PLATFORM` FOREIGN KEY (`GAME_ID`) REFERENCES `game` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `game_reel`
--

DROP TABLE IF EXISTS `game_reel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `game_reel` (
  `GAME_ID` int(11) DEFAULT NULL,
  `REEL` varchar(45) DEFAULT NULL,
  UNIQUE KEY `unique_index` (`GAME_ID`,`REEL`),
  KEY `GAME_ID_REEL_idx` (`GAME_ID`),
  CONSTRAINT `GAME_ID_REEL` FOREIGN KEY (`GAME_ID`) REFERENCES `game` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `game_type`
--

DROP TABLE IF EXISTS `game_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `game_type` (
  `GAME_ID` int(11) DEFAULT NULL,
  `GAME_TYPE` varchar(45) DEFAULT NULL,
  UNIQUE KEY `unique_index` (`GAME_ID`,`GAME_TYPE`),
  KEY `GAME_ID_TYPE_idx` (`GAME_ID`),
  CONSTRAINT `GAME_ID_TYPE` FOREIGN KEY (`GAME_ID`) REFERENCES `game` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `image`
--

DROP TABLE IF EXISTS `image`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `image` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `MD5` varchar(150) DEFAULT NULL,
  `FILE` longblob,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `ID_UNIQUE` (`ID`),
  UNIQUE KEY `MD5l_UNIQUE` (`MD5`)
) ENGINE=InnoDB AUTO_INCREMENT=6595 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jurisdiction`
--

DROP TABLE IF EXISTS `jurisdiction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jurisdiction` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `NAME` varchar(100) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `ID_UNIQUE` (`ID`),
  UNIQUE KEY `NAME_UNIQUE` (`NAME`)
) ENGINE=InnoDB AUTO_INCREMENT=2476 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2018-07-04 13:11:03
