package org.example.config;/**
 * Created by hanqf on 2020/3/2 15:15.
 */


import org.example.model.*;
import org.example.util.PropertiesCondition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;

/**
 * @author hanqf
 * @date 2020/3/2 15:15
 */
@Configuration
@ComponentScan("org.example")
//配置属性文件，这里不是springboot项目，所以必须配置，否则找不到
//若spring.profiles.active=dev有配置，但是没有这个配置文件（例：application-dev.properties），
// 则默认读取application.properties。若spring.profiles.active无配置，则默认读取application.properties。
// 这里一定要先配置classpath:application.properties，因为这样才能初始化${spring.profiles.active}属性,
//另外这样配置两个配置文件，可以只在不同的环境配置配置文件中增加变化的内容，不变的内容还是放在默认的配置文件中
@PropertySource(name = "application",value = {"classpath:application.properties",
        "classpath:application-${spring.profiles.active}.properties"})
//开启spring-boot对配置依赖的支持，如@ConfigurationProperties
@EnableConfigurationProperties
public class AppConfig {

    @Bean
    //PropertiesCondition.matches方法返回true则会在spring上下文创建如下对象
    @Conditional(PropertiesCondition.class)
    public ConditionProperties getConditionProperties(@Value("${first.value}") String first, @Value("${second.value}") String second) {
        ConditionProperties conditionProperties = new ConditionProperties();
        conditionProperties.setFirst(first);
        conditionProperties.setSecond(second);
        return conditionProperties;
    }


    @Bean
    public ProfilePreperties getProfilePreperties() {
        return new ProfilePreperties();
    }

    @Bean
    @Profile("dev") //@Profile也可以放在类的里面，加载不同环境对应的bean，这里要注意：1.属性文件可以基于${spring.profiles.active}的值自动赋值，2.如果${spring.profiles.active}的值不存在，则无法初始化一个可以用的对象
    public Animal getDevAnimal() {
        return new Dog();
    }

    @Bean
    @Profile("rel")
    public Animal getRelAnimal() {
        return new Cat();
    }
}
