package org.example;

import org.example.config.AppConfig;
import org.example.model.User;
import org.example.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;


/**
 * Hello world!
 */
public class App {

    private static final Logger logger = LoggerFactory.getLogger(App.class);


    public static void main(String[] args) throws SQLException {

        //获取注解对象，多个配置类可以逗号分隔
        ApplicationContext ctx
                = new AnnotationConfigApplicationContext(AppConfig.class);

        //DataSource dataSource = ctx.getBean(DataSource.class);
        //System.out.println(dataSource);
        //Connection connection = dataSource.getConnection();
        //connection.close();

        JdbcTemplate jdbcTemplate =ctx.getBean(JdbcTemplate.class);
        List<Map<String,Object>> list =jdbcTemplate.queryForList("select * from user where name = ?","hanqf");
        for(Map<String,Object> map : list){
            for(String key:map.keySet()){
                System.out.print(key+"=" + map.get(key)+",");
            }
            System.out.println();
        }

        UserService userService = ctx.getBean(UserService.class);
        List<User> userList = userService.getUserListByName("aaa");
        for(User user : userList){
            System.out.println(user);
        }

        System.out.println(userService.getUserByStatementCallback(39L));
        System.out.println(userService.getUserByConnectionCallback(39L));




    }
}
