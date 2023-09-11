package com.example.mysql;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serial;
import java.io.Serializable;

/**
 * <h1>SysUser</h1>
 * Created by hanqf on 2023/8/30 17:15.
 */

/**
CREATE TABLE `sys_user` (
        `id` varchar(50) COLLATE utf8mb4_bin NOT NULL COMMENT '用户id',
        `username` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '用户名称',
        `password` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '用户密码',
        `enable` tinyint(1) DEFAULT NULL COMMENT '是否有效',
        PRIMARY KEY (`id`),
        UNIQUE KEY `uni_username` (`username`)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
*/

@Data
@Table("sys_user")
public class SysUser implements Serializable {
    @Serial
    private static final long serialVersionUID = -2301710205992732167L;
    @Id
    private String id;

    private String username;

    private String password;

    private Boolean enable;

}
