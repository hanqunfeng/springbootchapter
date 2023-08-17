package com.example.dao.one;

import com.example.model.one.User;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.FetchType;
import org.apache.ibatis.type.JdbcType;

/**
 * Created by Mybatis Generator on 2023-08-16 18:13:56
 */
public interface UserMapper {
    @Delete({
        "delete from user",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
        "insert into user (id, name, ",
        "age, email, del)",
        "values (#{id,jdbcType=BIGINT}, #{name,jdbcType=VARCHAR}, ",
        "#{age,jdbcType=INTEGER}, #{email,jdbcType=VARCHAR}, #{del,jdbcType=VARCHAR})"
    })
    int insert(User row);

    @InsertProvider(type=UserSqlProvider.class, method="insertSelective")
    int insertSelective(User row);

    @Select({
        "select",
        "id, name, age, email, del",
        "from user",
        "where id = #{id,jdbcType=BIGINT}"
    })
    @Results({
        @Result(column="id", property="id", jdbcType=JdbcType.BIGINT, id=true),
        @Result(column="name", property="name", jdbcType=JdbcType.VARCHAR),
        @Result(column="age", property="age", jdbcType=JdbcType.INTEGER),
        @Result(column="email", property="email", jdbcType=JdbcType.VARCHAR),
        @Result(column="del", property="del", jdbcType=JdbcType.VARCHAR),
        //这里column="id" 是user表的主键
        @Result(column="id",property="books",many=@Many(select="com.example.dao.one.BookMapper.getBooksByUserId",fetchType= FetchType.LAZY))

    })
    User selectByPrimaryKey(Long id);

    @UpdateProvider(type=UserSqlProvider.class, method="updateByPrimaryKeySelective")
    int updateByPrimaryKeySelective(User row);

    @Update({
        "update user",
        "set name = #{name,jdbcType=VARCHAR},",
          "age = #{age,jdbcType=INTEGER},",
          "email = #{email,jdbcType=VARCHAR},",
          "del = #{del,jdbcType=VARCHAR}",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(User row);
}
