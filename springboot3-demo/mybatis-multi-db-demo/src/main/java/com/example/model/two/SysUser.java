package com.example.model.two;

import java.io.Serializable;
import lombok.Data;

/**
 * Created by Mybatis Generator on 2023-08-16 17:49:29
 */
@Data
public class SysUser implements Serializable {
    /**
     * 用户id
     */
    private String id;

    /**
     * 用户名称
     */
    private String username;

    /**
     * 用户密码
     */
    private String password;

    /**
     * 是否有效
     */
    private Boolean enable;

    private static final long serialVersionUID = 1L;
}