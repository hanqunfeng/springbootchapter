package org.example;

import org.example.config.AppConfig;
import org.example.config.OtherConfig;
import org.example.model.OtherBean;
import org.example.model.User1;
import org.example.model.User2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


/**
 * Hello world!
 */
public class App {

    private static final Logger logger = LoggerFactory.getLogger(App.class);


    public static void main(String[] args) {


        //获取注解对象，多个配置类可以逗号分隔
        ApplicationContext ctx
                = new AnnotationConfigApplicationContext(AppConfig.class, OtherConfig.class);
        User1 user1 = ctx.getBean(User1.class);
        logger.info(user1.toString());
        User1 user11 = ctx.getBean(User1.class);
        //单例模式返回true
        System.out.println(user1 == user11);

        User2 user2 = (User2) ctx.getBean("user2");
        logger.info(user2.toString());

        User2 user22 = (User2) ctx.getBean("user2");
        //非单例模式返回false
        System.out.println(user2 == user22);

        OtherBean otherBean = ctx.getBean(OtherBean.class);
        System.out.println(otherBean);


    }
}
