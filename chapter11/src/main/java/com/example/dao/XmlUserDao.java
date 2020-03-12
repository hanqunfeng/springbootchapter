package com.example.dao;

import com.example.model.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface XmlUserDao {
    int deleteByPrimaryKey(Long id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    List<User> selectUsers();

    public User getUserById(Long userId);

    public User getUserAndBooksById(Long userId);
    //
    public User getUserAddressAndBooksById(Long userId);


}