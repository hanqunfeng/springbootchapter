package org.example.service;

import org.example.model.User;

import java.util.List;

/**
 * Created by hanqf on 2020/3/5 16:47.
 */


public interface UserService {

    public List<User> getUserListByName(String name);

    public void save1And2(User user);
    public void save1(User user);
    public void save2(User user);
}
