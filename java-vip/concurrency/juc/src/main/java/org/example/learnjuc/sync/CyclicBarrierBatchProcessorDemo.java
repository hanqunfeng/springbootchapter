package org.example.learnjuc.sync;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class CyclicBarrierBatchProcessorDemo {

    public static void main(String[] args) {
        //生成数据     任务分工
        List<Integer> data = new ArrayList<>();
        for (int i = 1; i <= 50; i++) {
            data.add(i);
        }

        //指定数据处理大小
        int batchSize = 5;
        CyclicBarrierBatchProcessor processor = new CyclicBarrierBatchProcessor(data, batchSize);
        //处理数据
        processor.process(batchData -> {
            for (Integer i : batchData) {
                System.out.println(Thread.currentThread().getName() + "处理数据" + i);
            }
        });


    }
}


class CyclicBarrierBatchProcessor {
    private List<Integer> data;
    private int batchSize;
    private CyclicBarrier barrier;
    private List<Thread> threads;

    public CyclicBarrierBatchProcessor(List<Integer> data, int batchSize) {
        this.data = data;
        this.batchSize = batchSize;
        this.barrier = new CyclicBarrier(batchSize);
        this.threads = new ArrayList<>();
    }

    public void process(BatchTask task) {
        // 对任务分批，获取线程数
        int threadCount = (data.size() + batchSize - 1) / batchSize;
        for (int i = 0; i < threadCount; i++) {
            int start = i * batchSize;
            int end = Math.min(start + batchSize, data.size());
            //获取每个线程处理的任务数
            List<Integer> batchData = data.subList(start, end);
            Thread thread = new Thread(() -> {
                task.process(batchData);
                try {
                    barrier.await();

                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
            });
            threads.add(thread);
            thread.start();
        }

    }

    public interface BatchTask {
        void process(List<Integer> batchData);
    }
}
