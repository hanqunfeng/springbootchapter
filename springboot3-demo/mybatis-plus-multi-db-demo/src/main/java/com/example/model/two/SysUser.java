package com.example.model.two;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * Created by Mybatis Generator on 2023-08-16 17:49:29
 */
@Data
@TableName("sys_user")
public class SysUser implements Serializable {
    @Serial
    private static final long serialVersionUID = 1740967655391153231L;
    /**
     * 用户id
     */
    @TableId(value = "id",type= IdType.AUTO)
    private String id;

    /**
     * 用户名称
     */
    @TableField("username")
    private String username;

    /**
     * 用户密码
     */
    @TableField("password")
    private String password;

    /**
     * 是否有效
     */
    @TableField("enable")
    private Boolean enable;

}
