package com.hanqf.demo.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

/**
 * 用户表
 * Created by hanqf on 2025/8/27 17:14.
 */
@Setter
@Getter
@TableName("t_user")
public class User implements java.io.Serializable {
    @Serial
    private static final long serialVersionUID = -7970564112098055294L;
    @TableId
    private Long id;
    private String name;


}
