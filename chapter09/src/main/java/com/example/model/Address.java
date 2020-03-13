package com.example.model;/**
 * Created by hanqf on 2020/3/12 11:29.
 */


import javax.persistence.*;

/**
 * @author hanqf
 * @date 2020/3/12 11:29
 */
@Entity(name = "address")
@Table(name = "address")
public class Address
{
    //主键，自增
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "province")
    private String province;
    @Column(name = "city")
    private String city;

    //@Column(name = "userId")
    //private Long userId;

    @OneToOne(fetch=FetchType.EAGER)
    //因为上面声明了独立的属性userId，所以这里必须设置insertable = false,updatable = false
    //@JoinColumn(name="userId",nullable=true,insertable = false,updatable = false)
    @JoinColumn(name="userId",nullable=true)
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Address{" +
                "id=" + id +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                //", userId=" + userId +
                ", user=" + user +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    //public Long getUserId() {
    //    return userId;
    //}
    //
    //public void setUserId(Long userId) {
    //    this.userId = userId;
    //}
}
