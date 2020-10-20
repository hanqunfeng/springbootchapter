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
        //在调用线程池调用了 shutdown（）方法后，是否继续执行现有周期任务（通过 scheduleAtFixedRate、scheduleWithFixedDelay 提交的周期任务）的策略；默认值为false
        threadPoolExecutor.setContinueExistingPeriodicTasksAfterShutdownPolicy(true);

        return threadPoolExecutor;
    }
}
