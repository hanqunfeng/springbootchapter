-- 用户表
DROP TABLE IF EXISTS `cas_user`;
CREATE TABLE `cas_user`
(
    `id`       int(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `username` varchar(255) DEFAULT NULL COMMENT '用户名',
    `password` varchar(255) DEFAULT NULL COMMENT '密码MD5',
    `expired`  int(11)      DEFAULT NULL COMMENT '0：没过期，1：已过期，需要修改密码',
    `disabled` int(11)      DEFAULT NULL COMMENT '0：有效 1：禁用',
    `email`    varchar(255) DEFAULT NULL COMMENT '邮箱，用户重置密码',
    PRIMARY KEY (`id`),
    UNIQUE KEY `key_username` (`username`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8;
-- 用户数据初始化
INSERT INTO `cas_user` (`id`, `username`, `password`, `expired`, `disabled`)
VALUES ('1', 'admin', 'e10adc3949ba59abbe56e057f20f883e', '0', '0', 'admin@ccc.com'),
       ('2', 'test', 'e10adc3949ba59abbe56e057f20f883e', '1', '0', 'test@xxx.com');

-- 重置密码问题表
DROP TABLE IF EXISTS `cas_userpassword_question`;
CREATE TABLE `cas_userpassword_question`
(
    `id`       INT(11) NOT NULL AUTO_INCREMENT,
    `user_id`  INT(11) NOT NULL,
    `question` VARCHAR(255) DEFAULT NULL,
    `answer`   VARCHAR(255) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8;
-- 关联admin
INSERT INTO `cas_userpassword_question`
VALUES (1, 1, 'Your Name', 'O M G');