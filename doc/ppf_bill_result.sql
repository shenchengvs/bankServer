/*
Navicat MySQL Data Transfer

Source Server         : charging
Source Server Version : 50537
Source Host           : 172.16.15.55:3306
Source Database       : emcp_0313

Target Server Type    : MYSQL
Target Server Version : 50537
File Encoding         : 65001

Date: 2018-05-30 09:16:14
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for ppf_bill_result
-- ----------------------------
DROP TABLE IF EXISTS `ppf_bill_result`;
CREATE TABLE `ppf_bill_result` (
  `CUSTOMER_ID` int(8) NOT NULL,
  `DATA_TIME` datetime NOT NULL,
  `ENERGY_TYPE` smallint(4) NOT NULL,
  `CUT_COUNT` smallint(4) DEFAULT NULL,
  `RETURN_INFO` varchar(64) DEFAULT NULL,
  `ADD_TIME` datetime NOT NULL,
  `UPDATE_TIME` datetime NOT NULL,
  PRIMARY KEY (`CUSTOMER_ID`,`DATA_TIME`,`ENERGY_TYPE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
