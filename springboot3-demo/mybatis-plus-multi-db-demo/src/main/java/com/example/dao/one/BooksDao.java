package com.example.dao.one;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.model.one.BooksEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author hanqf
 * @since 2023-08-17 12:39:30
 */
@Mapper
public interface BooksDao extends BaseMapper<BooksEntity> {

    @Select("select * from books where userId = #{userId}")
    @Results({
            @Result(id=true,column="id",property="id"),
            @Result(column="bookName",property="bookName"),
            @Result(column="totalPage",property="totalPage"),
            @Result(column="price",property="price"),
            @Result(column="userId",property="userId")
    })
    List<BooksEntity> getBooksByUserId(Long userId);
}
