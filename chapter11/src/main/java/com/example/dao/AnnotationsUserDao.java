package com.example.dao;


import com.example.model.User;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.FetchType;
import org.apache.ibatis.type.JdbcType;

import java.util.List;

@Mapper
public interface AnnotationsUserDao {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user
     *
     * @mbg.generated
     */
    @Delete({
            "delete from user",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user
     *
     * @mbg.generated
     */
    @Insert({
            "insert into user (id, name, ",
            "age, email, del)",
            "values (#{id,jdbcType=BIGINT}, #{name,jdbcType=VARCHAR}, ",
            "#{age,jdbcType=INTEGER}, #{email,jdbcType=VARCHAR}, #{del,jdbcType=VARCHAR})"
    })
    int insert(User record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user
     *
     * @mbg.generated
     */
    @InsertProvider(type=UserSqlProvider.class, method="insertSelective")
    int insertSelective(User record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user
     *
     * @mbg.generated
     */
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
            @Result(column="del", property="del", jdbcType=JdbcType.VARCHAR)
    })
    User selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user
     *
     * @mbg.generated
     */
    @UpdateProvider(type=UserSqlProvider.class, method="updateByPrimaryKeySelective")
    int updateByPrimaryKeySelective(User record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user
     *
     * @mbg.generated
     */
    @Update({
            "update user",
            "set name = #{name,jdbcType=VARCHAR},",
            "age = #{age,jdbcType=INTEGER},",
            "email = #{email,jdbcType=VARCHAR},",
            "del = #{del,jdbcType=VARCHAR}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(User record);

    @Select({
            "select",
            "id, name, age, email, del",
            "from user"
    })
    @Results({
            @Result(column="id", property="id", jdbcType= JdbcType.BIGINT, id=true),
            @Result(column="name", property="name", jdbcType=JdbcType.VARCHAR),
            @Result(column="age", property="age", jdbcType=JdbcType.INTEGER),
            @Result(column="email", property="email", jdbcType=JdbcType.VARCHAR),
            @Result(column="del", property="del", jdbcType=JdbcType.VARCHAR)
    })
    List<User> selectUsers();


    //one to one
    @Select("select * from user where id = #{userId}")
    @Results({
            @Result(id=true,column="id",property="id"),
            @Result(column="name",property="name"),
            @Result(column="age",property="age"),
            @Result(column="email",property="email"),
            @Result(column="del",property="del"),
            //这里column="id" 是user表的主键
            @Result(column="id",property="userAddress",one=@One(select="com.example.dao.AnnotationsAddressMapper.getAddressByUserId",fetchType= FetchType.EAGER))
    })
    public User getUserById(Long userId);

    //one to many
    @Select("select * from user where id = #{userId}")
    @Results({
            @Result(id=true,column="id",property="id"),
            @Result(column="name",property="name"),
            @Result(column="age",property="age"),
            @Result(column="email",property="email"),
            @Result(column="del",property="del"),
            //这里column="id" 是user表的主键
            @Result(column="id",property="books",many=@Many(select="com.example.dao.AnnotationsBookMapper.getBooksByUserId",fetchType= FetchType.LAZY))
    })
    public User getUserAndBooksById(Long userId);


    //one to many
    @Select("select * from user where id = #{userId}")
    @Results({
            @Result(id=true,column="id",property="id"),
            @Result(column="name",property="name"),
            @Result(column="age",property="age"),
            @Result(column="email",property="email"),
            @Result(column="del",property="del"),
            //这里column="id" 是user表的主键,可以理解为当前表发送给getAddressByUserId方法的参数
            @Result(column="id",property="userAddress",one=@One(select="com.example.dao.AnnotationsAddressMapper.getAddressByUserId",fetchType= FetchType.EAGER)),
            //这里column="id" 是user表的主键,可以理解为当前表发送给getBooksByUserId方法的参数
            @Result(column="id",property="books",many=@Many(select="com.example.dao.AnnotationsBookMapper.getBooksByUserId",fetchType= FetchType.LAZY))
    })
    public User getUserAddressAndBooksById(Long userId);

}