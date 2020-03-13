package com.example.model;/**
 * Created by hanqf on 2020/3/5 16:49.
 */


import javax.persistence.*;
import java.util.List;

/**
 * @author hanqf
 * @date 2020/3/5 16:49
 */
@Entity(name = "user")
@Table(name = "user")
public class User {

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
    @Convert(converter = DelConverter.class)
    private DelEnum del;

    //映射Address类中的user属性的关联关系，onetoxxx关系中one的一方不要设置为预先抓取，否则可能造成无限循环
    @OneToOne(cascade={CascadeType.DETACH},mappedBy = "user",fetch = FetchType.LAZY)
    private Address userAddress;

    //映射Book类中的user属性的关联关系
    @OneToMany(cascade={CascadeType.DETACH},mappedBy = "user",fetch = FetchType.LAZY)
    private List<Book> books;


    public Address getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(Address userAddress) {
        this.userAddress = userAddress;
    }


    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }




    //toString方法中最好加入映射字段，否则会因为无限循环打印导致内存泄露
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", email='" + email + '\'' +
                ", del=" + del +
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

    public DelEnum getDel() {
        return del;
    }

    public void setDel(DelEnum del) {
        this.del = del;
    }
}
