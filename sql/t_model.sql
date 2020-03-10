/*
 Navicat Premium Data Transfer

 Source Server         : localhost(root权限)
 Source Server Type    : MySQL
 Source Server Version : 50562
 Source Host           : localhost:3306
 Source Schema         : images

 Target Server Type    : MySQL
 Target Server Version : 50562
 File Encoding         : 65001

 Date: 09/03/2020 22:52:30
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_model
-- ----------------------------
DROP TABLE IF EXISTS `t_model`;
CREATE TABLE `t_model`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `name` varchar(400) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '模特名字',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 525 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '模特' ROW_FORMAT = Compact;

SET FOREIGN_KEY_CHECKS = 1;
