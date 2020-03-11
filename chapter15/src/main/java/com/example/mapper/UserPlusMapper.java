package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.model.UserPlus;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by hanqf on 2020/2/28 14:15.
 */


public interface UserPlusMapper extends BaseMapper<UserPlus> {


    //自定义其它方法
    @Select("select * from user where name = #{name}")
    List<UserPlus> getUserPlusByName(String name);

    //自定义带条件的方法
    @Select("<script>"
            +"select * from user where 1=1"
            +"<if test='a != null'>"
            +"and age = #{a}"
            +"</if>"
            +"</script>")
    List<UserPlus> getUserPlusByAge(@Param("a") Integer age);
}
