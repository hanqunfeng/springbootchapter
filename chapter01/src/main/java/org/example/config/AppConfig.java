package org.example.config;/**
 * Created by hanqf on 2020/3/1 00:37.
 */


import org.example.model.User2;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.*;

/**
 * @author hanqf
 * @date 2020/3/1 00:37
 */
@Configuration
@ComponentScan("org.example")
public class AppConfig {

    @Bean(name = "user2")
    //bean的作用域，这里ConfigurableBeanFactory只提供了Singleton和Prototype两种选择
    //如果是web项目，则可以使用WebApplicationContext提供的选项
    //这里使用SCOPE_PROTOTYPE，表示每次获取对象都新创建一个
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public User2 initUser2() {
        User2 user2 = new User2();
        user2.setId(2L);
        user2.setUserName("user_name_2");
        user2.setNote("note_2");
        return user2;
    }
}
