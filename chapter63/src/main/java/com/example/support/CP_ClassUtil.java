package com.example.support;

import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.support.AopUtils;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * <h1>类工具</h1>
 * Created by hanqf on 2022/9/21 10:55.
 */


public class CP_ClassUtil {

    /**
     * 验证class是否存在
     */
    public static boolean classIsAvailable(String className) {
        boolean result;
        try {
            result = null != Class.forName(className);
        } catch (Throwable t) {
            result = false;
        }
        return result;
    }

    /**
     * 判断类中是否包含指定的方法
     */
    public static boolean hasMethod(Class clazz, String methodName, Class... argsType) {
        Method method = ReflectionUtils.findMethod(clazz, methodName, argsType);
        if (null != method) {
            return true;
        }
        return false;
    }

    /**
     * 判断类中是否包含指定的无参方法
     */
    public static boolean hasMethod(Class clazz, String methodName) {
        Method method = ReflectionUtils.findMethod(clazz, methodName);
        if (null != method) {
            return true;
        }
        return false;
    }

    /**
     * 获取指定类的方法
     */
    public static Method getMethod(Class clazz, String methodName, Class... argsType) {
        Method method = ReflectionUtils.findMethod(clazz, methodName, argsType);
        if (null != method) {
            return method;
        }
        return null;
    }

    /**
     * 获取指定类的无参方法
     */
    public static Method getMethod(Class clazz, String methodName) {
        Method method = ReflectionUtils.findMethod(clazz, methodName);
        if (null != method) {
            return method;
        }
        return null;
    }

    /**
     * 获取指定类的指定方法的返回类型
     */
    public static Class getMethodReturnType(Class clazz, String methodName, Class... argsType) {
        Method method = ReflectionUtils.findMethod(clazz, methodName, argsType);
        if (null != method) {
            return method.getReturnType();
        }
        return null;
    }

    /**
     * 获取指定类的指定无参方法的返回类型
     */
    public static Class getMethodReturnType(Class clazz, String methodName) {
        Method method = ReflectionUtils.findMethod(clazz, methodName);
        if (null != method) {
            return method.getReturnType();
        }
        return null;
    }


    /**
     * 判断类中是否包含指定的字段
     */
    public static boolean hasField(Class clazz, String fieldName) {
        Field field = ReflectionUtils.findField(clazz, fieldName);
        if (field != null) {
            return true;
        }
        return false;
    }

    /**
     * 获取类中指定的字段
     */
    public static Field getField(Class clazz, String fieldName) {
        Field field = ReflectionUtils.findField(clazz, fieldName);
        if (field != null) {
            field.setAccessible(true);
            return field;
        }
        return null;
    }

