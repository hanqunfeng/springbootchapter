package org.example.proxy.util;/**
 * Created by hanqf on 2020/3/3 16:49.
 */


import java.lang.reflect.InvocationTargetException;

/**
 * 代理拦截器
 * @author hanqf
 * @date 2020/3/3 16:49
 */
public class MyInterceptor implements Interceptor {

    @Override
    public boolean before() {
        System.out.println("before....");
        return true;
    }

    @Override
    public void after() {
        System.out.println("after....");
    }

    @Override
    public Object around(Invocation invocation) throws InvocationTargetException, IllegalAccessException {
        System.out.println("around before....");
        //执行真实的对象方法
        Object object = invocation.proceed();
        System.out.println("around after....");
        return object;
    }

    @Override
    public void afterReturning() {
        System.out.println("afterReturning....");
    }

    @Override
    public void afterThrowing() {
        System.out.println("afterThrowing");
    }

    @Override
    public boolean useAround() {
        return true;
    }
}
