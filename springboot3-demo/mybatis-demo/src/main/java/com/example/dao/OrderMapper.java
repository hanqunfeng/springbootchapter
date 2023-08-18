package com.example.dao;

import com.example.model.Order;

/**
 * Created by Mybatis Generator on 2023-08-18 12:29:15
 */
public interface OrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Order row);

    int insertSelective(Order row);

    Order selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Order row);

    int updateByPrimaryKey(Order row);
}