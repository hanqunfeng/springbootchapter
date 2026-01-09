package com.example;

import org.junit.jupiter.api.Test;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

/**
 *
 * Created by hanqf on 2025/12/25 14:59.
 */

@SpringBootTest
public class RedissonRockTests {

    @Autowired
    private RedissonClient redissonClient;


    @Test
    void testDistributedLockWithTimeout() throws InterruptedException {

        RLock lock = redissonClient.getLock("myLock");
        try {
            // 传统锁定方式，阻塞等待获取锁，会自动续期
            // lock.lock();

            // 阻塞等待获取锁，获取锁后在10秒后自动解锁，这里要注意，配置了自动释放锁的时间，就不会自动续期了，到时间就会释放锁
            // lock.lock(10, TimeUnit.SECONDS);

            // 尝试在 100 秒内获取锁，成功返回 true，失败返回 false，会自动续期
            // boolean res = lock.tryLock(100, TimeUnit.SECONDS);

            // 尝试在 100 秒内获取锁，获得后在 10 秒后自动解锁，这里要注意，配置了自动释放锁的时间，就不会自动续期了，到时间就会释放锁
            boolean res = lock.tryLock(100, 10, TimeUnit.SECONDS);
            if (res) {
                System.out.println("获取锁成功");
                System.out.println("开始执行业务逻辑");
                // 模拟执行业务逻辑
                TimeUnit.SECONDS.sleep(5);
            }
        } finally {
            // 检查当前线程是否持有锁
            if (lock.isHeldByCurrentThread()) {
                // 释放锁
                lock.unlock();
            }
        }

    }

}
