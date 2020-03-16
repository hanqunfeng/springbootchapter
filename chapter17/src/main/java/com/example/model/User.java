package com.example.model;/**
 * Created by hanqf on 2020/3/16 13:58.
 */


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Arrays;

/**
 * @author hanqf
 * @date 2020/3/16 13:58
 */
@Document(collection = "user")
public class User implements Serializable {

    private static final long serialVersionUID = -7229906944062898852L;

    /** ID */
    @Id
    private String id;

    /** 用户名 */
    @Field(name = "name")
    private String userName;

    /** 年龄 */
    @Field(name = "age")
    private Integer age;

    /** 邮箱 */
    @Field(name = "email")
    private String email;

    /** 博客地址 */
    @Field(name = "blog")
    private String blog;

    /** 标签 */
    @Field(name = "tags")
    private String[] tags;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", age=" + age +
                ", email='" + email + '\'' +
                ", blog='" + blog + '\'' +
                ", tags=" + Arrays.toString(tags) +
                '}';
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public String getBlog() {
        return blog;
    }

    public void setBlog(String blog) {
        this.blog = blog;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }
}
