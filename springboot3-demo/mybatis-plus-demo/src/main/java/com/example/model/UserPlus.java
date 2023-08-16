package com.example.model;/**
 * Created by hanqf on 2020/2/28 14:16.
 */


import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author hanqf
 * @date 2020/2/28 14:16
 */
@Data
@TableName("user")
public class UserPlus implements Serializable {

    @Serial
    private static final long serialVersionUID = 3422120821198028606L;

    //id
    @TableId(value = "id",type=IdType.AUTO)
    private Long id;
    //普通字段
    @TableField("name")
    private String name;
    @TableField("age")
    private Integer age;
    @TableField("email")
    private String email;

    @TableField("del")
    //逻辑删除，即执行删除时该字段被更新为1
    @TableLogic(value = "0",delval = "1")
    private String delStatus;

    //不与表关联的字段
    @TableField(exist = false)
    private String status;

}
