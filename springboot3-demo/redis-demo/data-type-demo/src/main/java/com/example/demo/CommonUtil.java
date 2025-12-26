package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * 公共方法
 * Created by hanqf on 2025/12/15 17:25.
 */


public class CommonUtil {
    @Autowired
    protected RedisTemplate<String, Object> redisTemplate;

    /**
     * 为key 设置过期时间
     *
     * @param key     键
     * @param seconds 过期时间（秒）
     */
    public void expire(String key, long seconds) {
        redisTemplate.expire(key, seconds, TimeUnit.SECONDS);
    }

    /**
     * 获取 key 的剩余的过期时间
     * 对应 Redis 命令: TTL key
     *
     * @param key 键
     * @return 剩余的过期时间（秒）
     */
    public Long ttl(String key) {
        return redisTemplate.getExpire(key);
    }

    /**
     * 清除 key 的过期时间，使其永不过期
     * 对应 Redis 命令: PERSIST key
     *
     * @param key 键
     * @return 是否成功
     */
    public Boolean persist(String key) {
        return redisTemplate.persist(key);
    }


    /**
     * 检查给定 key 是否存在
     * 对应 Redis 命令: EXISTS key
     *
     * @param key 键
     * @return 是否存在
     */
    public Boolean exists(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 删除已存在的键
     * 对应 Redis 命令: DEL key
     *
     * @param key 键
     * @return 删除的键数量
     */
    public Boolean del(String key) {
        return redisTemplate.delete(key);
    }

}
