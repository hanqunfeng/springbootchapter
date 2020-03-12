package com.example.dao;/**
 * Created by hanqf on 2020/3/12 11:36.
 */


import com.example.model.Address;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.FetchType;

/**
 * @author hanqf
 * @date 2020/3/12 11:36
 */
@Mapper
public interface AnnotationsAddressMapper
{
    @Select("select * from address where userId=#{userId}")
    //如果实体对象字段与表字段一致，可以不用加@Results
    @Results({
            @Result(id=true,column="id",property="id"),
            @Result(column="province",property="province"),
            @Result(column="city",property="city"),
            @Result(column="userId",property="userId")
    })
    public Address getAddressByUserId(Long userId);

    @Select("select * from address where userId=#{userId}")
    @Results({
            @Result(id=true,column="id",property="id"),
            @Result(column="province",property="province"),
            @Result(column="city",property="city"),
            @Result(column="userId",property="userId"),
            //这里column="userId" 当前表发送给是selectByPrimaryKey的参数，address表关联user表的外键，这里的查询方法要求不要返回关联对象，以免无限关联下去
            @Result(column="userId",property="user",one=@One(select="com.example.dao.AnnotationsUserDao.selectByPrimaryKey",fetchType= FetchType.EAGER))
    })
    public Address getAddressAndUserByUserId(Long userId);
}
