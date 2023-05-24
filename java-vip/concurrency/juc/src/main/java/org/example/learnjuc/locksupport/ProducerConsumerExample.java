package org.example.learnjuc.locksupport;

/**
 * 在上述示例中，我们使用producerBlocker和consumerBlocker分别作为生产者线程和消费者线程的阻塞对象。在生产者线程中，当缓冲区满时，调用LockSupport.park(producerBlocker)方法阻塞生产者线程，并关联producerBlocker对象。当有消费者线程从缓冲区取出数据后，调用LockSupport.unpark(consumerThread)方法唤醒生产者线程继续生产。
 *
 * 同样地，在消费者线程中，当缓冲区为空时，调用LockSupport.park(consumerBlocker)方法阻塞消费者线程，并关联consumerBlocker对象。当有生产者线程向缓冲区添加数据后，调用LockSupport.unpark(producerThread)方法唤醒消费者线程进行消费。
 *
 * 通过使用不同的阻塞对象，我们可以将阻塞和唤醒操作更加细粒度地应用于特定的线程，实现更精确的线程协作和同步机制。这样可以避免不必要的线程唤醒。
 * Created by hanqf on 2023/5/23 14:36.
 */


import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.LockSupport;

public class ProducerConsumerExample {
    private static final int BUFFER_SIZE = 10;
    private static Queue<Integer> buffer = new ConcurrentLinkedQueue<>();
    private static Thread producerThread;
    private static Thread consumerThread;

    public static void main(String[] args) {
        producerThread = new Thread(new Producer());
        consumerThread = new Thread(new Consumer());

        producerThread.start();
        consumerThread.start();
    }

    static class Producer implements Runnable {
        @Override
        public void run() {
            for (int i = 1; i <= 20; i++) {
                while (buffer.size() >= BUFFER_SIZE) {
                    LockSupport.park(producerThread); // 阻塞生产者线程
                }
                buffer.offer(i);
                System.out.println("Produced: " + i);
                LockSupport.unpark(consumerThread); // 唤醒消费者线程
            }
        }
    }

    static class Consumer implements Runnable {
        @Override
        public void run() {
            while (true) {
                while (buffer.isEmpty()) {
                    LockSupport.park(consumerThread); // 阻塞消费者线程
                }
                int item = buffer.poll();
                System.out.println("Consumed: " + item);
                LockSupport.unpark(producerThread); // 唤醒生产者线程
            }
        }
    }
}

