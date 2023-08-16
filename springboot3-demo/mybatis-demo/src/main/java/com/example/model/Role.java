package com.example.model;/**
 * Created by hanqf on 2020/3/13 14:56.
 */


import java.util.List;

/**
 * @author hanqf
 * @date 2020/3/13 14:56
 */

public class Role {
    //主键，自增
    private Long id;

    private String name;

    private List<User> users;

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", users=" + users +
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
}
