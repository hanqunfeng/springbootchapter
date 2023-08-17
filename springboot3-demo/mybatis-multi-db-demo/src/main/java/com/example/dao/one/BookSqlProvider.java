package com.example.dao.one;

import com.example.model.one.Book;
import org.apache.ibatis.jdbc.SQL;

public class BookSqlProvider {
    public String insertSelective(Book row) {
        SQL sql = new SQL();
        sql.INSERT_INTO("books");

        if (row.getId() != null) {
            sql.VALUES("id", "#{id,jdbcType=BIGINT}");
        }

        if (row.getBookname() != null) {
            sql.VALUES("bookName", "#{bookname,jdbcType=VARCHAR}");
        }

        if (row.getPrice() != null) {
            sql.VALUES("price", "#{price,jdbcType=DOUBLE}");
        }

        if (row.getTotalpage() != null) {
            sql.VALUES("totalPage", "#{totalpage,jdbcType=INTEGER}");
        }

        if (row.getUserid() != null) {
            sql.VALUES("userId", "#{userid,jdbcType=BIGINT}");
        }

        return sql.toString();
    }

    public String updateByPrimaryKeySelective(Book row) {
        SQL sql = new SQL();
        sql.UPDATE("books");

        if (row.getBookname() != null) {
            sql.SET("bookName = #{bookname,jdbcType=VARCHAR}");
        }

        if (row.getPrice() != null) {
            sql.SET("price = #{price,jdbcType=DOUBLE}");
        }

        if (row.getTotalpage() != null) {
            sql.SET("totalPage = #{totalpage,jdbcType=INTEGER}");
        }

        if (row.getUserid() != null) {
            sql.SET("userId = #{userid,jdbcType=BIGINT}");
        }

        sql.WHERE("id = #{id,jdbcType=BIGINT}");

        return sql.toString();
    }
}
