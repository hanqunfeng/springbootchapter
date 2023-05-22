package org.example.learnjuc.sync;


import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Exchanger;

import java.util.UUID;

public class ExchangerDemo3 {

    private static ArrayBlockingQueue<String> fullQueue
            = new ArrayBlockingQueue<>(5);
    private static ArrayBlockingQueue<String> emptyQueue
            = new ArrayBlockingQueue<>(5);
    private static Exchanger<ArrayBlockingQueue<String>> exchanger
            = new Exchanger<>();


    public static void main(String[] args) {
        new Thread(new Producer()).start();
        new Thread(new Consumer()).start();

    }

    /**
     * 生产者
     */
    static class Producer implements Runnable {
        @Override
        public void run() {
            ArrayBlockingQueue<String> current = emptyQueue;
            try {
                while (current != null) {
                    String str = UUID.randomUUID().toString();
                    try {
                        current.add(str);
                        System.out.println("producer：生产了一个序列：" + str + ">>>>>加入到交换区");
                        Thread.sleep(2000);
                    } catch (IllegalStateException e) {
                        System.out.println("producer：队列已满，换一个空的");
                        current = exchanger.exchange(current);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 消费者
     */
    static class Consumer implements Runnable {
        @Override
        public void run() {
            ArrayBlockingQueue<String> current = fullQueue;
            try {
                while (current != null) {
                    if (!current.isEmpty()) {
                        String str = current.poll();
                        System.out.println("consumer：消耗一个序列：" + str);
                        Thread.sleep(1000);
                    } else {
                        System.out.println("consumer：队列空了，换个满的");
                        current = exchanger.exchange(current);
                        System.out.println("consumer：换满的成功~~~~~~~~~~~~~~~~~~~~~~");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
