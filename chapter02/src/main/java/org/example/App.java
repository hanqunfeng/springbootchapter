package org.example;

import org.example.config.AppConfig;
import org.example.model.Animal;
import org.example.model.BussinessPerson;
import org.example.model.Dog;
import org.example.model.Person;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {

        ApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);
        //此时会报错，因为声明了多个相同类型的@Primary，此时可以采用如下方法：
        //1.只保留一个@Primary
        //2.不要根据类型获取对象，而是根据名称,ctx.getBean("dog")
        //Animal animal = ctx.getBean(Animal.class);
        Animal animal = (Dog) ctx.getBean("dog");
        System.out.println(animal);


        //可以在引入类中使用@Qualifier("dog")来指定要加载的对象
        Person person = ctx.getBean(BussinessPerson.class);
        person.service();
    }
}
