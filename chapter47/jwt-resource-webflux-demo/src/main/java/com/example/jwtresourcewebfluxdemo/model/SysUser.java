package com.example.jwtresourcewebfluxdemo.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;

/**
 * <h1>系统登录用户表</h1>
 * Created by hanqf on 2020/11/19 10:01.
 */


@Data
@Table("sys_user")
public class SysUser implements Serializable {
    private static final long serialVersionUID = -5339117952867854660L;
    /**
     * 非自增主键，即需要人为指定主键的时候，执行默认的新增会报错，
     * r2dbc默认的行为是如果主键为空，则认证是新增操作，此时需要数据库支持主键填充，如果主键值不为空，则认为是修改操作
     * 所以需要重新设计新增方法
    */
    @Id
    private String id;

    private String username;

    private String password;

    private Boolean enable;

}


