package com.example.dao.one;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.model.one.User;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.FetchType;
import org.apache.ibatis.type.JdbcType;

import java.util.List;

/**
 * Created by hanqf on 2020/2/28 14:15.
 */

public interface UserMapper extends BaseMapper<User> {


    //自定义其它方法
    @Select("select * from user where name = #{name}")
    List<User> getUserPlusByName(String name);

    //自定义带条件的方法
    @Select("<script>"
            +"select * from user where 1=1"
            +"<if test='a != null'>"
            +"and age = #{a}"
            +"</if>"
            +"</script>")
    List<User> getUserPlusByAge(@Param("a") Integer age);

    @Select({
            "select",
            "id, name, age, email, del",
            "from user",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @Results({
            @Result(column="id", property="id", jdbcType= JdbcType.BIGINT, id=true),
            @Result(column="name", property="name", jdbcType=JdbcType.VARCHAR),
            @Result(column="age", property="age", jdbcType=JdbcType.INTEGER),
            @Result(column="email", property="email", jdbcType=JdbcType.VARCHAR),
            @Result(column="del", property="delStatus", jdbcType=JdbcType.VARCHAR),
            //这里column="id" 是user表的主键
            @Result(column="id",property="books",many=@Many(select="com.example.dao.one.BooksDao.getBooksByUserId",fetchType= FetchType.LAZY))

    })
    User selectByIdAndBooks(Long id);
}
