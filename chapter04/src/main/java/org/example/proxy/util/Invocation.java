package org.example.proxy.util;/**
 * Created by hanqf on 2020/3/3 16:47.
 */


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 这个类的作用就是通过反射执行对应的方法
 * @author hanqf
 * @date 2020/3/3 16:47
 */
public class Invocation {

    //方法参数数组
    private Object[] params;
    //方法对象
    private Method method;
    //目标类对象
    private Object target;

    public Invocation(Object[] params, Method method, Object target) {
        this.params = params;
        this.method = method;
        this.target = target;
    }

    //反射方法
    public Object proceed() throws InvocationTargetException, IllegalAccessException {
        return method.invoke(target,params);
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }
}
