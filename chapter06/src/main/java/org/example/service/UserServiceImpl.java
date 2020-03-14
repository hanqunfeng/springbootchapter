package org.example.service;/**
 * Created by hanqf on 2020/3/5 16:49.
 */


import org.example.model.User;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.StatementCallback;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

/**
 * @author hanqf
 * @date 2020/3/5 16:49
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = { Exception.class })
public class UserServiceImpl implements UserService {
    @Resource(name = "jdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    @Transactional(readOnly = true)
    @Override
    public List<User> getUserListByName(String name) {

        //要求bean的属性与sql属性一致，不一致时sql字段用as
        return jdbcTemplate.query("select * from user where name = ?", new BeanPropertyRowMapper(User.class), name);

        //效果同上
        //return jdbcTemplate.query("select * from user where name = ?", new RowMapper<User>() {
        //    @Override
        //    public User mapRow(ResultSet resultSet, int i) throws SQLException {
        //        User user = new User();
        //        user.setId(resultSet.getLong("id"));
        //        user.setName(resultSet.getString("name"));
        //        user.setAge(resultSet.getInt("age"));
        //        user.setEmail(resultSet.getString("email"));
        //        user.setDel(resultSet.getString("del"));
        //        return user;
        //    }
        //}, name);
    }

    @Override
    public User getUserByStatementCallback(Long id) {
        User user = jdbcTemplate.execute((StatementCallback<User>) statement -> {


            String sql1 = "select count(*) total from user where id=" + id;
            ResultSet rsl = statement.executeQuery(sql1);
            while (rsl.next()) {
                int total = rsl.getInt("total");
                System.out.println(total);
            }

            //执行的 SQL
            String sql2 = "select id,name,age,email,del from user  where id = " + id;
            ResultSet rs2 = statement.executeQuery(sql2);
            User user1 = null;
            while (rs2.next()) {
                int rowNum = rs2.getRow();
                user1 = new BeanPropertyRowMapper<User>(User.class).mapRow(rs2, rowNum);
            }
            return user1;
        });
        return user;
    }

    @Override
    public User getUserByConnectionCallback(Long id) {

        User user = jdbcTemplate.execute((ConnectionCallback<User>) connection -> {


            String sql1 = "select count(*) total from user where id= ?";
            PreparedStatement ps1 = connection.prepareStatement(sql1);
            ps1.setLong(1, id);
            ResultSet rsl = ps1.executeQuery();
            while (rsl.next()) {
                int total = rsl.getInt("total");
                System.out.println(total);
            }

            //执行的 SQL
            String sql2 = "select id,name,age,email,del from user  where id = ?";
            PreparedStatement ps2 = connection.prepareStatement(sql2);
            ps2.setLong(1, id);
            ResultSet rs2 = ps2.executeQuery();
            User user1 = null;
            while (rs2.next()) {
                int rowNum = rs2.getRow();
                user1 = new BeanPropertyRowMapper<User>(User.class).mapRow(rs2, rowNum);
            }
            return user1;
        });
        return user;
    }
}
