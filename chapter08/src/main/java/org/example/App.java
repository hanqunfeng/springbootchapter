package org.example;

import org.example.config.AppConfig;
import org.example.dao.JpaUserRepository;
import org.example.model.User;
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

        JpaUserRepository jpaUserRepository = ctx.getBean(JpaUserRepository.class);
        List<User> list = jpaUserRepository.findAll();
        for (User user : list){
            System.out.println(user);
        }
    }
}
