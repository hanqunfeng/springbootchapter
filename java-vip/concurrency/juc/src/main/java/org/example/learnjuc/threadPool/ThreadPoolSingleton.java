package org.example.learnjuc.threadPool;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * <h1></h1>
 * Created by hanqf on 2023/5/26 16:24.
 */


public class ThreadPoolSingleton {
    private static ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 20, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1000), new ThreadPoolExecutor.CallerRunsPolicy());;

    private ThreadPoolSingleton() {
        // 私有构造函数，防止实例化
    }

    public static ThreadPoolExecutor getExecutor() {
        return executor;
    }

    public static void main(String[] args) {
        // 执行没有返回值的任务
        ThreadPoolSingleton.getExecutor().execute(() -> {
            // 任务逻辑
        });

        // 执行有返回值的任务
        final Future<Integer> future = ThreadPoolSingleton.getExecutor().submit(() -> {
            // 任务逻辑
            String result = "hello world";
            return result.length();
        });
    }
}
