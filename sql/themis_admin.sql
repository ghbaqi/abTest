/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80017
 Source Host           : 127.0.0.1:3306
 Source Schema         : themis_admin

 Target Server Type    : MySQL
 Target Server Version : 80017
 File Encoding         : 65001

 Date: 03/03/2020 23:29:57
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for experiment_filter
-- ----------------------------
DROP TABLE IF EXISTS `experiment_filter`;
CREATE TABLE `experiment_filter` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modify_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `tag_id` bigint(20) DEFAULT NULL COMMENT '标签ID',
  `tag_table_name` varchar(80) NOT NULL COMMENT '标签表的名字',
  `tag_column_name` varchar(255) DEFAULT NULL COMMENT '标签表的字段',
  `operator` varchar(80) DEFAULT NULL COMMENT '操作符',
  `value` varchar(255) DEFAULT NULL COMMENT '值',
  `next_filter_relation` varchar(80) DEFAULT NULL COMMENT '与下一个关系AND/OR',
  `sort` int(5) NOT NULL COMMENT '组内排序',
  `group_id` varchar(100) NOT NULL COMMENT '组编码',
  `group_sort` int(5) NOT NULL COMMENT '组排序',
  `experiment_group_id` bigint(20) DEFAULT NULL COMMENT '实验配置ID',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_experiment_group_id` (`experiment_group_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='实验过滤表';

