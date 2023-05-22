package org.example.learnjuc.sync;


import java.util.concurrent.locks.ReentrantLock;

/**
 * 模拟抢票场景
 */
public class ReentrantLockDemo {

    private final ReentrantLock lock = new ReentrantLock();//默认非公平
    private static int tickets = 8; // 总票数

    public void buyTicket() {
        lock.lock(); // 获取锁
        try {
            if (tickets > 0) { // 还有票    读
                try {
                    Thread.sleep(10); // 休眠10ms
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName() + "购买了第" + tickets-- + "张票"); //写

//                buyTicket();
            } else {
                System.out.println("票已经卖完了，" + Thread.currentThread().getName() + "抢票失败");
            }

        } finally {
            lock.unlock(); // 释放锁
        }
    }


    public static void main(String[] args) {
        ReentrantLockDemo ticketSystem = new ReentrantLockDemo();
        for (int i = 1; i <= 10; i++) {
            Thread thread = new Thread(() -> {
                ticketSystem.buyTicket(); // 抢票

            }, "线程" + i);
            // 启动线程
            thread.start();
        }


        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("剩余票数：" + tickets);
    }
}

