-- oauth2中规定的数据表,需要手动创建,一般项目中提供服务接口插入,参数由用户定义,在请求时会自动查询服务器中对应的参数数据匹配认证

CREATE TABLE `oauth_client_details` (
    `client_id` varchar(256) NOT NULL COMMENT '客户端的id',
    `resource_ids` varchar(256) DEFAULT NULL COMMENT '资源服务器的id，多个用，(逗号)隔开',
    `client_secret` varchar(256) DEFAULT NULL COMMENT '客户端的秘钥，需要PasswordEncoder加密',
    `scope` varchar(256) DEFAULT NULL,
    `authorized_grant_types` varchar(256) DEFAULT NULL COMMENT '认证的方式，可选值 授权码模式:authorization_code,密码模式:password,刷新token: refresh_token, 隐式模式: implicit: 客户端模式: client_credentials。支持多个用逗号分隔',
    `web_server_redirect_uri` varchar(256) DEFAULT NULL COMMENT '授权码模式认证成功跳转的地址。authorization_code和implicit需要该值进行校验，注册时填写',
    `authorities` varchar(256) DEFAULT NULL,
    `access_token_validity` int(11) DEFAULT NULL COMMENT 'access_token的有效时间(秒),默认(60 * 60 * 12,12小时)',
    `refresh_token_validity` int(11) DEFAULT NULL COMMENT 'refresh_token有效期(秒)，默认(60 *60 * 24 * 30, 30天)',
    `additional_information` varchar(4096) DEFAULT NULL COMMENT '值必须是json格式',
    `autoapprove` varchar(256) DEFAULT NULL COMMENT '默认false,适用于authorization_code模式,设置用户是否自动approval操作,设置true跳过用户确认授权操作页面，直接跳到redirect_uri',
    PRIMARY KEY (`client_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC;

# 密码要经过加密
INSERT INTO `oauth_client_details` VALUES ('postman', NULL, '$2a$10$Owubqs9VaN.vmskZ2B0UTe0GmOMwgTmhGtlIFBjrfCz2glBqISHSu', 'any,all', 'authorization_code,refresh_token,implicit,password,client_credentials', 'http://localhost:8080/redirect', NULL, 42300, 2592000, NULL, 'any');
INSERT INTO `oauth_client_details` VALUES ('demo-client', NULL, '$2a$10$v/B9.6c9NUXFbJDHqc28he6VWeyJNOBOD1UI7bwBDfBZTwY4zzcda', 'any', 'authorization_code,refresh_token,password', 'http://localhost:8080/redirect', NULL, 42300, 2592000, NULL, '');


# oauth_client_details表每个列的作用
#
# client_id：客户端的id
# 用于唯一标识每一个客户端(client)；注册时必须填写(也可以服务端自动生成)，这个字段是必须的，实际应用也有叫app_key
#
# resource_ids：资源服务器的id，多个用，(逗号)隔开
# 客户端能访问的资源id集合，注册客户端时，根据实际需要可选择资源id，也可以根据不同的注册流程，赋予对应的资源id
#
# client_secret：客户端的秘钥
# 注册填写或者服务端自动生成，实际应用也有叫app_secret, 必须要有前缀代表加密方式
#
# authorized_grant_types：认证的方式
# 可选值 授权码模式:authorization_code,密码模式:password,刷新token: refresh_token, 隐式模式: implicit: 客户端模式: client_credentials。支持多个用逗号分隔
#
# web_server_redirect_uri：授权码模式认证成功跳转的地址
# 客户端重定向uri，authorization_code和implicit需要该值进行校验，注册时填写，
#
# authorities：指定用户的权限范围，如果授权的过程需要用户登陆，该字段不生效，implicit和client_credentials需要
#
# access_token_validity：token的过期时间
# 设置access_token的有效时间(秒),默认(60 * 60 * 12,12小时)
#
# refresh_token_validity：刷新token的过期时间
# 设置refresh_token有效期(秒)，默认(60 *60 * 24 * 30, 30天)
#
# additional_information：值必须是json格式
# autoapprove：默认false,适用于authorization_code模式,设置用户是否自动approval操作,设置true跳过用户确认授权操作页面，直接跳到redirect_uri