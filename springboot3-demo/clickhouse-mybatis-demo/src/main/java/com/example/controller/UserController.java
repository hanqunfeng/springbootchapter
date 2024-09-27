package com.example.controller;

import com.example.dao.UserMapper;
import com.example.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <h1></h1>
 * Created by hanqf on 2024/9/26 17:41.
 */

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/insert/{id}")
    public String insert(@PathVariable Long id) {
        User user = new User().setId(id).setUsername("admin").setAddr("China1").setCreateTime(LocalDateTime.now());
        userMapper.insert(user);
        return "save success";
    }

    @GetMapping("/update/{id}")
    public String update(@PathVariable Long id) {
        User user = userMapper.selectById(id);
        user.setCreateTime(LocalDateTime.now());
        userMapper.update(user);

        return "update success";
    }

    @GetMapping("/get/{id}")
    public User getById(@PathVariable Long id) {
        return userMapper.selectById(id);
    }

    @GetMapping("/getList")
    public List<User> getList() {
        return userMapper.selectList();
    }

}
