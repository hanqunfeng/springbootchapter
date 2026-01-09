package com.example.base;

import org.junit.jupiter.api.Test;
import org.redisson.api.RBitSet;
import org.redisson.api.RLongAdder;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Bitmap — Redisson 对象与实现解析
 * Created by hanqf on 2026/1/9 18:06.
 */

@SpringBootTest
public class BitmapTests {

    @Autowired
    private RedissonClient redisson;

    /**
     * RBitSet —— 分布式 BitSet
     * <p>
     * 对应 Java：java.util.BitSet
     * 对应 Redis：String Bitmap（SETBIT / GETBIT）
     */
    @Test
    void testRBitSet() {
        RBitSet bitSet = redisson.getBitSet("user:online");

        // 设置位: SETBIT user:online 100 1
        bitSet.set(100);        // userId = 100 上线
        bitSet.set(200, true);
        // 清除位: SETBIT user:online 100 0
        bitSet.clear(100);

        // 读取位: GETBIT user:online 200
        boolean online = bitSet.get(200);

        // 批量操作
        bitSet.set(1000, 2000); // 区间置 1
        // 统计 1 的个数: BITCOUNT user:online
        long count = bitSet.cardinality(); // 统计 1 的个数
        System.out.println("online users = " + count);

    }


    /**
     * RLongAdder —— 高并发分片计数器
     * <p>
     * 目标：解决单 Key 原子 INCR 的热点瓶颈
     * 思想：分片 Bitmap + CAS 聚合
     */
    @Test
    void testRLongAdder() throws InterruptedException {
        RLongAdder counter =
                redisson.getLongAdder("pv:counter");

        counter.increment();
        counter.add(5);

        long value = counter.sum();
        System.out.println("value = " + value);

    }

}
