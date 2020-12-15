package org.example.util;

import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * <h1></h1>
 * Created by hanqf on 2020/12/15 16:52.
 */

public class InterfaceFactoryBean<T> implements FactoryBean<T> {
    private Class<T> interfaceClass;

    public Class<T> getInterfaceClass() {
        return interfaceClass;
    }

    public void setInterfaceClass(Class<T> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    /**
     * 新建bean
     * @return
     * @throws Exception
     */
    @Override
    public T getObject() throws Exception {
        //利用反射具体的bean新建实现，不支持T为接口。
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass},new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if(method.getName().equals("output")){
                    System.out.println(proxy.getClass().getName() + "||" + method.getName());
                }else if(method.getName().equals("defaultPrint")){
                    return args[0];
                }
                return null;
            }
        });
    }

    /**
     * 获取bean
     * @return
     */
    @Override
    public Class<?> getObjectType() {
        return interfaceClass;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
