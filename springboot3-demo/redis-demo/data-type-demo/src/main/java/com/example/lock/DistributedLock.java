package com.example.lock;

import java.util.concurrent.TimeUnit;

/**
 * DistributedLock
 * Created by hanqf on 2025/12/17 16:37.
 */


public interface DistributedLock {
    /**
     * 尝试获取锁
     *
     * @param key       锁的key
     * @param waitTime  尝试获取锁的最大等待时间
     * @param leaseTime 锁的过期时间
     * @param unit      时间单位
     * @return true表示获取锁成功，false表示获取锁失败
     */
    boolean tryLock(String key, long waitTime, long leaseTime, TimeUnit unit);

    /**
     * 尝试获取锁
     * 不等待立即返回
     * @param key       锁的key
     * @param leaseTime 锁的过期时间
     * @param unit      时间单位
     * @return true表示获取锁成功，false表示获取锁失败
     */
    boolean tryLock(String key, long leaseTime, TimeUnit unit);

    /**
     * 获取锁
     * 只要获取到锁就返回，否则一直自旋获取锁
     * @param key 锁的key
     * @param leaseTime 锁的过期时间
     * @param unit 时间单位
     * @return true表示获取锁成功，false表示获取锁失败
     */
    boolean lock(String key, long leaseTime, TimeUnit unit);

    /**
     * 释放锁
     *
     * @param key 锁的key
     */
    void unlock(String key);
}