-- ----------------------------
-- Table structure for experiment_group
-- ----------------------------
DROP TABLE IF EXISTS `experiment_group`;
CREATE TABLE `experiment_group` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modify_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `name` varchar(80) DEFAULT NULL COMMENT '实验名称',
  `terminal_type` varchar(10) NOT NULL DEFAULT 'CLIENT' COMMENT '客户端实验或者服务端实验',
  `param_key` varchar(80) NOT NULL COMMENT '实验key',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `user_name` varchar(100) NOT NULL COMMENT '用户名',
  `layer_id` bigint(20) NOT NULL COMMENT '层ID',
  `layer_name` varchar(80) NOT NULL COMMENT '层名称',
  `bucket_num` int(10) NOT NULL DEFAULT '10000' COMMENT '实验组所有桶数',
  `bucket_ids` text COMMENT '实验组所有桶ID,逗号拼接',
  `surplus_bucket_num` int(10) NOT NULL COMMENT '实验组所有剩余桶数',
  `surplus_bucket_ids` text COMMENT '实验组剩余桶ID,逗号拼接',
  `description` varchar(255) DEFAULT NULL COMMENT '实验组描述',
  `status` varchar(50) NOT NULL DEFAULT 'STOP' COMMENT '状态 TEST -> RUNNING -> STOP',
  `rate_flow_strategy` varchar(50) DEFAULT NULL,
  `bis_line` varchar(80) DEFAULT NULL COMMENT '业务线',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `duration` bigint(20) DEFAULT NULL,
  `bis_Line_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_key` (`param_key`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8 COMMENT='实验组配置';

-- ----------------------------
-- Table structure for experiment_item
-- ----------------------------
DROP TABLE IF EXISTS `experiment_item`;
CREATE TABLE `experiment_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modify_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `param_key` varchar(80) NOT NULL COMMENT '实验key(冗余)',
  `param_value` varchar(10) NOT NULL DEFAULT 'CLIENT' COMMENT '值',
  `bucket_ids` text COMMENT '桶ID,逗号拼接',
  `bucket_num` int(10) NOT NULL COMMENT '实验参数桶数',
  `experiment_group_bucket_num` int(10) NOT NULL COMMENT '所属实验组桶数',
  `item_type` varchar(100) NOT NULL COMMENT '类型实验组/对照组',
  `white_list` text COMMENT '白名单',
  `name` varchar(80) DEFAULT NULL COMMENT '实验组名称',
  `description` varchar(255) DEFAULT NULL COMMENT '实验组描述',
  `experiment_group_id` bigint(20) NOT NULL COMMENT '实验组ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 COMMENT='实验详情';

-- ----------------------------
-- Table structure for experiment_tag
-- ----------------------------
DROP TABLE IF EXISTS `experiment_tag`;
CREATE TABLE `experiment_tag` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modify_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `table_name` varchar(80) NOT NULL COMMENT '标签表的名字',
  `column_name` varchar(255) DEFAULT NULL COMMENT '标签表的字段',
  `description` varchar(500) DEFAULT NULL COMMENT '描述',
  `value_type` int(1) NOT NULL COMMENT '值类型值 1:String 2:Number 3:Field 4:Boolean ',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8 COMMENT='标签表';

-- ----------------------------
-- Table structure for experiments_pages
-- ----------------------------
DROP TABLE IF EXISTS `experiments_pages`;
CREATE TABLE `experiments_pages` (
  `experiment_group_id` bigint(20) NOT NULL COMMENT '实验组ID',
  `page_id` bigint(20) NOT NULL COMMENT '页面ID',
  PRIMARY KEY (`experiment_group_id`,`page_id`) USING BTREE,
  KEY `idx_page_id` (`page_id`) USING BTREE,
  KEY `idx_experiment_group_id` (`experiment_group_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='实验参数配置和页面中间表';

-- ----------------------------
-- Table structure for layer
-- ----------------------------
DROP TABLE IF EXISTS `layer`;
CREATE TABLE `layer` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modify_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `name` varchar(80) NOT NULL COMMENT '层名称',
  `description` varchar(255) DEFAULT NULL COMMENT '层描述',
  `surplus_bucket_ids` text COMMENT '层剩余桶ID,逗号拼接',
  `surplus_bucket_num` int(10) NOT NULL DEFAULT '10000' COMMENT '层所有桶数',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_name` (`name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='层配置';

-- ----------------------------
-- Table structure for log
-- ----------------------------
DROP TABLE IF EXISTS `log`;
CREATE TABLE `log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `exception_detail` text COMMENT '异常详情',
  `log_type` varchar(255) DEFAULT NULL COMMENT '日志类型',
  `method` varchar(255) DEFAULT NULL COMMENT '方法名',
  `params` text COMMENT '参数',
  `request_ip` varchar(255) DEFAULT NULL COMMENT '请求IP',
  `time` bigint(20) DEFAULT NULL COMMENT '运行时间',
  `username` varchar(255) DEFAULT NULL COMMENT '用户名',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='系统运行日志';

-- ----------------------------
-- Table structure for menu
-- ----------------------------
DROP TABLE IF EXISTS `menu`;
CREATE TABLE `menu` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
  `i_frame` bit(1) DEFAULT NULL COMMENT '是否外链',
  `name` varchar(255) DEFAULT NULL COMMENT '菜单名称',
  `component` varchar(255) DEFAULT NULL COMMENT '组件',
  `pid` bigint(20) NOT NULL COMMENT '上级菜单ID',
  `sort` bigint(20) NOT NULL COMMENT '排序',
  `icon` varchar(255) DEFAULT NULL COMMENT '图标',
  `path` varchar(255) DEFAULT NULL COMMENT '链接地址',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='系统菜单表(URL级别的控制)';

-- ----------------------------
-- Records of menu
-- ----------------------------
BEGIN;
INSERT INTO `menu` VALUES (1, '2020-02-18 15:14:44', b'0', '全部实验', 'system/experiment', 1, 2, 'peoples', 'user');
INSERT INTO `menu` VALUES (2, '2020-02-18 15:14:44', b'0', '全部实验', 'system/experiment', 1, 2, 'peoples', 'user');
COMMIT;

-- ----------------------------
-- Table structure for menus_roles
-- ----------------------------
DROP TABLE IF EXISTS `menus_roles`;
CREATE TABLE `menus_roles` (
  `menu_id` bigint(20) NOT NULL COMMENT '菜单ID',
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`menu_id`,`role_id`) USING BTREE,
  KEY `idx_role_id` (`role_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='角色菜单中间表';

-- ----------------------------
-- Records of menus_roles
-- ----------------------------
BEGIN;
INSERT INTO `menus_roles` VALUES (1, 1);
COMMIT;

-- ----------------------------
-- Table structure for page
-- ----------------------------
DROP TABLE IF EXISTS `page`;
CREATE TABLE `page` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modify_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `spm` varchar(80) NOT NULL COMMENT '页面SPM',
  `description` varchar(255) DEFAULT NULL COMMENT '页面描述',
  `category` varchar(80) NOT NULL COMMENT '业务线',
  `terminal_type` varchar(80) NOT NULL COMMENT '终端',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=77 DEFAULT CHARSET=utf8 COMMENT='页面配置';

-- ----------------------------
-- Records of page
-- ----------------------------
BEGIN;
INSERT INTO `page` VALUES (1, '2020-02-27 07:06:32', '2020-02-27 07:06:32', 'PXPB0001', '公共页面', 'PB', 'CLIENT');
INSERT INTO `page` VALUES (2, '2020-03-01 15:22:31', '2020-03-01 15:22:31', 'PHBB0032', 'NBA球队榜', 'BB', 'H5');
INSERT INTO `page` VALUES (3, '2020-03-01 15:22:31', '2020-03-01 15:22:31', 'PHBB0036', 'NBA薪资榜', 'BB', 'H5');
INSERT INTO `page` VALUES (4, '2020-03-01 15:22:31', '2020-03-01 15:22:31', 'PHBB0051', '比赛内页-前瞻', 'BB', 'H5');
INSERT INTO `page` VALUES (5, '2020-03-01 15:22:31', '2020-03-01 15:22:31', 'PHBB0067', '球队赛季页', 'BB', 'H5');
INSERT INTO `page` VALUES (6, '2020-03-01 15:22:31', '2020-03-01 15:22:31', 'PHBB0108', '球员评分页', 'BB', 'H5');
INSERT INTO `page` VALUES (7, '2020-03-01 15:22:31', '2020-03-01 15:22:31', 'PHBB0109', '球员评分榜', 'BB', 'H5');
INSERT INTO `page` VALUES (8, '2020-03-01 15:22:31', '2020-03-01 15:22:31', 'PHBB0112', '历史榜详细名单页', 'BB', 'H5');
INSERT INTO `page` VALUES (9, '2020-03-01 15:22:31', '2020-03-01 15:22:31', 'PHBB0113', '历史榜单页', 'BB', 'H5');
INSERT INTO `page` VALUES (10, '2020-03-01 15:22:31', '2020-03-01 15:22:31', 'PHBB0114', '中国篮球球队榜', 'BB', 'H5');
INSERT INTO `page` VALUES (11, '2020-03-01 15:22:31', '2020-03-01 15:22:31', 'PHSC0041', '足球球员首页', 'SC', 'H5');
INSERT INTO `page` VALUES (12, '2020-03-01 15:22:31', '2020-03-01 15:22:31', 'PXBB0010', 'NBA首页', 'BB', 'CLIENT');
INSERT INTO `page` VALUES (13, '2020-03-01 15:22:31', '2020-03-01 15:22:31', 'PXBB0011', '新闻视频综合页面', 'BB', 'CLIENT');
INSERT INTO `page` VALUES (14, '2020-03-01 15:22:31', '2020-03-01 15:22:31', 'PXBB0020', '中国篮球首页', 'BB', 'CLIENT');
INSERT INTO `page` VALUES (15, '2020-03-01 15:22:31', '2020-03-01 15:22:31', 'PXBB0030', 'NBA比赛首页', 'BB', 'CLIENT');
INSERT INTO `page` VALUES (16, '2020-03-01 15:22:31', '2020-03-01 15:22:31', 'PXBB0031', 'NBA赛程', 'BB', 'CLIENT');
INSERT INTO `page` VALUES (17, '2020-03-01 15:22:31', '2020-03-01 15:22:31', 'PXBB0040', '中国篮球页', 'BB', 'CLIENT');
INSERT INTO `page` VALUES (18, '2020-03-01 15:22:31', '2020-03-01 15:22:31', 'PXBB0041', '中国篮球赛程', 'BB', 'CLIENT');
INSERT INTO `page` VALUES (19, '2020-03-01 15:22:31', '2020-03-01 15:22:31', 'PXBB0050', '比赛内页', 'BB', 'CLIENT');
INSERT INTO `page` VALUES (20, '2020-03-01 15:22:31', '2020-03-01 15:22:31', 'PXBB0052', '比赛内页-直播', 'BB', 'CLIENT');
INSERT INTO `page` VALUES (21, '2020-03-01 15:22:31', '2020-03-01 15:22:31', 'PXBB0053', '篮球-比赛内页-统计', 'BB', 'CLIENT');
INSERT INTO `page` VALUES (22, '2020-03-01 15:22:31', '2020-03-01 15:22:31', 'PXBB0054', '比赛内页-热线', 'BB', 'CLIENT');
INSERT INTO `page` VALUES (23, '2020-03-01 15:22:31', '2020-03-01 15:22:31', 'PXBB0055', '比赛内页-竞猜', 'BB', 'CLIENT');
INSERT INTO `page` VALUES (24, '2020-03-01 15:22:31', '2020-03-01 15:22:31', 'PXBB0060', '球队页', 'BB', 'CLIENT');
INSERT INTO `page` VALUES (25, '2020-03-01 15:22:31', '2020-03-01 15:22:31', 'PXBB0061', '球队新闻', 'BB', 'CLIENT');
INSERT INTO `page` VALUES (26, '2020-03-01 15:22:31', '2020-03-01 15:22:31', 'PXBB0062', '球队赛程', 'BB', 'CLIENT');
INSERT INTO `page` VALUES (27, '2020-03-01 15:22:31', '2020-03-01 15:22:31', 'PXBS0001', '社区推荐', 'BS', 'CLIENT');
INSERT INTO `page` VALUES (28, '2020-03-01 15:22:31', '2020-03-01 15:22:31', 'PXBS0002', '关注页', 'BS', 'CLIENT');
INSERT INTO `page` VALUES (29, '2020-03-01 15:22:31', '2020-03-01 15:22:31', 'PXBS0003', '话题页', 'BS', 'CLIENT');
INSERT INTO `page` VALUES (30, '2020-03-01 15:22:31', '2020-03-01 15:22:31', 'PXBS0004', '帖子详情', 'BS', 'CLIENT');
INSERT INTO `page` VALUES (31, '2020-03-01 15:22:31', '2020-03-01 15:22:31', 'PXBS0005', '话题、版块内页', 'BS', 'CLIENT');
INSERT INTO `page` VALUES (32, '2020-03-01 15:22:31', '2020-03-01 15:22:31', 'PXBS0006', '图文发帖页', 'BS', 'CLIENT');
INSERT INTO `page` VALUES (33, '2020-03-01 15:22:31', '2020-03-01 15:22:31', 'PXBS0007', '社区tab', 'BS', 'CLIENT');
INSERT INTO `page` VALUES (34, '2020-03-01 15:22:31', '2020-03-01 15:22:31', 'PXBS0008', '移动端24小时榜', 'BS', 'CLIENT');
INSERT INTO `page` VALUES (35, '2020-03-01 15:22:31', '2020-03-01 15:22:31', 'PXBS0009', '移动端视频发帖页', 'BS', 'CLIENT');
INSERT INTO `page` VALUES (36, '2020-03-01 15:22:31', '2020-03-01 15:22:31', 'PXBS0010', '移动端视频tab页', 'BS', 'CLIENT');
INSERT INTO `page` VALUES (37, '2020-03-01 15:22:31', '2020-03-01 15:22:31', 'PXBS0011', '移动端视频全屏页', 'BS', 'CLIENT');
INSERT INTO `page` VALUES (38, '2020-03-01 15:22:31', '2020-03-01 15:22:31', 'PXBS0012', '移动端视频列表页', 'BS', 'CLIENT');
INSERT INTO `page` VALUES (39, '2020-03-01 15:22:31', '2020-03-01 15:22:31', 'PXPB0001', '公共页', 'PB', 'CLIENT');
INSERT INTO `page` VALUES (40, '2020-03-01 15:22:31', '2020-03-01 15:22:31', 'PXPB0002', '图片查看器', 'PB', 'CLIENT');
INSERT INTO `page` VALUES (41, '2020-03-01 15:22:31', '2020-03-01 15:22:31', 'PXPB0003', '更多页', 'PB', 'CLIENT');
INSERT INTO `page` VALUES (42, '2020-03-01 15:22:31', '2020-03-01 15:22:31', 'PXPB0004', '用户收藏页', 'PB', 'CLIENT');
INSERT INTO `page` VALUES (43, '2020-03-01 15:22:31', '2020-03-01 15:22:31', 'PXPB0006', '我的主队页', 'PB', 'CLIENT');
INSERT INTO `page` VALUES (44, '2020-03-01 15:22:31', '2020-03-01 15:22:31', 'PXPB0007', '账号安全页', 'PB', 'CLIENT');
INSERT INTO `page` VALUES (45, '2020-03-01 15:22:31', '2020-03-01 15:22:31', 'PXPB0008', '管理黑名单页', 'PB', 'CLIENT');
INSERT INTO `page` VALUES (46, '2020-03-01 15:22:31', '2020-03-01 15:22:31', 'PXPB0009', '图片浏览设置', 'PB', 'CLIENT');
INSERT INTO `page` VALUES (47, '2020-03-01 15:22:31', '2020-03-01 15:22:31', 'PXPB0010', '推送通知', 'PB', 'CLIENT');
INSERT INTO `page` VALUES (48, '2020-03-01 15:22:31', '2020-03-01 15:22:31', 'PXPB0011', '视频播放设置', 'PB', 'CLIENT');
INSERT INTO `page` VALUES (49, '2020-03-01 15:22:31', '2020-03-01 15:22:31', 'PXPB0012', '更多页管理', 'PB', 'CLIENT');
INSERT INTO `page` VALUES (50, '2020-03-01 15:22:31', '2020-03-01 15:22:31', 'PXPB0013', 'APP启动页', 'PB', 'CLIENT');
INSERT INTO `page` VALUES (51, '2020-03-01 15:22:31', '2020-03-01 15:22:31', 'PXPB0014', '关于我们页', 'PB', 'CLIENT');
INSERT INTO `page` VALUES (52, '2020-03-01 15:22:31', '2020-03-01 15:22:31', 'PXPB0015', '用户首页', 'PB', 'CLIENT');
INSERT INTO `page` VALUES (53, '2020-03-01 15:22:31', '2020-03-01 15:22:31', 'PXPB0016', '个人资料编辑页', 'PB', 'CLIENT');
INSERT INTO `page` VALUES (54, '2020-03-01 15:22:31', '2020-03-01 15:22:31', 'PXPB0020', '启动APP时先显示的页面', 'PB', 'CLIENT');
INSERT INTO `page` VALUES (55, '2020-03-01 15:22:31', '2020-03-01 15:22:31', 'PXPB0029', '浏览记录页', 'PB', 'CLIENT');
INSERT INTO `page` VALUES (56, '2020-03-01 15:22:31', '2020-03-01 15:22:31', 'PXPB0030', '我的消息页', 'PB', 'CLIENT');
INSERT INTO `page` VALUES (57, '2020-03-01 15:22:31', '2020-03-01 15:22:31', 'PXPB0032', '新用户兴趣选择页', 'PB', 'CLIENT');
INSERT INTO `page` VALUES (58, '2020-03-01 15:22:31', '2020-03-01 15:22:31', 'PXPB0033', '新用户首页自定义导航页', 'PB', 'CLIENT');
INSERT INTO `page` VALUES (59, '2020-03-01 15:22:31', '2020-03-01 15:22:31', 'PXPB0034', '比赛自定义导航页', 'PB', 'CLIENT');
INSERT INTO `page` VALUES (60, '2020-03-01 15:22:31', '2020-03-01 15:22:31', 'PXPB0036', '基线-搜索结果页', 'PB', 'CLIENT');
INSERT INTO `page` VALUES (61, '2020-03-01 15:22:31', '2020-03-01 15:22:31', 'PXPB0037', '新用户自定义球队页', 'PB', 'CLIENT');
INSERT INTO `page` VALUES (62, '2020-03-01 15:22:31', '2020-03-01 15:22:31', 'PXSC0001', '新闻列表页', 'SC', 'CLIENT');
INSERT INTO `page` VALUES (63, '2020-03-01 15:22:31', '2020-03-01 15:22:31', 'PXSC0002', '国际足球赛程列表', 'SC', 'CLIENT');
INSERT INTO `page` VALUES (64, '2020-03-01 15:22:31', '2020-03-01 15:22:31', 'PXSC0013', '比赛赛况页', 'SC', 'CLIENT');
INSERT INTO `page` VALUES (65, '2020-03-01 15:22:31', '2020-03-01 15:22:31', 'PXSC0014', '足球比赛直播页', 'SC', 'CLIENT');
INSERT INTO `page` VALUES (66, '2020-03-01 15:22:31', '2020-03-01 15:22:31', 'PXSC0021', '球队新闻页', 'SC', 'CLIENT');
INSERT INTO `page` VALUES (67, '2020-03-01 15:22:31', '2020-03-01 15:22:31', 'PXSC0023', '球队数据页', 'SC', 'CLIENT');
INSERT INTO `page` VALUES (68, '2020-03-01 15:22:31', '2020-03-01 15:22:31', 'PXSC0024', '球队球员列表页', 'SC', 'CLIENT');
INSERT INTO `page` VALUES (69, '2020-03-01 15:22:31', '2020-03-01 15:22:31', 'PXSC0025', '球队转化页', 'SC', 'CLIENT');
INSERT INTO `page` VALUES (70, '2020-03-01 15:22:31', '2020-03-01 15:22:31', 'PXSC0026', '球队资料页', 'SC', 'CLIENT');
INSERT INTO `page` VALUES (71, '2020-03-01 15:22:31', '2020-03-01 15:22:31', 'PXSC0037', '足球视频页', 'SC', 'CLIENT');
INSERT INTO `page` VALUES (72, '2020-03-01 15:22:31', '2020-03-01 15:22:31', 'PXSC0039', '足球比赛首页', 'SC', 'CLIENT');
INSERT INTO `page` VALUES (73, '2020-03-01 15:22:31', '2020-03-01 15:22:31', 'PXSC0040', '足球球队首页', 'SC', 'CLIENT');
INSERT INTO `page` VALUES (74, '2020-03-01 15:22:31', '2020-03-01 15:22:31', 'PXSC0042', '榜单数据汇总页', 'SC', 'CLIENT');
INSERT INTO `page` VALUES (75, '2020-03-18 17:19:42', '2020-03-18 17:19:42', 'PMPB0001', 'M站首页', 'PB', 'M');
INSERT INTO `page` VALUES (76, '2020-03-18 17:20:09', '2020-03-18 17:20:09', 'PPPB0001', 'PC站首页', 'PB', 'PC');
COMMIT;

-- ----------------------------
-- Table structure for permission
-- ----------------------------
DROP TABLE IF EXISTS `permission`;
CREATE TABLE `permission` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `alias` varchar(255) DEFAULT NULL COMMENT '别名',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
  `name` varchar(255) DEFAULT NULL COMMENT '名称',
  `pid` int(11) NOT NULL COMMENT '上级权限',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8 COMMENT='权限表(方法级别的控制)';

-- ----------------------------
-- Records of permission
-- ----------------------------
BEGIN;
INSERT INTO `permission` VALUES (1, '超级管理员', '2019-12-03 12:27:48', 'ADMIN', 0);
INSERT INTO `permission` VALUES (2, '用户管理', '2019-12-03 12:28:19', 'USER_ALL', 0);
INSERT INTO `permission` VALUES (3, '用户查询', '2019-12-03 12:31:35', 'USER_SELECT', 2);
INSERT INTO `permission` VALUES (4, '用户创建', '2019-12-03 12:31:35', 'USER_CREATE', 2);
INSERT INTO `permission` VALUES (5, '用户编辑', '2019-12-03 12:31:35', 'USER_EDIT', 2);
INSERT INTO `permission` VALUES (6, '用户删除', '2019-12-03 12:31:35', 'USER_DELETE', 2);
INSERT INTO `permission` VALUES (7, '角色管理', '2019-12-03 12:28:19', 'ROLES_ALL', 0);
INSERT INTO `permission` VALUES (8, '角色查询', '2019-12-03 12:31:35', 'ROLES_SELECT', 7);
INSERT INTO `permission` VALUES (10, '角色创建', '2019-12-09 20:10:16', 'ROLES_CREATE', 7);
INSERT INTO `permission` VALUES (11, '角色编辑', '2019-12-09 20:10:42', 'ROLES_EDIT', 7);
INSERT INTO `permission` VALUES (12, '角色删除', '2019-12-09 20:11:07', 'ROLES_DELETE', 7);
INSERT INTO `permission` VALUES (13, '权限管理', '2019-12-09 20:11:37', 'PERMISSION_ALL', 0);
INSERT INTO `permission` VALUES (14, '权限查询', '2019-12-09 20:11:55', 'PERMISSION_SELECT', 13);
INSERT INTO `permission` VALUES (15, '权限创建', '2019-12-09 20:14:10', 'PERMISSION_CREATE', 13);
INSERT INTO `permission` VALUES (16, '权限编辑', '2019-12-09 20:15:44', 'PERMISSION_EDIT', 13);
INSERT INTO `permission` VALUES (17, '权限删除', '2019-12-09 20:15:59', 'PERMISSION_DELETE', 13);
INSERT INTO `permission` VALUES (18, '缓存管理', '2019-12-17 13:53:25', 'REDIS_ALL', 0);
INSERT INTO `permission` VALUES (20, '缓存查询', '2019-12-17 13:54:07', 'REDIS_SELECT', 18);
INSERT INTO `permission` VALUES (22, '缓存删除', '2019-12-17 13:55:04', 'REDIS_DELETE', 18);
INSERT INTO `permission` VALUES (23, '菜单管理', '2019-12-28 17:34:31', 'MENU_ALL', 0);
INSERT INTO `permission` VALUES (24, '菜单查询', '2019-12-28 17:34:41', 'MENU_SELECT', 23);
INSERT INTO `permission` VALUES (25, '菜单创建', '2019-12-28 17:34:52', 'MENU_CREATE', 23);
INSERT INTO `permission` VALUES (26, '菜单编辑', '2019-12-28 17:35:20', 'MENU_EDIT', 23);
INSERT INTO `permission` VALUES (27, '菜单删除', '2019-12-28 17:35:29', 'MENU_DELETE', 23);
INSERT INTO `permission` VALUES (28, '实验管理', '2019-12-28 17:34:31', 'EXPERIMENT_GROUP_ALL', 0);
INSERT INTO `permission` VALUES (29, '实验查询', '2019-12-28 17:34:41', 'EXPERIMENT_GROUP_SELECT', 23);
INSERT INTO `permission` VALUES (30, '实验创建', '2019-12-28 17:34:52', 'EXPERIMENT_GROUP_CREATE', 23);
INSERT INTO `permission` VALUES (31, '实验编辑', '2019-12-28 17:35:20', 'EXPERIMENT_GROUP_EDIT', 23);
INSERT INTO `permission` VALUES (32, '实验删除', '2019-12-28 17:35:29', 'EXPERIMENT_GROUP_DELETE', 23);
INSERT INTO `permission` VALUES (33, '通用查询', '2019-12-28 17:35:29', 'GENERAL_SELECT', 0);
COMMIT;

-- ----------------------------
-- Table structure for quartz_job
-- ----------------------------
DROP TABLE IF EXISTS `quartz_job`;
CREATE TABLE `quartz_job` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `bean_name` varchar(255) DEFAULT NULL COMMENT 'Spring Bean名称',
  `cron_expression` varchar(255) DEFAULT NULL COMMENT 'cron 表达式',
  `is_pause` bit(1) DEFAULT NULL COMMENT '状态：1暂停、0启用',
  `job_name` varchar(255) DEFAULT NULL COMMENT '任务名称',
  `method_name` varchar(255) DEFAULT NULL COMMENT '方法名称',
  `params` varchar(255) DEFAULT NULL COMMENT '参数',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建或更新日期',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='定时任务配置表';

-- ----------------------------
-- Table structure for quartz_log
-- ----------------------------
DROP TABLE IF EXISTS `quartz_log`;
CREATE TABLE `quartz_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `bean_name` varchar(255) DEFAULT NULL COMMENT '实例名字',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `cron_expression` varchar(255) DEFAULT NULL COMMENT '时间表达式',
  `exception_detail` text COMMENT '异常详情',
  `is_success` bit(1) DEFAULT NULL COMMENT '是否成功',
  `job_name` varchar(255) DEFAULT NULL COMMENT '任务名',
  `method_name` varchar(255) DEFAULT NULL COMMENT '方法名',
  `params` varchar(255) DEFAULT NULL COMMENT '参数',
  `time` bigint(20) DEFAULT NULL COMMENT '运行时长',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='定时任务执行日志';

-- ----------------------------
-- Table structure for role
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
  `name` varchar(255) NOT NULL COMMENT '名称',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `data_scope` varchar(255) DEFAULT NULL COMMENT '数据范围',
  `level` int(255) DEFAULT NULL COMMENT '用户等级',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='角色表';

-- ----------------------------
-- Records of role
-- ----------------------------
BEGIN;
INSERT INTO `role` VALUES (1, '2020-02-01 11:04:37', '超级管理员', '系统所有权', '自定义', 1);
INSERT INTO `role` VALUES (2, '2020-02-01 11:04:37', '实验查询配置人员', '实验查询配置权限', '自定义', 1);
COMMIT;

-- ----------------------------
-- Table structure for roles_permissions
-- ----------------------------
DROP TABLE IF EXISTS `roles_permissions`;
CREATE TABLE `roles_permissions` (
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `permission_id` bigint(20) NOT NULL COMMENT '权限ID',
  PRIMARY KEY (`role_id`,`permission_id`) USING BTREE,
  KEY `idx_permission_id` (`permission_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='角色权限中间表';

-- ----------------------------
-- Records of roles_permissions
-- ----------------------------
BEGIN;
INSERT INTO `roles_permissions` VALUES (1, 1);
INSERT INTO `roles_permissions` VALUES (1, 2);
INSERT INTO `roles_permissions` VALUES (1, 3);
INSERT INTO `roles_permissions` VALUES (1, 4);
INSERT INTO `roles_permissions` VALUES (1, 5);
INSERT INTO `roles_permissions` VALUES (1, 6);
INSERT INTO `roles_permissions` VALUES (1, 7);
INSERT INTO `roles_permissions` VALUES (1, 8);
INSERT INTO `roles_permissions` VALUES (1, 10);
INSERT INTO `roles_permissions` VALUES (1, 11);
INSERT INTO `roles_permissions` VALUES (1, 12);
INSERT INTO `roles_permissions` VALUES (1, 13);
INSERT INTO `roles_permissions` VALUES (1, 14);
INSERT INTO `roles_permissions` VALUES (1, 15);
INSERT INTO `roles_permissions` VALUES (1, 16);
INSERT INTO `roles_permissions` VALUES (1, 17);
INSERT INTO `roles_permissions` VALUES (1, 18);
INSERT INTO `roles_permissions` VALUES (1, 20);
INSERT INTO `roles_permissions` VALUES (1, 22);
INSERT INTO `roles_permissions` VALUES (1, 23);
INSERT INTO `roles_permissions` VALUES (1, 24);
INSERT INTO `roles_permissions` VALUES (1, 25);
INSERT INTO `roles_permissions` VALUES (1, 26);
INSERT INTO `roles_permissions` VALUES (1, 27);
INSERT INTO `roles_permissions` VALUES (1, 28);
INSERT INTO `roles_permissions` VALUES (2, 28);
INSERT INTO `roles_permissions` VALUES (1, 29);
INSERT INTO `roles_permissions` VALUES (2, 29);
INSERT INTO `roles_permissions` VALUES (1, 30);
INSERT INTO `roles_permissions` VALUES (2, 30);
INSERT INTO `roles_permissions` VALUES (1, 31);
INSERT INTO `roles_permissions` VALUES (2, 31);
INSERT INTO `roles_permissions` VALUES (1, 32);
INSERT INTO `roles_permissions` VALUES (2, 32);
INSERT INTO `roles_permissions` VALUES (1, 33);
INSERT INTO `roles_permissions` VALUES (2, 33);
COMMIT;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像地址',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
  `email` varchar(255) DEFAULT NULL COMMENT '邮箱',
  `enabled` bigint(20) DEFAULT NULL COMMENT '状态：1启用、0禁用',
  `password` varchar(255) DEFAULT NULL COMMENT '密码',
  `username` varchar(100) DEFAULT NULL COMMENT '用户名',
  `last_password_reset_time` datetime DEFAULT NULL COMMENT '最后修改密码的日期',
  `phone` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_email` (`email`) USING BTREE,
  UNIQUE KEY `uk_username` (`username`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='用户表';

-- ----------------------------
-- Records of user
-- ----------------------------
BEGIN;
INSERT INTO `user` VALUES (1, '', '2019-08-23 09:11:56', 'admin@hupu_themis.com', 1, '40fbe32a8a5789ea8f62f978c81d2ba7', 'admin', '2019-09-19 00:09:05', '18888888888');
COMMIT;

-- ----------------------------
-- Table structure for users_roles
-- ----------------------------
DROP TABLE IF EXISTS `users_roles`;
CREATE TABLE `users_roles` (
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`,`role_id`) USING BTREE,
  KEY `idx_role_id` (`role_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户角色中间表';

-- ----------------------------
-- Records of users_roles
-- ----------------------------
BEGIN;
INSERT INTO `users_roles` VALUES (1, 1);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
