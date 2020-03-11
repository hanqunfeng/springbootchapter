package org.example.util;/**
 * Created by hanqf on 2020/3/2 12:16.
 */


import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * @author hanqf
 * @date 2020/3/2 12:16
 */
@Component
public class BeanPostProcessorExample implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("4:BeanPostProcessor:postProcessBeforeInitialization:" + bean.getClass().getSimpleName() + ":" + beanName);
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("7:BeanPostProcessor:postProcessAfterInitialization:" + bean.getClass().getSimpleName() + ":" + beanName);
        return bean;
    }
}
