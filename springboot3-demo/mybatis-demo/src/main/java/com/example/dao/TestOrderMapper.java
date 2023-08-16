package com.example.dao;

import com.example.model.TestOrder;

public interface TestOrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(TestOrder record);

    int insertSelective(TestOrder record);

    TestOrder selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TestOrder record);

    int updateByPrimaryKey(TestOrder record);

}