    /**
     * 获取声明了某个注解的字段，这里只返回获取到的第一个字段
     */
    public static Field getField(Class clazz, Class annotationClass) {
        Assert.notNull(clazz, "Class must not be null");
        Assert.notNull(annotationClass, "AnnotatedType must not be null");
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.getAnnotation(annotationClass) != null) {
                field.setAccessible(true);
                return field;
            }
        }
        return null;
    }

    /**
     * 获取声明了指定注解的全部字段
    */
    public static Field[] getFields(Class clazz, Class annotationClass) {
        Assert.notNull(clazz, "Class must not be null");
        Assert.notNull(annotationClass, "AnnotatedType must not be null");
        List<Field> annotationList = new ArrayList<>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.getAnnotation(annotationClass) != null) {
                field.setAccessible(true);
                annotationList.add(field);
            }
        }
        return annotationList.toArray(new Field[annotationList.size()]);
    }

    /**
     * 获取字段的返回值
     */
    public static Object getFieldValue(Object entity, Field field) {
        Assert.notNull(entity, "entity must not be null");
        Assert.notNull(field, "field must not be null");
        try {
            return field.get(entity);
        } catch (IllegalAccessException e) {
            return null;
        }

    }


    /**
     * <h2>获得定义当前`类`的类型时在其父类上的声明的泛型真实类型</h2>
     * 示例：
     * public class BookInfoGrap extends MustLoginGrap<BookInfoParam, BookInfoResult>
     * 这里当前Class就是 BookInfoGrap.class
     * 其父类上声明的泛型真实类型就是 0:BookInfoParam 1:BookInfoResult
     *
     * @param clazz 当前`类`的类型
     * @param num   第一个泛型类型，从0开始算，如BookInfoParam就是第0个
     * @return java.lang.Class 泛型真实类型
     * @author hanqf
     */
    public static Class getTClass(Class clazz, int num) {
        Class tClass = null;
        Type genericSuperclass = clazz.getGenericSuperclass();
        if (!"java.lang.reflect.Proxy".equals(genericSuperclass.getTypeName())) {
            //获取第二个泛型类型，这里是按实际需要，只需获取第二个泛型类型，这里就是获取<T extends BaseResult>的实际类型
            Type actualTypeArgument = ((ParameterizedType) genericSuperclass).getActualTypeArguments()[num];
            //判断T是否依旧包含泛型，如果包含泛型，则只取出外层类型
            //这里因为为每个BaseResult都创建的实现类，而且其实现类都是不包含泛型的，所以不会执行这个逻辑
            if (actualTypeArgument instanceof ParameterizedType) {
                tClass = (Class) ((ParameterizedType) actualTypeArgument).getRawType();
            } else {//直接返回BaseResult的实现类类型，
                tClass = (Class) actualTypeArgument;
            }
        }
        return tClass;
    }

    /**
     * <h2>获得定义当前`类`或`接口`时在其父接口上的声明的泛型真实类型</h2>
     * 示例：
     * public interface BookInfoJpaRepository extends BaseJpaRepository<BookInfo, Long>
     * 这里当前Interface就是 BookInfoJpaRepository.class
     * 其父类上声明的泛型真实类型就是 0:BookInfo 1:Long
     *
     * @param clazz        当前`类`或`接口`的类型
     * @param interfaceNum 要获取第几个接口，从0开始计算，如BaseJpaRepository就是第0个
     * @param num          接口中第几个泛型类型，从0开始算，如BookInfo就是第0个
     * @return java.lang.Class 泛型真实类型
     * @author hanqf
     */
    public static Class getTIClass(Class clazz, int interfaceNum, int num) {
        Class tClass = null;
        final Type[] genericInterfaces = clazz.getGenericInterfaces();
        Type genericSuperclass = genericInterfaces[interfaceNum];
        if (!"java.lang.reflect.Proxy".equals(genericSuperclass.getTypeName())) {
            //获取第二个泛型类型，这里是按实际需要，只需获取第二个泛型类型，这里就是获取<T extends BaseResult>的实际类型
            Type actualTypeArgument = ((ParameterizedType) genericSuperclass).getActualTypeArguments()[num];
            //判断T是否依旧包含泛型，如果包含泛型，则只取出外层类型
            //这里因为为每个BaseResult都创建的实现类，而且其实现类都是不包含泛型的，所以不会执行这个逻辑
            if (actualTypeArgument instanceof ParameterizedType) {
                tClass = (Class) ((ParameterizedType) actualTypeArgument).getRawType();
            } else {//直接返回BaseResult的实现类类型，
                tClass = (Class) actualTypeArgument;
            }
        }
        return tClass;
    }


    /**
     * 获取当前代理对象的真实目标对象，即实现类
     *
     * @param proxy 代理对象
     * @return 目标对象
     * @throws Exception
     */
    public static Object getProxyTarget(Object proxy) throws Exception {
        if (!AopUtils.isAopProxy(proxy)) {
            return proxy;
        }
        //判断是jdk还是cglib代理
        if (AopUtils.isJdkDynamicProxy(proxy)) {
            proxy = getJdkDynamicProxyTargetObject(proxy);
        } else {
            proxy = getCglibProxyTargetObject(proxy);
        }
        return getProxyTarget(proxy);
    }


    /**
     * 获取Cglib代理对象的真实目标对象，即实现类
     *
     * @param proxy 代理对象
     * @return 目标对象
     * @throws Exception
     */
    private static Object getCglibProxyTargetObject(Object proxy) throws Exception {
        Field h = proxy.getClass().getDeclaredField("CGLIB$CALLBACK_0");
        h.setAccessible(true);
        Object dynamicAdvisedInterceptor = h.get(proxy);
        Field advised = dynamicAdvisedInterceptor.getClass().getDeclaredField("advised");
        advised.setAccessible(true);
        Object target = ((AdvisedSupport) advised.get(dynamicAdvisedInterceptor)).getTargetSource().getTarget();
        return target;
    }

    /**
     * 获取JdkDynamic代理对象的真实目标对象，即实现类
     *
     * @param proxy 代理对象
     * @return 目标对象
     * @throws Exception
     */
    private static Object getJdkDynamicProxyTargetObject(Object proxy) throws Exception {
        Field h = proxy.getClass().getSuperclass().getDeclaredField("h");
        h.setAccessible(true);
        AopProxy aopProxy = (AopProxy) h.get(proxy);
        Field advised = aopProxy.getClass().getDeclaredField("advised");
        advised.setAccessible(true);
        Object target = ((AdvisedSupport) advised.get(aopProxy)).getTargetSource().getTarget();
        return target;
    }


    /**
     * <h2>获取当前代理对象的真实目标接口类型，即接口</h2>
     *
     * @param proxy proxy 代理对象
     * @param num   第几个接口，从0开始计算
     * @return java.lang.Class  接口类型
     * @author hanqf
     */
    public static Class getProxyTargetInterface(Object proxy, int num) throws Exception {
        if (!AopUtils.isAopProxy(proxy)) {
            return null;
        }
        //判断是jdk还是cglib代理
        if (AopUtils.isJdkDynamicProxy(proxy)) {
            return getJdkDynamicProxyTargetInterface(proxy, num);
        } else {
            return getCglibProxyTargetInterface(proxy, num);
        }

    }

    /**
     * 获取CGLIB动态代理对象的目标接口
     *
     * @param proxy 代理对象
     * @param num   第几个接口
     * @return 目标接口类型
     */
    private static Class getCglibProxyTargetInterface(Object proxy, int num) throws Exception {
        Field h = proxy.getClass().getDeclaredField("CGLIB$CALLBACK_0");
        h.setAccessible(true);
        Object dynamicAdvisedInterceptor = h.get(proxy);
        Field advised = dynamicAdvisedInterceptor.getClass().getDeclaredField("advised");
        advised.setAccessible(true);
        final Class<?>[] proxiedInterfaces = ((AdvisedSupport) advised.get(dynamicAdvisedInterceptor)).getProxiedInterfaces();
        return proxiedInterfaces[num];
    }

    /**
     * 获取Jdk动态代理对象的目标接口
     *
     * @param proxy 代理对象
     * @param num   第几个接口
     * @return 目标接口类型
     */
    private static Class getJdkDynamicProxyTargetInterface(Object proxy, int num) throws Exception {
        Field h = proxy.getClass().getSuperclass().getDeclaredField("h");
        h.setAccessible(true);
        AopProxy aopProxy = (AopProxy) h.get(proxy);
        Field advised = aopProxy.getClass().getDeclaredField("advised");
        advised.setAccessible(true);
        final Class<?>[] proxiedInterfaces = ((AdvisedSupport) advised.get(aopProxy)).getProxiedInterfaces();
        return proxiedInterfaces[num];
    }
}
