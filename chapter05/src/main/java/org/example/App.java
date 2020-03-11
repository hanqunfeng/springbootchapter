package org.example;/**
 * Created by hanqf on 2020/3/2 15:16.
 */


import org.example.aspect.enhance.UserValidator;
import org.example.config.AppConfig;
import org.example.model.User;
import org.example.service.UserService;
import org.example.service.UserServiceNoInterface;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author hanqf
 * @date 2020/3/2 15:16
 */
public class App {

    public static void main(String[] args) {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);

        //JDK动态代理
        UserService userService = ctx.getBean(UserService.class);
        User user = new User();
        user.setId(100L);
        user.setName("zhangsan");
        user.setNote("note");
        userService.printUser(user);



        //增强接口测试
        UserValidator userValidator = (UserValidator)userService;
        if(userValidator.validate(user)){
            userService.printUser(user);
        }

        //异常测试
        //userService.printUser(null);

        //CGLIB动态代理
        UserServiceNoInterface userServiceNoInterface = ctx.getBean(UserServiceNoInterface.class);
        userServiceNoInterface.printUser(user);
    }
}
