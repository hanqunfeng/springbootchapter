package com.example.dao.one;

import com.example.model.one.Order;

import java.util.List;

/**
 * Created by Mybatis Generator on 2023-08-16 18:10:36
 */
public interface OrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Order row);

    int insertSelective(Order row);

    Order selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Order row);

    int updateByPrimaryKey(Order row);

    List<Order> selectAll();
}
