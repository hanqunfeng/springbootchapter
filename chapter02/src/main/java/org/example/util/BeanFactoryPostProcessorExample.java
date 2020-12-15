package org.example.util;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;

/**
 * <h1>BeanFactoryPostProcessor</h1>
 * Created by hanqf on 2020/12/15 10:30.
 */

@Component
public class BeanFactoryPostProcessorExample implements BeanFactoryPostProcessor {
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        BeanDefinition dog = configurableListableBeanFactory.getBeanDefinition("dog");
        System.out.println(dog.getBeanClassName());
        System.out.println(dog.getScope());
        System.out.println(dog.getPropertyValues());
        System.out.println(dog.getConstructorArgumentValues());
        System.out.println(dog.getFactoryBeanName());
        System.out.println(dog.getFactoryMethodName());
    }
}
