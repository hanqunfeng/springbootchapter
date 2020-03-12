package com.example.model;/**
 * Created by hanqf on 2020/3/12 11:30.
 */


import javax.persistence.*;

/**
 * @author hanqf
 * @date 2020/3/12 11:30
 */
@Table(name = "books")
public class Book
{
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "bookName")
    private String bookName;
    @Column(name = "totalPage")
    private Integer totalPage;
    @Column(name = "price")
    private Double price;
    @Column(name = "userId")
    private Long userId;

    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", bookName='" + bookName + '\'' +
                ", totalPage=" + totalPage +
                ", price=" + price +
                ", userId=" + userId +
                ", user=" + user +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
