package com.example.chapter38.config;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

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
        //初始线程池大小
        executor.setCorePoolSize(5);
        //等待队列满后可以扩容的最大线程数
        executor.setMaxPoolSize(10);
        //等待队列大小
        executor.setQueueCapacity(200);
        //超过60秒没用的线程会回收，最少保留CorePoolSize
        executor.setKeepAliveSeconds(60);
        //线程名称前缀
        executor.setThreadNamePrefix("taskExecutor-");
        //线程池满后，最大线程数也到了，此时再有新的任务则使用主线程执行（同步执行）
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        //关闭服务时先执行完成异步任务
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //如果60秒后还没有执行完成任务，则强制关闭
        executor.setAwaitTerminationSeconds(60);

        //new ThreadPoolExecutor.AbortPolicy(); //抛出异常
        //new ThreadPoolExecutor.DiscardOldestPolicy();//从队里中去除最久没有执行的任务
        //new ThreadPoolExecutor.DiscardPolicy();//丢弃当前任务
        return executor;
    }

    // 默认实现类是：SimpleAsyncUncaughtExceptionHandler，这里使用自定义处理器
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new CustomAsyncExceptionHandler();
    }

}
