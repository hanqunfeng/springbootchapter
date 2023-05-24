package org.example.learnjuc.blockingqueue;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class BlockingQueueExample {

    private static final int QUEUE_CAPACITY = 5;
    private static final int PRODUCER_DELAY_MS = 1000;
    private static final int CONSUMER_DELAY_MS = 2000;

    public static void main(String[] args) throws InterruptedException {


        // 创建一个容量为QUEUE_CAPACITY的阻塞队列
        BlockingQueue<String> queue = new ArrayBlockingQueue<>(QUEUE_CAPACITY);

        // 创建一个生产者线程
        Runnable producer = () -> {
            while (true) {
                try {
                    // 在队列满时阻塞
                    queue.put("producer");
                    System.out.println("生产了一个元素，队列中元素个数：" + queue.size());
                    Thread.sleep(PRODUCER_DELAY_MS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(producer).start();

        // 创建一个消费者线程
        Runnable consumer = () -> {
            while (true) {
                try {
                    // 在队列为空时阻塞
                    String element = queue.take();
                    System.out.println("消费了一个元素，队列中元素个数：" + queue.size());
                    Thread.sleep(CONSUMER_DELAY_MS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(consumer).start();
    }
}
