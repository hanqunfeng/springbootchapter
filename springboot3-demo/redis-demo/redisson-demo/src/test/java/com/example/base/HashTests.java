package com.example.base;

import org.junit.jupiter.api.Test;
import org.redisson.api.*;
import org.redisson.client.codec.StringCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Hash — Redisson 操作对象与代码示例
 * Created by hanqf on 2026/1/9 16:24.
 */

@SpringBootTest
public class HashTests {

    @Autowired
    private RedissonClient redisson;

    /**
     * RMap<K,V> —— 分布式 Map（Redis Hash）
     * <p>
     * 对应 Redis：Hash
     * 语义：Java ConcurrentMap
     * 核心用途：结构化 KV 存储
     */
    @Test
    void testRMap() {
        // 创建 Hash，使用 StringCodec 进行键值对编码，否则存储到 Redis 时，会出现乱码
        RMap<String, String> userMap = redisson.getMap("user:1001", StringCodec.INSTANCE);

        userMap.clear();

        // put -> HSET user:1001 name "Tom" age 18
        userMap.put("name", "Tom");
        userMap.put("age", "18");

        // get -> HGET user:1001 name
        String name = userMap.get("name");
        System.out.println("name = " + name);

        // remove -> HDEL user:1001 age
        userMap.remove("age");

        // size -> HLEN user:1001
        System.out.println("size = " + userMap.size());

        // 判断字段是否存在 -> HEXISTS user:1001 name
        boolean exists = userMap.containsKey("name");
        System.out.println("exists = " + exists);

        // 获取所有字段 -> HGETALL user:1001
        Map<String, String> map = userMap.readAllMap();
        System.out.println("map = " + map);

    }

    /**
     * RMapCache<K,V> —— 带 TTL 的分布式 Map（Redis Hash）
     * <p>
     * 对应 Redis：Hash
     * 语义：Java ConcurrentMap
     * 缓存用途：结构化 KV 存储
     * <p>
     * 扩展能力：字段级 TTL
     * Redis 原生 Hash 不支持 field TTL
     * Redis 7.4+ 新增 HPEXPIRE/HTTL 命令，支持 field TTL
     */
    @Test
    void testRMapCache() throws InterruptedException {
        // 加不加 StringCodec 都是乱码，这是 RMapCache 底层实现决定的
        RMapCache<String, String> cache =
                redisson.getMapCache("session:token", StringCodec.INSTANCE);
        cache.clear();

        // 设置 key 10 秒过期
        cache.put("token1", "abc", 10, TimeUnit.SECONDS);

        // 永不过期
        cache.put("token2", "xyz");

        // 获取
        System.out.println(cache.get("token1"));

        // 查看剩余 TTL，ms
        long ttl = cache.remainTimeToLive("token1");
        System.out.println("ttl(ms) = " + ttl);

        // 等待过期
        TimeUnit.SECONDS.sleep(11);

        // 已自动过期
        System.out.println(cache.get("token1")); // null

    }

    /**
     * RLocalCachedMap<K,V> —— 本地缓存 Map（两级缓存）
     * Redisson PRO version
     * <p>
     * 对应 Redis：Hash
     * 语义：Local JVM Cache (Caffeine / ConcurrentHashMap)
     * 缓存用途：结构化 KV 存储
     */
    @Test
    void testRLocalCachedMap() {
        LocalCachedMapCacheOptions<String, String> options =
                LocalCachedMapCacheOptions.<String, String>defaults()
                        .cacheSize(1_000) // 缓存大小
                        .evictionPolicy(LocalCachedMapCacheOptions.EvictionPolicy.LRU) // 缓存策略, LRU
                        .syncStrategy(LocalCachedMapCacheOptions.SyncStrategy.UPDATE) // 同步策略，UPDATE
                        .reconnectionStrategy(LocalCachedMapCacheOptions.ReconnectionStrategy.CLEAR); // 重连策略，CLEAR

        RLocalCachedMap<String, String> localMap =
                redisson.getLocalCachedMapCache("config:global", StringCodec.INSTANCE, options);

        // 首次读取 -> Redis
        String v1 = localMap.get("timeout");
        System.out.println("v1 = " + v1);

        // 写入 -> Redis + 本地缓存
        localMap.put("timeout", "30s");

        // 再次读取 -> 本地缓存（无网络 IO）
        String v2 = localMap.get("timeout");
        System.out.println("v2 = " + v2);

    }
}
