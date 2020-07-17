package com.example.config;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * <p></p>
 * 参考：https://www.cnblogs.com/williamjie/p/11250679.html
 * Created by hanqf on 2020/7/17 12:08.
 */

@Component
public class RedissonLockDemo {

    @Autowired
    RedissonClient redissonClient;

    public void testRedissonLock() {
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
}
