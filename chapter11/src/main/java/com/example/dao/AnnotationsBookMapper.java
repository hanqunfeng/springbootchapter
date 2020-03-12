package com.example.dao;

import com.example.model.Book;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.FetchType;

import java.util.List;

/**
 * Created by hanqf on 2020/3/12 11:37.
 */

@Mapper
public interface AnnotationsBookMapper
{
    @Select("select * from books where userId = #{userId}")
    @Results({
            @Result(id=true,column="id",property="id"),
            @Result(column="bookName",property="bookName"),
            @Result(column="totalPage",property="totalPage"),
            @Result(column="price",property="price"),
            @Result(column="userId",property="userId")
    })
    public List<Book> getBooksByUserId(Long userId);

    @Select("select * from books where userId = #{userId}")
    @Results({
            @Result(id=true,column="id",property="id"),
            @Result(column="bookName",property="bookName"),
            @Result(column="totalPage",property="totalPage"),
            @Result(column="price",property="price"),
            @Result(column="userId",property="userId"),
            //这里column="userId" 当前表发送给selectByPrimaryKey方法的参数， 是books表关联user表的外键，这里的查询方法要求不要返回关联对象，以免无限关联下去
            @Result(column="userId",property="user",one=@One(select="com.example.dao.AnnotationsUserDao.selectByPrimaryKey",fetchType= FetchType.EAGER))
    })
    public List<Book> getBooksAndUserByUserId(Long userId);
}
