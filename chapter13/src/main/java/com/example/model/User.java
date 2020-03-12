package com.example.model;

import tk.mybatis.mapper.annotation.KeySql;
import tk.mybatis.mapper.code.IdentityDialect;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.awt.print.Book;
import java.io.Serializable;
import java.util.List;


/**
 * user
 * @author 
 */
//通用mapper要求必须有主键,如果表名或者字段名称与table里一致，可以不用加注解，否则必须加注解进行关联
@Table(name = "user")
public class User implements Serializable {
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", email='" + email + '\'' +
                ", del='" + del + '\'' +
                ", userAddress=" + userAddress +
                ", books=" + books +
                ", page=" + page +
                ", rows=" + rows +
                '}';
    }


    /**
     * 主键ID
     */
    @Id
    @Column(name = "id")
    //主键自增策略
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    //功能同上，这两种方式都会执行插入后获取id的sql：比如 SELECT LAST_INSERT_ID()
    @KeySql(dialect = IdentityDialect.MYSQL)
    //下面这种方式不会执行获取id的sql，只支持mysql,sqlserver,使用 JDBC 方式获取主键，优先级最高，设置为 true 后，不对其他配置校验
    //@KeySql(useGeneratedKeys = true)
    //oralce可以使用如下方式
    //@KeySql(sql = "select SEQ_ID.nextval from dual", order = ORDER.BEFORE)
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

    private Address userAddress;

    private List<Book> books;

    //不进行映射
    @Transient
    private Integer page = 1;

    @Transient
    private Integer rows = 10;

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }



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