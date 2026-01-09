package com.example.base;

import org.junit.jupiter.api.Test;
import org.redisson.api.RSet;
import org.redisson.api.RSetCache;
import org.redisson.api.RSortedSet;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

/**
 * Set — Redisson 操作对象与代码示例
 * Created by hanqf on 2026/1/9 18:06.
 */

@SpringBootTest
public class SetTests {

    @Autowired
    private RedissonClient redisson;

    /**
     * RSet<V> —— 无序集合
     * <p>
     * 对应 Redis：Set
     * 语义：Java Set
     * 适用场景: 去重、缓存、去重缓存
     */
    @Test
    void testRSet() {
        RSet<String> set = redisson.getSet("set:demo", StringCodec.INSTANCE);

        // add -> SADD
        set.add("A");
        set.add("B");
        set.add("C");
        set.add("A"); // 自动去重

        // contains -> SISMEMBER
        System.out.println(set.contains("B"));  // true

        // remove -> SREM
        set.remove("C");

        // size -> SCARD
        System.out.println("size = " + set.size());

        // 遍历 -> SMEMBERS / SSCAN
        for (String v : set) {
            System.out.println(v);
        }

    }


    /**
     * RSetCache<V> —— 带 TTL 的 Set
     * <p>
     * 扩展能力：元素级过期时间
     * Redis 原生 Set 不支持 member TTL
     */
    @Test
    void testRSetCache() throws InterruptedException {
        RSetCache<String> cache =
                redisson.getSetCache("set:cache");

        // 添加元素并设置 TTL
        cache.add("token1", 10, TimeUnit.SECONDS);
        cache.add("token2", 30, TimeUnit.SECONDS);

        // 判断是否存在
        System.out.println(cache.contains("token1"));
        // 没有获取元素 TTL 的方法

        // 等待过期
        TimeUnit.SECONDS.sleep(11);

        // 自动清理
        System.out.println(cache.contains("token1")); // false

        // 遍历 -> SMEMBERS / SSCAN
        for (String v : cache) {
            System.out.println(v);
        }
    }


    /**
     * RSortedSet<V> —— 有序集合（基于 ZSet）
     * <p>
     * 对应 Redis：ZSet
     * 按 score 排序
     */
    @Test
    void testRSortedSet() {
        RSortedSet<String> sortedSet =
                redisson.getSortedSet("zset:demo", StringCodec.INSTANCE);
        sortedSet.clear();

        // 添加（score 自动生成）
        sortedSet.add("Alice");
        sortedSet.add("Bob");
        sortedSet.add("Carol");

        // 获取第一个（最小 score）
        System.out.println(sortedSet.first());

        // 获取最后一个（最大 score）
        System.out.println(sortedSet.last());

        // 遍历（按 score 顺序）
        for (String v : sortedSet) {
            System.out.println(v);
        }

    }


}
