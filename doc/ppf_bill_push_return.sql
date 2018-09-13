/*
Navicat MySQL Data Transfer

Source Server         : charging
Source Server Version : 50537
Source Host           : 172.16.15.55:3306
Source Database       : emcp_0313

Target Server Type    : MYSQL
Target Server Version : 50537
File Encoding         : 65001

Date: 2018-05-30 09:15:55
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for ppf_bill_push_return
-- ----------------------------
DROP TABLE IF EXISTS `ppf_bill_push_return`;
CREATE TABLE `ppf_bill_push_return` (
  `ADD_TIME` datetime NOT NULL,
  `TYPE` smallint(4) NOT NULL COMMENT '1.推送2.返回3.签约4.解约',
  `ENERGY_TYPE` smallint(4) NOT NULL COMMENT '1.电2.水5.气',
  `CONTENT` mediumtext,
  PRIMARY KEY (`ADD_TIME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
