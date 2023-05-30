package org.example.learnjuc.threadPool;

/**
 * <h1>线程池工具类</h1>
 * Created by hanqf on 2023/5/26 10:53.
 */


import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadPoolSingleton_1 {
    private static volatile ThreadPoolExecutor executor;
    private static final Lock lock = new ReentrantLock();

    private ThreadPoolSingleton_1() {
        // 私有构造函数，防止实例化
    }

    private static ThreadPoolExecutor getExecutor() {
        if (executor == null || executor.isShutdown()) {
            lock.lock();
            try {
                if (executor == null || executor.isShutdown()) {
                    executor = new ThreadPoolExecutor(10, 20, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1000));
                }
            } finally {
                lock.unlock();
            }
        }
        return executor;
    }

    public static ThreadPoolExecutor getExecutorInstance() {
        return executor;
    }

    public static void shutdownThreadPool() {
        lock.lock();
        try {
            // RUNNING时可以执行shutdown
            if (executor != null && !executor.isShutdown()) {
                executor.shutdown();
            }
        } finally {
            lock.unlock();
        }
    }

    public static List<Runnable> shutdownNowThreadPool() {
        List<Runnable> runnableList = null;
        lock.lock();
        try {
            // RUNNING时可以执行shutdownNow
            if (executor != null && !executor.isShutdown()) {
                runnableList = executor.shutdownNow();
            }
        } finally {
            lock.unlock();
        }
        return runnableList;
    }

    public static boolean isTerminated() {
        lock.lock();
        try {
            return executor != null && executor.isTerminated();
        } finally {
            lock.unlock();
        }
    }

    public static boolean isShutdown() {
        lock.lock();
        try {
            return executor != null && executor.isShutdown();
        } finally {
            lock.unlock();
        }
    }

    public static void execute(Runnable task) {
        getExecutor().execute(task);
    }

    public static <E> Future<E> submit(Callable<E> task) {
        return getExecutor().submit(task);
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // 执行没有返回值的任务
        ThreadPoolSingleton_1.execute(() -> {
            // 任务逻辑
        });

        // 执行有返回值的任务
        final Future<Integer> future = ThreadPoolSingleton_1.submit(() -> {
            // 任务逻辑
            String result = "hello world";
            return result.length();
        });

        System.out.println(future.get());

        // 关闭线程池，系统缺省线程池，可以不用关闭
        ThreadPoolSingleton_1.shutdownThreadPool();
    }
}

