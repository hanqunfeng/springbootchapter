package org.example.model;/**
 * Created by hanqf on 2020/3/2 12:06.
 */


import org.springframework.beans.BeansException;
import org.springframework.beans.factory.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * class必须实现对应的spring接口，才能被spring管理生命周期
 * @author hanqf
 * @date 2020/3/2 12:06
 */
@Component
public class LifeCycle implements Person, BeanNameAware, BeanFactoryAware, ApplicationContextAware, InitializingBean, DisposableBean {

    //@Autowired
    //@Qualifier("cat")
    private Animal animal;

    public LifeCycle(@Autowired @Qualifier("dog") Animal animal) {
        System.out.println("0:LifeCycle Constructor");
        this.animal = animal;
    }

    @Override
    public void service() {
        this.animal.use();
    }

    @Override
    //@Autowired @Qualifier("dog")
    public void setAnimal(Animal animal) {
        System.out.println("0:LifeCycle setAnimal");  //这里采用的是构造函数对象的初始化
        this.animal = animal;
    }

    @Override
    public void setBeanName(String s) {
        System.out.println("1:" + this.getClass().getSimpleName() + " BeanNameAware");
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        System.out.println("2:" + this.getClass().getSimpleName() + " BeanFactoryAware");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        System.out.println("3:" + this.getClass().getSimpleName() + " ApplicationContextAware");
    }


    @PostConstruct
    public void init() {
        System.out.println("5:" + this.getClass().getSimpleName() + " @PostConstruct");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("6:" + this.getClass().getSimpleName() + " InitializingBean");
    }

    @PreDestroy
    public void destroy1() {
        System.out.println("8:" + this.getClass().getSimpleName() + " @PreDestroy");
    }

    @Override
    public void destroy() throws Exception {
        System.out.println("9:" + this.getClass().getSimpleName() + " DisposableBean");
    }


}
