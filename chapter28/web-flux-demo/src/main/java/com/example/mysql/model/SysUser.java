package com.example.mysql.model;

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
    @Id
    private String id;

    private String username;

    private String password;

    private Boolean enable;

}


