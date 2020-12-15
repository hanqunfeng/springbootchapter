package com.example.chapter49;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

/**
 * <h1></h1>
 * Created by hanqf on 2020/12/8 18:20.
 */

@Configuration
@EnableAsync
public class DemoConfig {

    @Autowired
    private Environment environment;

    @Bean
    public String ev(){
        System.out.println(environment);
        return environment.getProperty("a");
    }

    @Bean
    public CommandLineRunner commandLineRunner(AsyncTask asyncTask){
        return args -> {
            for (int i = 0; i < 10; i++) {
                asyncTask.loopPrint(i);
            }
        };
    }

    @Component
    public class AsyncTask {
        //异步任务不是使用的主线程，而是使用一个线程池，默认有8个线程，线程名以task-开头
        @Async //还可以注解在类上，意味着整个类的所有方法都是异步的。
        public void loopPrint(Integer i){
            System.out.println(Thread.currentThread().getName() + ":当前计数为：" + i);
        }
    }


}
