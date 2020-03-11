package org.example.proxy;/**
 * Created by hanqf on 2020/3/3 16:52.
 */


import org.example.proxy.util.Interceptor;
import org.example.proxy.util.Invocation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author hanqf
 * @date 2020/3/3 16:52
 */
public class ProxyBean implements InvocationHandler {

    private Object target = null;

    private Interceptor interceptor = null;

    //绑定代理对象
    //target : 被代理对象
    //interceptor ： 拦截器
    //返回结果为代理对象，使用时可以强制转换为对应的类型
    public static Object getProxyBean(Object target, Interceptor interceptor) {
        //封装代理proxyBean对象，为调用invoke填充数据
        ProxyBean proxyBean = new ProxyBean();
        proxyBean.target = target;
        proxyBean.interceptor = interceptor;

        Object proxy = Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass().getInterfaces(), proxyBean);
        return proxy;
    }

    //处理代理对象方法逻辑,也就是代理对象执行方法时真实的执行过程
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        boolean exceptionFlag = false;
        //封装反射要用的的对象，其实把这三个参数直接传递到around里也行
        Invocation invocation = new Invocation(args, method, target);
        Object retObj = null;
        try {
            if (this.interceptor.before()) {
                retObj = this.interceptor.around(invocation);
            } else {
                retObj = method.invoke(target, args);
            }
        } catch (Exception ex) {//产生异常
            exceptionFlag = true;
        }
        this.interceptor.after();
        if (exceptionFlag) {
            this.interceptor.afterThrowing();
        } else {
            this.interceptor.afterReturning();
            return retObj;
        }
        return null;
    }
}
