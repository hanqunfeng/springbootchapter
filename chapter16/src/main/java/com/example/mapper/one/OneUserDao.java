package com.example.mapper.one;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.model.one.OneUser;
import org.apache.ibatis.annotations.Mapper;

//@Component
@Mapper
public interface OneUserDao extends BaseMapper<OneUser> {

}