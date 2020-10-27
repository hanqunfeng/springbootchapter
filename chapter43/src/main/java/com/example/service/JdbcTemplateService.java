package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <h1>sql查询通用service</h1>
 * Created by hanqf on 2020/10/27 18:11.
 */


@Service
public class JdbcTemplateService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private NamedParameterJdbcTemplate nameJdbcTemplate;

    /**
     * 防止SQL注入（适用于参数占位符为 ? 的参数语句，如果参数为命名绑定，则使用Map设置参数）
     * @param sql     sql语句
     * @param params  按 ? 顺序添加参数
     * @param clazz   返回List包含对象class
     */
    public <T> List<T> queryList(String sql, @Nullable Object[] params, Class<T> clazz){
        return jdbcTemplate.queryForList(sql, params, clazz);
    }

    /**
     * 防止SQL注入，适用于参数为命名绑定形式(如 vmCode = :vmCode) （推荐）
     * @param sql       sql语句
     * @param paramMap  参数Map，key为绑定的命名参数（:后面的名称,如上为vmCode）
     * @param clazz     返回List包含对象class
     */
    public <T> List<T> queryList(String sql, Map<String, Object> paramMap, Class<T> clazz){
        if(paramMap == null){
            paramMap = new HashMap<>();
        }
        return nameJdbcTemplate.query(sql, paramMap, new BeanPropertyRowMapper<>(clazz));
    }

    /**
     * 查询单个对象
     * @param sql    sql语句
     * @param clazz  对象class
     */
    public <T> T queryOne(String sql, Class<T> clazz){
        return jdbcTemplate.queryForObject(sql, clazz);
    }

    /**
     * @param sql    sql语句
     * @return       List列表中是一个个返回对象，Map的key为字段名，value为对应字段值
     */
    public List<Map<String, Object>> queryList(String sql){
        return jdbcTemplate.queryForList(sql);
    }

    /**
     * 查询指定返回对象类型列表(不能防止SQL注入)
     * @param sql    sql语句
     * @param clazz  返回List包含对象class
     */
    public <T> List<T> queryList(String sql, Class<T> clazz){
        return jdbcTemplate.queryForList(sql, clazz);
    }
}



