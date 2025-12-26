package com.example.lock;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * RedisReentrantLock
 * Created by hanqf on 2025/12/17 16:25.
 */

@Component
public class RedisReentrantLock implements DistributedLock {

    /**
     * 锁的key前缀
     */
    private static final String LOCK_PREFIX = "lock:";

    /**
     * 锁的Lua脚本
     * KEYS[1] 锁key
     * ARGV[1] ownerId (uuid:threadId)
     * ARGV[2] expireMillis 过期时间
     * <p>
     * 说明：
     * 1.key 不存在时创建锁
     * 2.key 存在时判断锁的拥有者是否为当前线程
     */
    private static final String LOCK_SCRIPT = """
            if (redis.call('exists', KEYS[1]) == 0) then
                redis.call('hset', KEYS[1], ARGV[1], 1)
                redis.call('pexpire', KEYS[1], ARGV[2])
                return 1
            end
            if (redis.call('hexists', KEYS[1], ARGV[1]) == 1) then
                redis.call('hincrby', KEYS[1], ARGV[1], 1)
                redis.call('pexpire', KEYS[1], ARGV[2])
                return 1
            end
            return 0
            """;

    /**
     * 释放锁的Lua脚本
     * KEYS[1] 锁key
     * ARGV[1] ownerId (uuid:threadId)
     */
    private static final String UNLOCK_SCRIPT = """
            if (redis.call('hexists', KEYS[1], ARGV[1]) == 0) then
                return 0
            end
            local count = redis.call('hincrby', KEYS[1], ARGV[1], -1)
            if (count > 0) then
                return 1
            else
                redis.call('hdel', KEYS[1], ARGV[1])
                if (redis.call('hlen', KEYS[1]) == 0) then
                    redis.call('del', KEYS[1])
                end
                return 1
            end
            """;

    private final StringRedisTemplate redisTemplate;
    private final RedisLockWatchDog watchDog;
    private final String uuid = UUID.randomUUID().toString();

    public RedisReentrantLock(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        watchDog = new RedisLockWatchDog(redisTemplate);
    }

    /**
     * 获取当前线程的 ownerId
     *
     * @return
     */
    private String ownerId() {
        return uuid + ":" + Thread.currentThread().getId();
    }


    @Override
    public boolean tryLock(String key, long leaseTime, TimeUnit unit) {
        String lockKey = LOCK_PREFIX + key;
        long expireMillis = unit.toMillis(leaseTime);
        Boolean success = redisTemplate.execute(
                new DefaultRedisScript<>(LOCK_SCRIPT, Boolean.class),
                Collections.singletonList(lockKey),
                ownerId(),
                String.valueOf(expireMillis)
        );
        if (Boolean.TRUE.equals(success)) {//防止NullPointerException
            // 启动 Watch Dog（只有在 leaseTime 不确定时）
            watchDog.startRenew(
                    lockKey,
                    ownerId(),
                    expireMillis
            );
            return true;
        }
        return false;
    }

    @Override
    public boolean tryLock(String key, long waitTime, long leaseTime, TimeUnit unit) {
        long deadline = System.currentTimeMillis() + unit.toMillis(waitTime);
        // 使用带条件的循环，避免重复赋值
        while (System.currentTimeMillis() < deadline) {
            if (tryLock(key, leaseTime, unit)) {
                return true;
            } else {
                // 让当前线程挂起（阻塞），最长不超过指定的纳秒数
                // 在竞争失败后，让出 CPU 一小段时间，避免忙等，同时控制对 Redis 的重试频率。
                // 这里50毫秒是经验值，可以根据实际需求调整
                LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(50));
            }
        }
        return false;
    }

    @Override
    public boolean lock(String key, long leaseTime, TimeUnit unit) {

        // 使用无限循环，语义更清晰
        while (true) {
            if (tryLock(key, leaseTime, unit)) {
                return true;
            } else {
                // 让当前线程挂起（阻塞），最长不超过指定的纳秒数
                // 在竞争失败后，让出 CPU 一小段时间，避免忙等，同时控制对 Redis 的重试频率。
                // 这里50毫秒是经验值，可以根据实际需求调整
                LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(50));
            }
        }
    }

    @Override
    public void unlock(String key) {
        String lockKey = LOCK_PREFIX + key;
        // 先停续期
        watchDog.stopRenew(lockKey);
        // 释放锁
        redisTemplate.execute(
                new DefaultRedisScript<>(UNLOCK_SCRIPT, Long.class),
                Collections.singletonList(lockKey),
                ownerId()
        );
    }


}
