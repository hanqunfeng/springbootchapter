package com.example.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * <p>Redis分布式锁</p>
 * 加锁和释放锁都需要保证原子性
 * Created by hanqf on 2020/7/17 10:11.
 */

@Component
public class RedisDistributedLock {
    /**
     * LUA 删除锁，自己的锁自己珊
     */
    private static String RELEASE_LOCK_SCRIPT = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * <p>尝试获取锁</p>
     *
     * @param lockKey    锁key
     * @param value      锁value，这里可以设置一个有意义的值，比如用户Id，业务名称等，可以用于区分锁是谁加的
     * @param expireTime 锁的过期时间，单位秒，根据业务执行时间设置过期时间，设置过期时间的目的是防止发生异常不能正确释放锁
     * @return boolean
     * @author hanqf
     * 2020/7/17 10:54
     */
    public boolean tryGetDistributedLock(String lockKey, String value, long expireTime) {
        return stringRedisTemplate.opsForValue().setIfAbsent(lockKey, value, expireTime, TimeUnit.SECONDS);
    }

    /**
     * <p>尝试在指定时间内获取锁</p>
     *
     * @param lockKey     锁key
     * @param value       锁value
     * @param expireTime  锁的过期时间，单位秒，根据业务执行时间设置过期时间，设置过期时间的目的是防止发生异常不能正确释放锁
     * @param waitTime    等待时间，单位秒
     * @param waitPerTime 等待间隔时间，单位秒
     * @return boolean
     * @author hanqf
     * 2020/7/17 10:56
     */
    public boolean tryGetDistributedLock(String lockKey, String value, long expireTime, long waitTime, long waitPerTime) {
        long waitAlready = 0L;
        if (!tryGetDistributedLock(lockKey, value, expireTime) && waitAlready < waitTime) {
            try {
                Thread.sleep(waitPerTime * 1000);
            } catch (InterruptedException e) {
                System.out.println(String.format("Interrupted when trying to get a lock. key:[%s]", lockKey));
            }
            waitAlready += waitPerTime;
        }

        if (waitAlready < waitTime) {
            return true;
        } else {
            System.out.println(String.format("try to get lock by key [%s] failed after waiting for [%d] sceonds", lockKey, waitTime));
            return false;
        }
    }

    /**
     * <p>释放锁</p>
     *
     * @param lockKey 锁key
     * @param value   锁value
     * @return boolean
     * @author hanqf
     * 2020/7/17 10:58
     */
    public boolean releaseDistributedLock(String lockKey, String value) {
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(RELEASE_LOCK_SCRIPT, Long.class);
        Long execute = stringRedisTemplate.execute(redisScript, Collections.singletonList(lockKey), value);
        return execute == 1L ? true : false;

    }
}
