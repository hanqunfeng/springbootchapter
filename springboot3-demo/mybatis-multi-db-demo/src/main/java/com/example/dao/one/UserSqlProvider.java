package com.example.dao.one;

import com.example.model.one.User;
import org.apache.ibatis.jdbc.SQL;

public class UserSqlProvider {
    public String insertSelective(User row) {
        SQL sql = new SQL();
        sql.INSERT_INTO("user");
        
        if (row.getId() != null) {
            sql.VALUES("id", "#{id,jdbcType=BIGINT}");
        }
        
        if (row.getName() != null) {
            sql.VALUES("name", "#{name,jdbcType=VARCHAR}");
        }
        
        if (row.getAge() != null) {
            sql.VALUES("age", "#{age,jdbcType=INTEGER}");
        }
        
        if (row.getEmail() != null) {
            sql.VALUES("email", "#{email,jdbcType=VARCHAR}");
        }
        
        if (row.getDel() != null) {
            sql.VALUES("del", "#{del,jdbcType=VARCHAR}");
        }
        
        return sql.toString();
    }

    public String updateByPrimaryKeySelective(User row) {
        SQL sql = new SQL();
        sql.UPDATE("user");
        
        if (row.getName() != null) {
            sql.SET("name = #{name,jdbcType=VARCHAR}");
        }
        
        if (row.getAge() != null) {
            sql.SET("age = #{age,jdbcType=INTEGER}");
        }
        
        if (row.getEmail() != null) {
            sql.SET("email = #{email,jdbcType=VARCHAR}");
        }
        
        if (row.getDel() != null) {
            sql.SET("del = #{del,jdbcType=VARCHAR}");
        }
        
        sql.WHERE("id = #{id,jdbcType=BIGINT}");
        
        return sql.toString();
    }
}