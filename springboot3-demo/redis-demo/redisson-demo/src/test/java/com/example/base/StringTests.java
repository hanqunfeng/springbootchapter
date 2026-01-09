package com.example.base;

import org.junit.jupiter.api.Test;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * String — Redisson 操作对象与代码示例
 * Created by hanqf on 2026/1/9 14:36.
 */

@SpringBootTest
public class StringTests {
    @Autowired
    private RedissonClient redisson;

    /**
     * RBucket<T> —— 单值对象（String / Object）
     * <p>
     * 适用场景: 配置缓存、Token、小对象缓存、状态标记
     */
    @Test
    void testRBucket() throws InterruptedException {
        RBucket<String> bucket = redisson.getBucket("user:name");

        // 写入 -- SET user:name Tom
        bucket.set("Tom");

        // 读取 -- GET user:name
        String value = bucket.get();
        System.out.println(value);   // Tom

        // 写入的同时设置 TTL -- SETEX user:name 10 Alice
        bucket.set("Alice", Duration.ofSeconds(10));
        // 等待5秒
        TimeUnit.SECONDS.sleep(5);
        // 获取到期时间 -- EXPIRETIME user:name
        System.out.println(bucket.getExpireTime());
        // 查看剩余 TTL ms
        System.out.println(bucket.remainTimeToLive());

        // 写入的同时保留 TTL -- SET user:name Jerry KEEPTTL
        bucket.setAndKeepTTL("Jerry");
        // 获取到期时间 -- EXPIRETIME user:name
        System.out.println(bucket.getExpireTime());
        // 查看剩余 TTL
        System.out.println(bucket.remainTimeToLive());

        // CAS（原子替换）-- Lua 脚本 实现，此时忽略 TTL
        boolean success = bucket.compareAndSet("Jerry", "Bob");
        System.out.println("updated = " + success);
        // 获取到期时间 -- EXPIRETIME user:name
        System.out.println(bucket.getExpireTime());
        // 查看剩余 TTL
        System.out.println(bucket.remainTimeToLive());
        System.out.println(bucket.get());

        // 删除 -- DEL user:name
        bucket.delete();
    }

    /**
     * RAtomicLong —— 分布式原子计数器
     * <p>
     * 对应 Redis：String
     * 对应命令：INCR / INCRBY
     * 适用场景: 全局序列号、计数器、QPS 统计、分布式 ID 前缀
     */
    @Test
    void testRAtomicLong() {
        RAtomicLong counter = redisson.getAtomicLong("order:seq");

        // 初始化（仅第一次有效）
        counter.compareAndSet(0, 1000);

        // +1
        long v1 = counter.incrementAndGet();
        System.out.println(v1);

        // +10
        long v2 = counter.addAndGet(10);
        System.out.println(v2);

        // 获取当前值
        long current = counter.get();
        System.out.println(current);

    }

    /**
     * RAtomicDouble —— 分布式浮点原子变量
     * <p>
     * 对应 Redis：String
     * Redis 本身不支持浮点原子操作，Redisson 底层通过 Lua 实现原子浮点运算
     * 性能略低于 RAtomicLong
     * 适用场景: 权重累计、浮点指标统计、评分系统
     */
    @Test
    void testRAtomicDouble() {
        RAtomicDouble score = redisson.getAtomicDouble("user:score");

        // 初始化
        score.set(1.5);

        // +2.3
        double result = score.addAndGet(2.3);
        System.out.println(result);   // 3.8

        // 获取当前值
        double current = score.get();
        System.out.println(current);
    }

    /**
     * RLongAdder —— 高并发计数器（分片累加）
     * <p>
     * 对应 Redis：Hash + Lua
     * 类似 Java LongAdder，降低热点竞争
     * <p>
     * 适用场景: 高并发指标统计、PV / UV 计数、秒级聚合
     * <p>
     * 与 RAtomicLong 的区别
     * | 对比项   | RAtomicLong | RLongAdder |
     * | ----- | ----------- | ---------- |
     * | 并发性能  | 中           | 高          |
     * | 精确实时值 | 是           | 近实时        |
     * | 内部结构  | 单 Key       | 分片 Hash    |
     * | 汇总成本  | O(1)        | O(n)       |
     */
    @Test
    void testRLongAdder() {
        RLongAdder adder = redisson.getLongAdder("metrics:pv");

        // 并发安全递增
        adder.increment();
        adder.add(5);

        // 汇总结果（会合并所有分片）
        long total = adder.sum();
        System.out.println(total);

        // 重置
        adder.reset();

    }

    /**
     * RBitSet —— 位集合（Bitmap）
     * <p>
     * 对应 Redis：String Bitmap
     * 对应命令：SETBIT / GETBIT
     * <p>
     * 适用场景: 用户在线状态、签到、布尔标记矩阵、权限位图
     */
    @Test
    void testRBitSet() {
        RBitSet bitSet = redisson.getBitSet("user:online");

        // 设置 bit
        bitSet.set(1001, true);
        bitSet.set(1002, true);

        // 读取 bit
        boolean online = bitSet.get(1001);
        System.out.println(online);   // true

        // 统计 bit 数量
        long count = bitSet.cardinality();
        System.out.println("online users = " + count);


        RBitSet other = redisson.getBitSet("user:vip");
        other.set(1001, true);

        // AND / OR / XOR
        // 正确做法，创建一个临时 RBitSet 对象
        RBitSet tmp = redisson.getBitSet("tmp:online_and_vip");
        // BITOP AND tmp:online_and_vip user:online user:vip
        // 返回结果是 tmp:online_and_vip 所占的字节数, 公式：byteSize = (maxBitIndex + 1 + 7) / 8
        // 并集运算后存储在 tmp:online_and_vip 中的是 1001，(1001 +1 + 7)/8 = 1010 / 8 = 126.25 → 126
        final long and = tmp.and("user:online", "user:vip");
        System.out.println("and byte size = " + and);
    }

    /**
     * RBinaryStream —— 二进制流（大对象）
     * <p>
     * 对应 Redis：String
     * <p>
     * 适用场景: 小文件缓存、二进制片段、临时对象存储
     */
    @Test
    void testRBinaryStream() throws IOException {
        RBinaryStream stream = redisson.getBinaryStream("file:chunk");

        // 写入数据 -- SET file:chunk hello world
        byte[] data = "hello world".getBytes();
        stream.set(data);
        // 读取数据 -- GET file:chunk
        byte[] read = stream.get();
        System.out.println(new String(read));

        // Redisson 没有提供对应的命令，这里使用 InputStream 模拟
        // 读取 [0, 4] -> "hello" -- GETRANGE file:chunk 0 4
        byte[] part = stream.getInputStream().readNBytes(5);
        System.out.println(new String(part));

        // 读取 [6, 10] -> "world" -- GETRANGE file:chunk 6 10
        InputStream inputStream = stream.getInputStream();
        inputStream.skipNBytes(6);
        System.out.println(new String(inputStream.readNBytes(5)));

        // 追加写入数据 -- APPEND file:chunk hello world
        try (OutputStream out = stream.getOutputStream()) {
            out.write("hello ".getBytes());
            out.write("world".getBytes());
        }
        // 读取数据 -- GET file:chunk
        try (InputStream in = stream.getInputStream()) {
            byte[] buffer = new byte[1024];
            int len = in.read(buffer);

            byte[] data2 = Arrays.copyOf(buffer, len);
            System.out.println(new String(data2));
        }


        read = stream.get();
        System.out.println(new String(read));



    }
}
