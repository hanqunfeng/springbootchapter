package com.example.model;/**
 * Created by hanqf on 2020/2/28 14:16.
 */


import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;

/**
 * @author hanqf
 * @date 2020/2/28 14:16
 */
@TableName("user")
public class UserPlus implements Serializable {

    private static final long serialVersionUID = 1L;
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

    @Override
    public String toString() {
        return "UserPlus{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", email='" + email + '\'' +
                ", delStatus='" + delStatus + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDelStatus() {
        return delStatus;
    }

    public void setDelStatus(String delStatus) {
        this.delStatus = delStatus;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
