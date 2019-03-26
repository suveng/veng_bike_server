/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50725
Source Host           : localhost:3306
Source Database       : bike

Target Server Type    : MYSQL
Target Server Version : 50725
File Encoding         : 65001

Date: 2019-03-26 11:09:29
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for rechargerecord
-- ----------------------------
DROP TABLE IF EXISTS `rechargerecord`;
CREATE TABLE `rechargerecord` (
  `rechareId` bigint(20) NOT NULL AUTO_INCREMENT,
  `userId` varchar(255) NOT NULL DEFAULT '',
  `charge` double(255,0) NOT NULL DEFAULT '0',
  `createTime` timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  `province` varchar(255) NOT NULL DEFAULT '',
  `city` varchar(255) NOT NULL DEFAULT '',
  `district` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`rechareId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for rentalpoint
-- ----------------------------
DROP TABLE IF EXISTS `rentalpoint`;
CREATE TABLE `rentalpoint` (
  `pointId` varchar(255) NOT NULL,
  `longitude` double(255,0) NOT NULL DEFAULT '0',
  `latitude` double(255,0) NOT NULL DEFAULT '0',
  `leftBike` int(255) NOT NULL DEFAULT '0',
  PRIMARY KEY (`pointId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for rentalrecord
-- ----------------------------
DROP TABLE IF EXISTS `rentalrecord`;
CREATE TABLE `rentalrecord` (
  `rentalId` bigint(20) NOT NULL AUTO_INCREMENT,
  `userId` varchar(255) NOT NULL DEFAULT '',
  `vehicleId` varchar(255) NOT NULL DEFAULT '',
  `isFinish` int(255) NOT NULL DEFAULT '0',
  `beginPoint` varchar(255) NOT NULL DEFAULT '',
  `endPoint` varchar(255) NOT NULL DEFAULT '',
  `beginTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `endTime` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`rentalId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `userId` varchar(255) NOT NULL,
  `status` int(255) NOT NULL DEFAULT '0',
  `phoneNum` varchar(255) NOT NULL DEFAULT '0',
  `name` varchar(255) NOT NULL DEFAULT '',
  `idNum` varchar(255) NOT NULL DEFAULT '',
  `deposit` double(255,0) NOT NULL DEFAULT '0',
  `balance` double(255,0) NOT NULL DEFAULT '0',
  PRIMARY KEY (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for vehicle
-- ----------------------------
DROP TABLE IF EXISTS `vehicle`;
CREATE TABLE `vehicle` (
  `vehicleId` varchar(255) NOT NULL,
  `qrCode` varchar(255) NOT NULL DEFAULT '',
  `longitude` double(255,0) NOT NULL DEFAULT '0',
  `latitude` double(255,0) NOT NULL DEFAULT '0',
  `status` int(255) NOT NULL DEFAULT '0',
  `type` int(255) NOT NULL DEFAULT '0',
  `pointId` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`vehicleId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
