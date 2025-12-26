package com.example;

import com.example.lock.RedisReentrantLock;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * Created by hanqf on 2025/12/17 16:50.
 */

@SpringBootTest
public class LockTests {
    @Autowired
    RedisReentrantLock redisReentrantLock;

    String lockKey = "order:123";

    // 测试获取锁
    @Test
    void demo() {
        if (redisReentrantLock.tryLock(lockKey, 5, 30, TimeUnit.SECONDS)) {
            try {
                // 业务逻辑
                doBusiness();
            } finally {
                redisReentrantLock.unlock(lockKey);
            }
        }
    }
    // 测试可重入锁
    private void doBusiness() {
        try {
            if (redisReentrantLock.lock(lockKey, 30, TimeUnit.SECONDS)) {
                try {
                    System.out.println("开始执行业务逻辑");
                    // 模拟业务逻辑执行时间，这里设置200秒，就是为了测试锁的自动续期功能
                    TimeUnit.SECONDS.sleep(200);
                    System.out.println("结束执行业务逻辑");
                } finally {
                    redisReentrantLock.unlock(lockKey);
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    // 测试多线程同时获取锁
    @Test
    void demoMultiThread() throws InterruptedException {
        int threadCount = 5;
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            new Thread(() -> {
                try {
                    // 每个线程都尝试获取同一个锁
                    if (redisReentrantLock.tryLock(lockKey, 10, 30, TimeUnit.SECONDS)) {
                        try {
                            successCount.incrementAndGet();
                            System.out.println("线程 " + threadId + " 获取锁成功，开始执行业务");

                            // 模拟业务执行时间
                            TimeUnit.SECONDS.sleep(3);

                            System.out.println("线程 " + threadId + " 业务执行完成");
                        } finally {
                            redisReentrantLock.unlock(lockKey);
                            System.out.println("线程 " + threadId + " 释放锁");
                        }
                    } else {
                        System.out.println("线程 " + threadId + " 获取锁失败");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            }).start();
        }

        // 等待所有线程执行完成
        latch.await();
        System.out.println("成功获取锁的线程数: " + successCount.get());
    }
}
