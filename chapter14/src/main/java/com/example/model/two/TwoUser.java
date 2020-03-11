package com.example.model.two;

import org.apache.ibatis.type.Alias;

import javax.persistence.*;
import java.io.Serializable;


/**
 * user
 * @author 
 */

@Alias("twoUser")
@Table(name = "user")
public class TwoUser implements Serializable {
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", email='" + email + '\'' +
                ", del='" + del + '\'' +
                ", page=" + page +
                ", rows=" + rows +
                '}';
    }

    /**
     * 主键ID
     */
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 姓名
     */
    @Column(name = "name")
    private String name;

    /**
     * 年龄
     */
    @Column(name = "age")
    private Integer age;

    /**
     * 邮箱
     */
    @Column(name = "email")
    private String email;

    @Column(name = "del")
    private String del;

    //不进行映射
    @Transient
    private Integer page = 1;

    @Transient
    private Integer rows = 10;
    private static final long serialVersionUID = 1L;


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

    public String getDel() {
        return del;
    }

    public void setDel(String del) {
        this.del = del;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }
}