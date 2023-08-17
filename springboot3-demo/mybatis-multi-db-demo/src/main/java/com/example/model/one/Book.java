package com.example.model.one;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by Mybatis Generator on 2023-08-16 18:13:45
 */
@Data
public class Book implements Serializable {
    /**
     */
    private Long id;

    /**
     */
    private String bookname;

    /**
     */
    private Double price;

    /**
     */
    private Integer totalpage;

    /**
     */
    private Long userid;

    private static final long serialVersionUID = 1L;
}
