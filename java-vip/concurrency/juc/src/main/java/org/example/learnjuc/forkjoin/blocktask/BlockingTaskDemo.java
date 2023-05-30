package org.example.learnjuc.forkjoin.blocktask;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

public class BlockingTaskDemo {
    public static void main(String[] args) {
        //构建一个forkjoin线程池
        ForkJoinPool pool = new ForkJoinPool();

        //创建一个异步任务，并将其提交到ForkJoinPool中执行
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            try {
                // 模拟一个耗时的任务
                TimeUnit.SECONDS.sleep(5);
                return "Hello, world!";
            } catch (InterruptedException e) {
                e.printStackTrace();
                return null;
            }
        }, pool);

        try {
            // 等待任务完成，并获取结果
            String result = future.get();

            System.out.println(result);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } finally {
            //关闭ForkJoinPool，释放资源
            pool.shutdown();
        }
    }
}
