package com.example.model.one;/**
 * Created by hanqf on 2020/3/5 16:49.
 */


import javax.persistence.*;

/**
 * @author hanqf
 * @date 2020/3/5 16:49
 */
//这里给实体起了名字，所以在查询中要用user代替类名，默认类名吃
@Entity(name = "oneUser")
@Table(name = "user")
public class OneUser {

    //主键，自增
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "age")
    private Integer age;
    @Column(name = "email")
    private String email;

    //枚举类型转换
    @Column(name = "del")
    private String del;

    @Override
    public String toString() {
        return "OneUser{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", email='" + email + '\'' +
                ", del='" + del + '\'' +
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

    public String getDel() {
        return del;
    }

    public void setDel(String del) {
        this.del = del;
    }
}
