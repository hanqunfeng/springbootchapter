package org.example.proxy.util;/**
 * Created by hanqf on 2020/3/3 16:42.
 */

import java.lang.reflect.InvocationTargetException;

/**
 * 代理拦截器接口
 * @author hanqf
 * @date 2020/3/3 16:42
 */
public interface Interceptor {

    //事前方法
    public boolean before();
    //事后方法
    public void after();
    //取代原有方法
    //这里自定义了一个Invocation类，目的是封装反射相关参数为一个对象，没必要传递那么多参数
    public Object around(Invocation invocation) throws InvocationTargetException,IllegalAccessException;
    //返回方法
    public void afterReturning();
    //当发生异常时执行
    public void afterThrowing();
    //是否使用around方法
    boolean useAround();

}
