package org.example;

import org.example.config.AppConfig;
import org.example.model.User;
import org.example.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.sql.SQLException;
import java.util.List;


/**
 * Hello world!
 */
public class App {

    private static final Logger logger = LoggerFactory.getLogger(App.class);


    public static void main(String[] args) throws SQLException {

        //获取注解对象，多个配置类可以逗号分隔
        ApplicationContext ctx
                = new AnnotationConfigApplicationContext(AppConfig.class);


        UserService userService = ctx.getBean(UserService.class);
        List<User> userList = userService.getUserListByName("bb");
        for(User user : userList){
            System.out.println(user);
        }

        User user = new User();
        user.setName("bb");
        user.setAge(20);
        user.setEmail("bb@bb.com");
        user.setDel("0");
        userService.save1And2(user);

        //分开执行，第一个没有抛异常，不会回滚
        //userService.save1(user);
        //抛异常，回滚
        //userService.save2(user);





    }
}
