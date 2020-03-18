package com.example.service;

import com.example.model.User;

import java.util.List;

/**
 * Created by hanqf on 2020/3/17 16:34.
 */


public interface UserService {
    List<User> getAllUser();
    User Save(User user);
}
