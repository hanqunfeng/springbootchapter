package com.example.lock;

/**
 * RedisLockWatchDog
 * Created by hanqf on 2025/12/17 17:13.
 */


import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.*;

public class RedisLockWatchDog {

    /**
     * WatchDog的Lua脚本
     * KEYS[1] 锁key
     * ARGV[1] ownerId (uuid:threadId)
     * ARGV[2] expireMillis 过期时间
     * <p>
     * 说明：
     * 拥有者是当前线程就续期
     */
    private static final String WATCHDOG_SCRIPT = """
            if (redis.call('hexists', KEYS[1], ARGV[1]) == 1) then
                redis.call('pexpire', KEYS[1], ARGV[2])
                return 1
            end
            return 0
            """;

    private final StringRedisTemplate redisTemplate;

    /**
     * 单线程足够（Redisson 也是）
     */
    private final ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r);
                t.setName("redis-lock-watch-dog");
                t.setDaemon(true);
                return t;
            });

    /**
     * 每个 lockKey 对应一个续期任务
     */
    private final Map<String, ScheduledFuture<?>> renewTasks = new ConcurrentHashMap<>();

    public RedisLockWatchDog(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 启动续期
     *
     * @param lockKey     Redis 锁 key
     * @param ownerId     uuid:threadId
     * @param leaseMillis 锁过期时间
     */
    public void startRenew(String lockKey, String ownerId, long leaseMillis) {

        // 防止重复启动
        if (renewTasks.containsKey(lockKey)) {
            return;
        }

        // 间隔多久续期一次
        long period = leaseMillis / 3;

        ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(() -> {
            try {
                renew(lockKey, ownerId, leaseMillis);
            } catch (Exception e) {
                // 生产环境建议接日志
            }
        }, period, period, TimeUnit.MILLISECONDS);

        renewTasks.put(lockKey, future);
    }

    /**
     * 取消续期
     */
    public void stopRenew(String lockKey) {
        ScheduledFuture<?> future = renewTasks.remove(lockKey);
        if (future != null) {
            future.cancel(false);
        }
    }

    /**
     * 实际续期逻辑
     */
    private void renew(String lockKey, String ownerId, long leaseMillis) {

        Boolean result = redisTemplate.execute(
                new DefaultRedisScript<>(WATCHDOG_SCRIPT, Boolean.class),
                Collections.singletonList(lockKey),
                ownerId,
                String.valueOf(leaseMillis)
        );

        if (!result) {
            // 锁已不属于当前线程，停止 Watch Dog
            stopRenew(lockKey);
        }
    }

    /**
     * 应用关闭时释放资源（可选）
     */
    public void shutdown() {
        scheduler.shutdown();
    }
}

