package com.example.redislock;

import com.example.redislock.util.RedisDistributedLock;
import org.junit.jupiter.api.Test;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

@SpringBootTest
class RedisLockApplicationTests {

    @Autowired
    RedisDistributedLock redisDistributedLock;
    @Autowired
    RedissonClient redissonClient;

    @Test
    void testRedisDistributedLock() {
        boolean redisLock = redisDistributedLock.tryGetDistributedLock("redisLock", 30000);
        if (redisLock) {
            try {
                //执行业务逻辑
                Thread.sleep(10000);
                System.out.println(123);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                redisDistributedLock.releaseDistributedLock("redisLock");
            }
        }
    }

    @Test
    void testRedissonLock() {
        RLock myLock = redissonClient.getLock("myLock");
        //设置锁超时时间，防止异常造成死锁，也可以不设置，默认30秒
        //myLock.lock();
        myLock.lock(20, TimeUnit.SECONDS);
        try {
            //执行业务逻辑
            Thread.sleep(10000);
            System.out.println(123);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            myLock.unlock();
        }

    }


    @Test
    void testRedissonLock2() {
        RLock myLock = redissonClient.getLock("myLock");
        //执行业务逻辑
        try {
            //设置锁超时时间，防止异常造成死锁，也可以不设置，默认30秒
            myLock.lock();
            Thread.sleep(10000);
            System.out.println(123);

            myLock.lock(); //同一个客户端可与锁多次，但是释放时也要释放多次
            Thread.sleep(10000);
            System.out.println(456);

            myLock.unlock();
            Thread.sleep(10000);
            myLock.unlock();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }



    }
}
