package org.example.learnjuc.sync;

import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockDemo2 {

    public static void main(String[] args) throws InterruptedException {
        Counter counter = new Counter(); // 创建计数器对象

        // 测试递归调用
        counter.recursiveCall(10);
    }


}

class Counter {
    private final ReentrantLock lock = new ReentrantLock(); // 创建 ReentrantLock 对象
    private volatile int count = 0; // 计数器


    public void recursiveCall(int num) {
        lock.lock(); // 获取锁
        try {
            if (num == 0) {
                return;
            }
            System.out.println("执行递归，num = " + num);
            recursiveCall(num - 1);
        } finally {
            lock.unlock(); // 释放锁
        }
    }
}
