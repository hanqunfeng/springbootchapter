package com.example.model.one;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Mybatis Generator on 2023-08-16 18:13:56
 */
@Data
public class User implements Serializable {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 姓名
     */
    private String name;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 邮箱
     */
    private String email;

    /**
     */
    private String del;

    private List<Book> books;

    private static final long serialVersionUID = 1L;
}
