package com.example.mapper.one;

import com.example.mapper.BaseMapper;
import com.example.model.one.OneUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OneUserDao extends BaseMapper<OneUser> {

}