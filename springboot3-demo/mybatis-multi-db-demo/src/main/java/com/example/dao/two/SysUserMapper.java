package com.example.dao.two;

import com.example.model.two.SysUser;

import java.util.List;

/**
 * Created by Mybatis Generator on 2023-08-16 17:49:29
 */
public interface SysUserMapper {
    int deleteByPrimaryKey(String id);

    int insert(SysUser row);

    int insertSelective(SysUser row);

    SysUser selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(SysUser row);

    int updateByPrimaryKey(SysUser row);

    List<SysUser> selectAll();

}
