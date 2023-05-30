package org.example.learnjuc.threadPool;

/**
 * <h1>线程池工具类</h1>
 * Created by hanqf on 2023/5/26 10:57.
 */


import java.util.concurrent.*;

public class NewThreadPoolExecutor {

    private ThreadPoolExecutor executor; // 线程池执行器

    private NewThreadPoolExecutor() {
    }

    // 使用构造器模式获取线程池实例
    private NewThreadPoolExecutor getExecutor(int corePoolSize, int maxPoolSize, long keepAliveTime, TimeUnit unit,
                                              int queueSize, RejectedExecutionHandler rejectedExecutionHandler) {
        executor = new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime, unit,
                new ArrayBlockingQueue<>(queueSize), rejectedExecutionHandler);
        return this;
    }

    public void shutdownThreadPool() {
        if (executor != null) {
            executor.shutdown(); // 关闭线程池
        }
    }

    public void shutdownNowThreadPool() {
        if (executor != null) {
            executor.shutdownNow(); // 立即关闭线程池
        }
    }

    public boolean isTerminated() {
        return  executor != null && executor.isTerminated(); // 判断线程池是否已经终止
    }

    public boolean isShutdown() {
        return executor != null && executor.isShutdown(); // 判断线程池是否已经关闭
    }

    public void execute(Runnable task) {
        if (executor != null) {
            executor.execute(task); // 提交任务给线程池执行
        }
    }

    public <E> Future<E> submit(Callable<E> task) {
        if (executor != null) {
            return executor.submit(task);
        }
        return null;
    }

    public static class Builder {
        private int corePoolSize = 10; // 核心线程数
        private int maxPoolSize = 20; // 最大线程数
        private long keepAliveTime = 60; // 线程最大空闲时间
        private TimeUnit unit = TimeUnit.SECONDS; // 空闲时间的单位
        private int queueSize = 100; // 任务队列大小
        private RejectedExecutionHandler rejectedExecutionHandler = new ThreadPoolExecutor.CallerRunsPolicy(); // 拒绝策略

        public Builder corePoolSize(int corePoolSize) {
            this.corePoolSize = corePoolSize;
            return this;
        }

        public Builder maxPoolSize(int maxPoolSize) {
            this.maxPoolSize = maxPoolSize;
            return this;
        }

        public Builder keepAliveTime(long keepAliveTime, TimeUnit unit) {
            this.keepAliveTime = keepAliveTime;
            this.unit = unit;
            return this;
        }

        public Builder queueSize(int queueSize) {
            this.queueSize = queueSize;
            return this;
        }

        public Builder rejectedExecutionHandler(RejectedExecutionHandler rejectedExecutionHandler) {
            this.rejectedExecutionHandler = rejectedExecutionHandler;
            return this;
        }


        public NewThreadPoolExecutor build() {
            return new NewThreadPoolExecutor().getExecutor(corePoolSize, maxPoolSize, keepAliveTime, unit, queueSize, rejectedExecutionHandler);
        }
    }

    public static void main(String[] args) {

        // 使用Builder进行参数初始化并创建线程池
        NewThreadPoolExecutor newThreadPoolExecutor = new NewThreadPoolExecutor.Builder()
                .corePoolSize(5)
                .maxPoolSize(10)
                .keepAliveTime(30, TimeUnit.SECONDS)
                .queueSize(50)
                .rejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy())
                .build();

        // 执行任务
        newThreadPoolExecutor.execute(() -> {
            // 任务逻辑
        });

        // 执行有返回值的任务
        final Future<Integer> future = newThreadPoolExecutor.submit(() -> {
            // 任务逻辑
            String result = "hello world";
            return result.length();
        });


        // 关闭线程池，非系统共用线程池，用完要关闭
        newThreadPoolExecutor.shutdownThreadPool();
    }
}

