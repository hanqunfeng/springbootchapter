package com.example.chapter41.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.*;

/**
 * <h1>计划任务配置类</h1>
 * Created by hanqf on 2020/10/20 10:58.
 */

@Configuration
@EnableScheduling
public class ScheduledConfig implements SchedulingConfigurer {
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(scheduledExecutor());
    }

    @Bean
    public Executor scheduledExecutor() {
        RejectedExecutionHandler handler = new ThreadPoolExecutor.CallerRunsPolicy();
        ScheduledThreadPoolExecutor threadPoolExecutor = new ScheduledThreadPoolExecutor(10, handler);
        threadPoolExecutor.setMaximumPoolSize(20);
        threadPoolExecutor.setKeepAliveTime(60,TimeUnit.SECONDS);

        return threadPoolExecutor;
    }
}
