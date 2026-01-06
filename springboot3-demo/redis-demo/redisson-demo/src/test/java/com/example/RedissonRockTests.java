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
    void redisLock() throws InterruptedException {

        RLock lock = redissonClient.getLock("myLock");

        // 传统锁定方式，阻塞等待获取锁
        // lock.lock();

        // 或者，获取锁并在10秒后自动解锁
        // lock.lock(10, TimeUnit.SECONDS);

        // 或等，尝试在 100 秒内获取锁，获得后在 10 秒后自动解锁
        boolean res = lock.tryLock(100, 10, TimeUnit.SECONDS);
        if (res) {
            try {
                System.out.println("获取锁成功");
                System.out.println("开始执行业务逻辑");
                TimeUnit.SECONDS.sleep(5);
            } finally {
                lock.unlock();
            }
        }
    }

}
