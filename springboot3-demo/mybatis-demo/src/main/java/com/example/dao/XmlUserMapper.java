package com.example.dao;

import com.example.model.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface XmlUserMapper {
    int deleteByPrimaryKey(Long id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    List<User> selectUsers();

    public User getUserById(Long id);

    public User getUserAndBooksById(Long id);
    //
    public User getUserAddressAndBooksById(Long id);

    public User getUserRolesById(Long id);

    public List<User> getUserListByRoleId(Long roleId);


}
