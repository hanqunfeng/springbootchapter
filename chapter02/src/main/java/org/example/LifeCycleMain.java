package org.example;/**
 * Created by hanqf on 2020/3/2 12:22.
 */


import org.example.config.AppConfig;
import org.example.model.BeanFactoryModelDemo;
import org.example.model.FactoryBeanModelDemo;
import org.example.registerModel.A;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author hanqf
 * @date 2020/3/2 12:22
 */
public class LifeCycleMain {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);
        //LifeCycle lifeCycle = ctx.getBean(LifeCycle.class);
        //lifeCycle.service();

        BeanDefinition dog = ctx.getBeanFactory().getBeanDefinition("dog");
        System.out.println(dog.getBeanClassName());
        System.out.println(dog.getScope());
        System.out.println(dog.getPropertyValues());
        System.out.println(dog.getConstructorArgumentValues());
        System.out.println(dog.getFactoryBeanName());
        System.out.println(dog.getFactoryMethodName());

        //通过BeanFactory注册一个对象到spring上下文，这种方式注册的bean是没有BeanDefinition的
        ctx.getBeanFactory().registerSingleton("beanFactoryModelDemo",new BeanFactoryModelDemo());
        //BeanDefinition beanFactoryModelDemo = ctx.getBeanFactory().getBeanDefinition("beanFactoryModelDemo");
        //System.out.println(beanFactoryModelDemo.getBeanClassName());
        BeanFactoryModelDemo bean = ctx.getBean(BeanFactoryModelDemo.class);
        bean.demo();

        bean = (BeanFactoryModelDemo)ctx.getBean("beanFactoryModelDemo");
        bean.demo();


        //通过FactoryBean注册一个对象到spring上下文，这种方式注册的bean是没有BeanDefinition的
        FactoryBeanModelDemo factoryBeanModelDemo = ctx.getBean(FactoryBeanModelDemo.class);
        factoryBeanModelDemo.demo();

        A a = ctx.getBean(A.class);
        a.output();
        System.out.println(a.defaultPrint("sdfsdf"));
        ctx.close();//会调用各个bean的destroy方法
    }
}
