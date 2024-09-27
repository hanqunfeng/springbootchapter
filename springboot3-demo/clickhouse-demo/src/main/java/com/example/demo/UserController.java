package com.example.demo;

/**
 * <h1></h1>
 * Created by hanqf on 2024/9/23 14:36.
 */


import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserRepository userRepository;

    @GetMapping("/save/{id}")
    public String save(@PathVariable Long id) {
        boolean isExist = userRepository.findById(id).isPresent();
        if (isExist) {
            userRepository.updateUser(userRepository.findById(id).get().setCreateTime(LocalDateTime.now()));
        } else {
            User user = new User().setId(id).setUsername("admin").setAddr("China1").setCreateTime(LocalDateTime.now());
            userRepository.save(user);
        }
        return "save success";
    }

    @GetMapping("/list")
    public List<User> list() {
        return userRepository.findAll();
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id) {
        userRepository.delUser(id);
        return "delete success";
    }
}

