package org.example.model;/**
 * Created by hanqf on 2020/3/2 11:28.
 */


/**
 * 同时声明两个相同类型的bean，并都配置为@Primary
 * org.springframework.beans.factory.NoUniqueBeanDefinitionException
 * No qualifying bean of type 'org.example.model.Animal' available: more than one 'primary' bean found among candidates: [cat, dog]
 *
 * @author hanqf
 * @date 2020/3/2 11:28
 */
public class Dog implements Animal {
    @Override
    public void use() {
        System.out.println("狗："+Dog.class.getSimpleName());
    }
}
