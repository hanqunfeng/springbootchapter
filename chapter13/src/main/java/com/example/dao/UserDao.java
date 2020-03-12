package com.example.dao;


import com.example.model.User;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.FetchType;

@Mapper
public interface UserDao extends BaseMapper<User>{
    //继承的方法不够用，也可以自定义方法，规则与mybatis一致
    //one to many
    @Select("select * from user where id = #{userId}")
    @Results({
            @Result(id=true,column="id",property="id"),
            @Result(column="name",property="name"),
            @Result(column="age",property="age"),
            @Result(column="email",property="email"),
            @Result(column="del",property="del"),
            //这里column="id" 是user表的主键,可以理解为当前表发送给getAddressByUserId方法的参数
            @Result(column="id",property="userAddress",one=@One(select="com.example.dao.AddressDao.getAddressByUserId",fetchType= FetchType.EAGER)),
            //这里column="id" 是user表的主键,可以理解为当前表发送给getBooksByUserId方法的参数
            @Result(column="id",property="books",many=@Many(select="com.example.dao.BookDao.getBooksByUserId",fetchType= FetchType.LAZY))
    })
    public User getUserAddressAndBooksById(Long userId);
}