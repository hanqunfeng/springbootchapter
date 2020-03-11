package org.example.service;/**
 * Created by hanqf on 2020/3/5 16:49.
 */


import org.example.model.User;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author hanqf
 * @date 2020/3/5 16:49
 */
@Service
@Transactional(transactionManager = "transactionManager",propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = { Exception.class })
public class UserServiceImpl implements UserService {
    @Resource(name = "oneJdbcTemplate")
    private JdbcTemplate oneJdbcTemplate;

    @Resource(name = "twoJdbcTemplate")
    private JdbcTemplate twoJdbcTemplate;

    @Override
    public List<User> getUserListByName(String name) {

        //要求bean的属性与sql属性一致，不一致时sql字段用as
        List<User> onelist =  oneJdbcTemplate.query("select * from user where name = ?", new BeanPropertyRowMapper(User.class),name);
        List<User> twolist =  twoJdbcTemplate.query("select * from user where name = ?", new BeanPropertyRowMapper(User.class),name);
        onelist.addAll(twolist);
        return onelist;
    }

    //会触发回滚
    @Override
    public void save1And2(User user) {
        //String sql = "insert into user(name,age,email,del) values(?,?,?,?)";
        //oneJdbcTemplate.update(sql,user.getName(),user.getAge(),user.getEmail(),user.getDel());
        //twoJdbcTemplate.update(sql,user.getName(),user.getAge(),user.getEmail(),user.getDel());
        //throw new RuntimeException("我是一个异常");
        save1(user);
        save2(user);
    }

    @Override
    public void save1(User user) {
        String sql = "insert into user(name,age,email,del) values(?,?,?,?)";
        oneJdbcTemplate.update(sql,user.getName(),user.getAge(),user.getEmail(),user.getDel());
    }

    @Override
    public void save2(User user) {
        String sql = "insert into user(name,age,email,del) values(?,?,?,?)";
        twoJdbcTemplate.update(sql,user.getName(),user.getAge(),user.getEmail(),user.getDel());
        throw new RuntimeException("我是一个异常");
    }


}
