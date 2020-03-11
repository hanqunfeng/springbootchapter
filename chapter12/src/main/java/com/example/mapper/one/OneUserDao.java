package com.example.mapper.one;

import com.example.model.one.OneUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OneUserDao {
    int deleteByPrimaryKey(Long id);

    int insert(OneUser record);

    int insertSelective(OneUser record);

    OneUser selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(OneUser record);

    int updateByPrimaryKey(OneUser record);

    List<OneUser> selectUsers();
}