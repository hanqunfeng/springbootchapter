package com.example.base;

import org.junit.jupiter.api.Test;
import org.redisson.api.RHyperLogLog;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * HyperLogLog — Redisson 对象与实现解析
 * Created by hanqf on 2026/1/9 18:06.
 */

@SpringBootTest
public class HyperLogLogTests {

    @Autowired
    private RedissonClient redisson;

    /**
     * RHyperLogLog —— 近似去重计数
     */
    @Test
    void testRHyperLogLog() {
        RHyperLogLog<String> hll =
                redisson.getHyperLogLog("uv:2026-01", StringCodec.INSTANCE);

        // 添加访问用户
        // PFADD uv:2026-01 user:1001 user:1002 user:1001
        hll.add("user:1001");
        hll.add("user:1002");
        hll.add("user:1001");   // 重复无影响

        // 统计 UV: PFCOUNT uv:2026-01
        long uv = hll.count();
        System.out.println("UV = " + uv);

        // 合并多个统计周期
        RHyperLogLog<String> hll2 =
                redisson.getHyperLogLog("uv:2026-02");
        hll2.add("user:1001");
        hll2.add("user:1002");

        // 合并
        RHyperLogLog<String> mergedHll =
                redisson.getHyperLogLog("uv:2026");
        // PFMERGE uv:2026 uv:2026-01 uv:2026-02
        long merged = mergedHll.countWith("uv:2026-01", "uv:2026-02");
        System.out.println("merged uv = " + merged);


    }



}
