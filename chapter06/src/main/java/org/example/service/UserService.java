package org.example.service;

import org.example.model.User;

import java.util.List;

/**
 * Created by hanqf on 2020/3/5 16:47.
 */


public interface UserService {

    public List<User> getUserListByName(String name);
    public User getUserByStatementCallback(Long id);
    public User getUserByConnectionCallback(Long id);
}
