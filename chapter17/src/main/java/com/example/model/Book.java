package com.example.model;/**
 * Created by hanqf on 2020/3/16 13:58.
 */


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

/**
 * @author hanqf
 * @date 2020/3/16 13:58
 */
@Document(collection = "books")
public class Book implements Serializable {

    private static final long serialVersionUID = -7229906944062898852L;

    /** ID */
    @Id
    private String id;

    /** 书名 */
    @Field(name = "bookName")
    private String bookName;

    /** 关联的用户Id */
    @Field(name = "userId")
    private String  userId;

    /** 价格 */
    @Field(name = "price")
    private Double price;

    /** 页数 */
    @Field(name = "totalPage")
    private Integer totalPage;

    @Override
    public String toString() {
        return "Book{" +
                "id='" + id + '\'' +
                ", bookName='" + bookName + '\'' +
                ", userId='" + userId + '\'' +
                ", price=" + price +
                ", totalPage=" + totalPage +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }
}
