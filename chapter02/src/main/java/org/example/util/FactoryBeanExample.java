package org.example.util;

import org.example.model.FactoryBeanModelDemo;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

/**
 * <h1>FactoryBean</h1>
 * Created by hanqf on 2020/12/15 10:56.
 * FactoryBean 返回的对象会被注册到spring上下文
 */

@Component
public class FactoryBeanExample implements FactoryBean {
    @Override
    public Object getObject() throws Exception {
        return new FactoryBeanModelDemo();
    }

    @Override
    public Class<?> getObjectType() {
        return FactoryBeanModelDemo.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
