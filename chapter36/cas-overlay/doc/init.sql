

DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `username` varchar(255) DEFAULT NULL COMMENT '用户名',
  `password` varchar(255) DEFAULT NULL COMMENT '密码MD5',
  `expired` int(11) DEFAULT NULL COMMENT '0：没过期，1：已过期，需要修改密码',
  `disabled` int(11) DEFAULT NULL COMMENT '0：有效 1：禁用',
  PRIMARY KEY (`id`),
  UNIQUE KEY `key_username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

INSERT INTO `sys_user` (`id`, `username`, `password`, `expired`, `disabled`) VALUES
('1', 'test', 'e10adc3949ba59abbe56e057f20f883e', '1', '0'),
('2', 'admin', 'e10adc3949ba59abbe56e057f20f883e', '0', '0');