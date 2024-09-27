package com.example.dao;

import com.example.model.User;

import java.util.List;

/**
 * Created by Mybatis Generator on 2024-09-26 17:36:13
 */
public interface UserMapper {
    int insert(User user);

    int insertSelective(User user);

    User selectById(Long id);

    List<User> selectList () ;

    int update(User user);

}
