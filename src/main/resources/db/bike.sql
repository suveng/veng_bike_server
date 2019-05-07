/*
 Navicat Premium Data Transfer

 Source Server         : local
 Source Server Type    : MySQL
 Source Server Version : 50703
 Source Host           : localhost:3306
 Source Schema         : bike

 Target Server Type    : MySQL
 Target Server Version : 50703
 File Encoding         : 65001

 Date: 07/05/2019 10:39:38
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for rechargerecord
-- ----------------------------
DROP TABLE IF EXISTS `rechargerecord`;
CREATE TABLE `rechargerecord`  (
  `rechareId` bigint(20) NOT NULL AUTO_INCREMENT,
  `userId` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `charge` double(255, 0) NOT NULL DEFAULT 0,
  `createTime` timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  `province` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `city` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `district` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  PRIMARY KEY (`rechareId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for rentalpoint
-- ----------------------------
DROP TABLE IF EXISTS `rentalpoint`;
CREATE TABLE `rentalpoint`  (
  `pointId` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `longitude` double(255, 6) NOT NULL DEFAULT 0.000000,
  `latitude` double(255, 6) NOT NULL DEFAULT 0.000000,
  `leftBike` int(255) NOT NULL DEFAULT 0,
  PRIMARY KEY (`pointId`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for rentalrecord
-- ----------------------------
DROP TABLE IF EXISTS `rentalrecord`;
CREATE TABLE `rentalrecord`  (
  `rentalId` bigint(20) NOT NULL AUTO_INCREMENT,
  `userId` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `vehicleId` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `isFinish` int(255) NOT NULL DEFAULT 0,
  `beginPoint` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `endPoint` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `beginTime` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  `endTime` timestamp(0) NULL DEFAULT NULL,
  PRIMARY KEY (`rentalId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `userId` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `status` int(2) NOT NULL DEFAULT 0,
  `phoneNum` varchar(15) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0',
  `name` varchar(12) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `idNum` varchar(25) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `deposit` double(255, 0) NOT NULL DEFAULT 0,
  `balance` double(255, 0) NOT NULL DEFAULT 0,
  PRIMARY KEY (`userId`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for vehicle
-- ----------------------------
DROP TABLE IF EXISTS `vehicle`;
CREATE TABLE `vehicle`  (
  `vehicleId` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `qrCode` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `longitude` double(255, 6) NOT NULL DEFAULT 0.000000,
  `latitude` double(255, 6) NOT NULL DEFAULT 0.000000,
  `status` int(255) NOT NULL DEFAULT 0,
  `type` int(255) NOT NULL DEFAULT 0,
  `pointId` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  PRIMARY KEY (`vehicleId`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

SET FOREIGN_KEY_CHECKS = 1;
