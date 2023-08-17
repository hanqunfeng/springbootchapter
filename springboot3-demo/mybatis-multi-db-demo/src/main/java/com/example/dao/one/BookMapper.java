package com.example.dao.one;

import com.example.model.one.Book;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;

import java.util.List;

/**
 * Created by Mybatis Generator on 2023-08-16 18:13:45
 */
public interface BookMapper {
    @Delete({
        "delete from books",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
        "insert into books (id, bookName, ",
        "price, totalPage, ",
        "userId)",
        "values (#{id,jdbcType=BIGINT}, #{bookname,jdbcType=VARCHAR}, ",
        "#{price,jdbcType=DOUBLE}, #{totalpage,jdbcType=INTEGER}, ",
        "#{userid,jdbcType=BIGINT})"
    })
    int insert(Book row);

    @InsertProvider(type=BookSqlProvider.class, method="insertSelective")
    int insertSelective(Book row);

    @Select({
        "select",
        "id, bookName, price, totalPage, userId",
        "from books",
        "where id = #{id,jdbcType=BIGINT}"
    })
    @Results({
        @Result(column="id", property="id", jdbcType=JdbcType.BIGINT, id=true),
        @Result(column="bookName", property="bookname", jdbcType=JdbcType.VARCHAR),
        @Result(column="price", property="price", jdbcType=JdbcType.DOUBLE),
        @Result(column="totalPage", property="totalpage", jdbcType=JdbcType.INTEGER),
        @Result(column="userId", property="userid", jdbcType=JdbcType.BIGINT)
    })
    Book selectByPrimaryKey(Long id);

    @UpdateProvider(type=BookSqlProvider.class, method="updateByPrimaryKeySelective")
    int updateByPrimaryKeySelective(Book row);

    @Update({
        "update books",
        "set bookName = #{bookname,jdbcType=VARCHAR},",
          "price = #{price,jdbcType=DOUBLE},",
          "totalPage = #{totalpage,jdbcType=INTEGER},",
          "userId = #{userid,jdbcType=BIGINT}",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(Book row);


    @Select("select * from books where userId = #{userId}")
    @Results({
            @Result(id=true,column="id",property="id"),
            @Result(column="bookName",property="bookname"),
            @Result(column="totalPage",property="totalpage"),
            @Result(column="price",property="price"),
            @Result(column="userId",property="userid")
    })
    List<Book> getBooksByUserId(Long userId);
}
