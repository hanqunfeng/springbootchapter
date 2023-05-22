package org.example.learnjuc.sync;

import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 基于ReentrantLock和Condition实现一个简单队列
 */
public class ReentrantLockDemo3 {

    public static void main(String[] args) {
        // 创建队列
        Queue queue = new Queue(5);
        //启动生产者线程
        new Thread(new Producer(queue)).start();
        //启动消费者线程
        new Thread(new Customer(queue)).start();

    }
}

/**
 * 队列封装类
 */
class Queue {
    private Object[] items ;
    int size = 0;
    int takeIndex;
    int putIndex;
    private ReentrantLock lock;
    public Condition notEmpty; //消费者线程阻塞唤醒条件，队列为空阻塞，生产者生产完唤醒
    public Condition notFull; //生产者线程阻塞唤醒条件，队列满了阻塞，消费者消费完唤醒

    public Queue(int capacity){
        this.items = new Object[capacity];
        lock = new ReentrantLock();
        notEmpty = lock.newCondition();
        notFull =  lock.newCondition();
    }


    public void put(Object value) throws Exception {
        //加锁
        lock.lock();
        try {
            while (size == items.length)
                // 队列满了让生产者等待
                notFull.await();

            items[putIndex] = value;
            if (++putIndex == items.length)
                putIndex = 0;
            size++;
            notEmpty.signal(); // 生产完唤醒消费者

        } finally {
            System.out.println("producer生产：" + value);
            //解锁
            lock.unlock();
        }
    }

    public Object take() throws Exception {
        lock.lock();
        try {
            // 队列空了就让消费者等待
            while (size == 0)
                notEmpty.await();

            Object value = items[takeIndex];
            items[takeIndex] = null;
            if (++takeIndex == items.length)
                takeIndex = 0;
            size--;
            notFull.signal(); //消费完唤醒生产者生产
            return value;
        } finally {
            lock.unlock();
        }
    }
}

/**
 * 生产者
 */
class Producer implements Runnable {

    private Queue queue;

    public Producer(Queue queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            // 隔1秒轮询生产一次
            while (true) {
                Thread.sleep(1000);
                queue.put(new Random().nextInt(1000));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

/**
 * 消费者
 */
class Customer implements Runnable {

    private Queue queue;

    public Customer(Queue queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            // 隔2秒轮询消费一次
            while (true) {
                Thread.sleep(2000);
                System.out.println("consumer消费：" + queue.take());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
