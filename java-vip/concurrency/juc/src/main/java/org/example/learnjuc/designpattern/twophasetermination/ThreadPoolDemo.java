package org.example.learnjuc.designpattern.twophasetermination;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 如何优雅地终止线程池
 */
public class ThreadPoolDemo {
    public static void main(String[] args) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(5);

        for (int i = 0; i < 10; i++) {
            executorService.submit(() -> {
                try {
                    // 执行任务操作
                    System.out.println(Thread.currentThread().getName() + "正在执行任务...");
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    // 重新设置中断状态
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                } finally {
                    System.out.println(Thread.currentThread().getName() + "任务执行完毕");
                }
            });
        }

        // 停止线程池接受新的任务，但不能强制停止已经提交的任务
        executorService.shutdown();

        // 等待线程池中的任务执行完毕，或者超时时间到达
        boolean terminated = executorService.awaitTermination(3, TimeUnit.SECONDS);
        if (!terminated) {
            // 如果线程池中还有未执行完毕的任务，则调用线程池的shutdownNow方法，中断所有正在执行的任务
            // 如果有还没开始执行的任务，则返回未执行的任务列表
            List<Runnable> tasks = executorService.shutdownNow();
            System.out.println("剩余未执行的任务数：" + tasks.size());
        }

    }
}
