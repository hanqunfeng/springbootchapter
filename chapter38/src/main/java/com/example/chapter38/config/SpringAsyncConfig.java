package com.example.chapter38.config;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * <p>异步配置</p>
 * Created by hanqf on 2020/9/1 15:41.
 */


@Configuration
//开启异步注解支持
@EnableAsync
public class SpringAsyncConfig implements AsyncConfigurer {

    // 默认情况下，Spring使用SimpleAsyncTaskExecutor去执行这些异步方法（此执行器没有限制线程数）,
    // 所以需要自定义线程池
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.initialize();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(25);
        return executor;
    }

    // 默认实现类是：SimpleAsyncUncaughtExceptionHandler，这里使用自定义处理器
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new CustomAsyncExceptionHandler();
    }

}
