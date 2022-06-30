package com.example.model;

import java.io.Serializable;

/**
 * <h1>book</h1>
 * Created by hanqf on 2022/6/30 15:31.
 */


public class BookInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    /*
     * 主键
     */
    private java.lang.Long id;


    /*
     * book名称
     */
    private java.lang.String title;


    /*
     * 作者
     */
    private java.lang.String author;



    /*
     * book简介
     */
    private java.lang.String description;



    /*
     * 封面图片连接
     */
    private java.lang.String hcover;



    /*
     * 适合的年龄段
     */
    private java.lang.String age;



    /*
     * 创建时间
     */
    private java.time.LocalDateTime createTime;


    /*
     * 更新时间
     */
    private java.time.LocalDateTime updateTime;



}

