package com.example.chapter38.config;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

import java.lang.reflect.Method;

/**
 * <p>异步异常处理器，默认实现是SimpleAsyncUncaughtExceptionHandler</p>
 * Created by hanqf on 2020/9/1 15:43.
 *
 * 当方法返回值是Future的时候，异常捕获是没问题的 - Future.get()方法会抛出异常。
 * 但是，如果返回类型是Void，那么异常在当前线程就捕获不到。因此，我们需要添加额外的配置来处理异常。
 */
public class CustomAsyncExceptionHandler
        implements AsyncUncaughtExceptionHandler {

    @Override
    public void handleUncaughtException(Throwable throwable, Method method, Object... obj) {

        System.out.println("Exception message - " + throwable.getMessage());
        System.out.println("Class name - " + method.getDeclaringClass().getName());
        System.out.println("Method name - " + method.getName());
        for (Object param : obj) {
            System.out.println("Parameter value - " + param);
        }
    }

}
