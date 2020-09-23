package com.cas.model;

/**
 * <p></p>
 * Created by hanqf on 2020/9/22 16:18.
 */


public class User {
    private String username;
    //密码MD5
    private String password;
    private String email;
    //0：没过期，1：已过期，需要修改密码
    private Integer expired;
    //0：有效 1：禁用
    private Integer disabled;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getExpired() {
        return expired;
    }

    public void setExpired(Integer expired) {
        this.expired = expired;
    }

    public Integer getDisabled() {
        return disabled;
    }

    public void setDisabled(Integer disabled) {
        this.disabled = disabled;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", expired=" + expired +
                ", disabled=" + disabled +
                '}';
    }
}
