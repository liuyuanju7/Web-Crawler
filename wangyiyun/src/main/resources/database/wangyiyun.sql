/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50717
Source Host           : localhost:3306
Source Database       : wangyiyun

Target Server Type    : MYSQL
Target Server Version : 50717
File Encoding         : 65001

Date: 2018-08-23 09:21:21
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `comment`
-- ----------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `commentId` int(11) DEFAULT NULL COMMENT '评论id',
  `songId` varchar(20) DEFAULT NULL,
  `userId` int(11) DEFAULT NULL COMMENT '用户id',
  `avatarUrl` varchar(200) DEFAULT NULL COMMENT '用户头像url',
  `nickname` varchar(50) DEFAULT NULL COMMENT '用户昵称',
  `content` varchar(1000) CHARACTER SET gbk DEFAULT NULL COMMENT '评论内容',
  `time` timestamp NULL DEFAULT NULL COMMENT '评论时间',
  `likeCount` int(11) DEFAULT NULL COMMENT '点赞数',
  PRIMARY KEY (`id`),
  KEY `inx_songId` (`songId`) USING BTREE,
  KEY `inx_userId` (`userId`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of comment
-- ----------------------------

-- ----------------------------
-- Table structure for `playlist`
-- ----------------------------
DROP TABLE IF EXISTS `playlist`;
CREATE TABLE `playlist` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `listId` varchar(30) DEFAULT NULL COMMENT '歌单id',
  `name` varchar(50) DEFAULT NULL COMMENT '歌单名称',
  `musicType` varchar(10) DEFAULT NULL COMMENT '音乐类型',
  `creater` varchar(30) DEFAULT NULL COMMENT '创建人',
  `imgUrl` varchar(200) DEFAULT NULL COMMENT '歌单图片url',
  `count` varchar(10) DEFAULT NULL COMMENT '播放量',
  `des` varchar(500) DEFAULT NULL COMMENT '歌单描述',
  PRIMARY KEY (`id`),
  KEY `inx_listId` (`listId`) USING BTREE,
  KEY `inx_type` (`musicType`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of playlist
-- ----------------------------

-- ----------------------------
-- Table structure for `playlist_song`
-- ----------------------------
DROP TABLE IF EXISTS `playlist_song`;
CREATE TABLE `playlist_song` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `playlistId` varchar(30) NOT NULL COMMENT '歌单id',
  `songId` varchar(20) NOT NULL COMMENT '歌曲id',
  `songName` varchar(500) DEFAULT NULL COMMENT '歌名',
  `singer` varchar(100) DEFAULT NULL COMMENT '歌手',
  `album` varchar(100) DEFAULT NULL COMMENT '专辑',
  PRIMARY KEY (`id`),
  KEY `idx_playlistId` (`playlistId`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of playlist_song
-- ----------------------------

-- ----------------------------
-- Table structure for `song`
-- ----------------------------
DROP TABLE IF EXISTS `song`;
CREATE TABLE `song` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `songId` varchar(20) DEFAULT NULL COMMENT '歌曲id',
  `name` varchar(500) DEFAULT NULL COMMENT '歌曲名',
  `singer` varchar(100) DEFAULT NULL,
  `singerIds` varchar(100) DEFAULT NULL,
  `album` varchar(100) DEFAULT NULL COMMENT '专辑',
  `outChain` varchar(200) DEFAULT NULL COMMENT '外链',
  `commentCount` int(11) DEFAULT NULL,
  `imgUrl` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `inx_songId` (`songId`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of song
-- ----------------------------
