package com.example.dao.two;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.model.two.SysUser;

import java.util.List;

/**
 * <h1></h1>
 * Created by hanqf on 2023/8/17 10:56.
 */


public interface SysUserDao extends BaseMapper<SysUser> {
    SysUser selectByPrimaryKey(String id);

    List<SysUser> selectAll();
}
