package org.example;/**
 * Created by hanqf on 2020/3/2 15:16.
 */


import org.example.config.AppConfig;
import org.example.model.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author hanqf
 * @date 2020/3/2 15:16
 */
public class App {

    public static void main(String[] args) {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);

        ReadProperties readProperties = ctx.getBean(ReadProperties.class);
        System.out.println(readProperties);

        ConfigProperties configProperties = ctx.getBean(ConfigProperties.class);
        System.out.println(configProperties);

        ConditionProperties conditionProperties = ctx.getBean(ConditionProperties.class);
        System.out.println(conditionProperties);

        ProfilePreperties profilePreperties = ctx.getBean(ProfilePreperties.class);
        System.out.println(profilePreperties);

        Animal animal = ctx.getBean(Animal.class);
        System.out.println(animal);

        ELProperties elProperties = ctx.getBean(ELProperties.class);
        System.out.println(elProperties);
    }
}
