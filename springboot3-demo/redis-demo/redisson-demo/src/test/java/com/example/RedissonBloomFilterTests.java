package com.example;

import org.junit.jupiter.api.Test;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 *
 * Created by hanqf on 2025/12/25 14:59.
 */

@SpringBootTest
public class RedissonBloomFilterTests {

    @Autowired
    private RedissonClient redissonClient;

    @Test
    void bloomFilter() {
        //使用Redisson提供的BloomFilter工具类
        RBloomFilter<Object> bloomFilter = redissonClient.getBloomFilter("bloomFilter");
        bloomFilter.tryInit(1000000, 0.01);
        bloomFilter.add("test");
        System.out.println(bloomFilter.contains("test"));
        System.out.println(bloomFilter.count()); // 获取当前布隆过滤器中已添加的元素个数
        System.out.println(bloomFilter.getSize()); // 获取当前布隆过滤器占用内存的大小，单位bit
    }

}
