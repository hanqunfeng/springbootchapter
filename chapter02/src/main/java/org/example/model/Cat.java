package org.example.model;/**
 * Created by hanqf on 2020/3/2 11:27.
 */


import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * 同时声明两个相同类型的bean，并都配置为@Primary
 * org.springframework.beans.factory.NoUniqueBeanDefinitionException
 * No qualifying bean of type 'org.example.model.Animal' available: more than one 'primary' bean found among candidates: [cat, dog]
 *
 * @author hanqf
 * @date 2020/3/2 11:27
 */
@Component
@Primary
public class Cat implements Animal {
    @Override
    public void use() {
        System.out.println("猫："+Cat.class.getSimpleName());
    }
}
