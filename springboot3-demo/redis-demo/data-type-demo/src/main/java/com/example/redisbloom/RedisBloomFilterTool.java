package com.example.redisbloom;

/**
 * 基于 RedisBloom 插件的 BloomFilter 实现
 * https://github.com/RedisBloom/RedisBloom/releases
 * <p>
 * 不想安装插件也可以使用 Redission 的 BloomFilter
 */


import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class RedisBloomFilterTool {

    private final StringRedisTemplate redisTemplate;

    public RedisBloomFilterTool(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 初始化 BloomFilter
     * <p>
     * 不能重复执行
     *
     * @param key       BloomFilter 名称
     * @param errorRate 错误率，比如为0.01，即 1%
     * @param capacity  容量，比如为1000
     */
    public void reserve(String key, double errorRate, long capacity) {
        redisTemplate.execute((RedisCallback<String>) connection ->
                connection.scriptingCommands().eval(
                        ("return redis.call('BF.RESERVE', KEYS[1], " + errorRate + ", " + capacity + ")").getBytes(),
                        ReturnType.STATUS,
                        1,
                        key.getBytes()
                )
        );
    }

    /**
     * 添加元素到 BloomFilter，BloomFilter 不存在会自动创建
     */
    public boolean add(String key, String value) {
        // 使用 RedisModule 提供的 BF.ADD 命令
        return Boolean.TRUE.equals(redisTemplate.execute((RedisCallback<Boolean>) connection ->
                connection.scriptingCommands().eval(
                        ("return redis.call('BF.ADD', KEYS[1], ARGV[1])").getBytes(),
                        ReturnType.BOOLEAN,
                        1,
                        key.getBytes(),
                        value.getBytes()
                )
        ));
    }


    /**
     * 判断元素是否存在
     */
    public boolean exists(String key, String value) {
        return Boolean.TRUE.equals(redisTemplate.execute((RedisCallback<Boolean>) connection ->
                connection.scriptingCommands().eval(
                        ("return redis.call('BF.EXISTS', KEYS[1], ARGV[1])").getBytes(),
                        ReturnType.BOOLEAN,
                        1,
                        key.getBytes(),
                        value.getBytes()
                )
        ));
    }

    /**
     * 批量添加
     */
    public List<Long> addBatch(String key, String... items) {
        if (items == null || items.length == 0) {
            return Collections.emptyList();
        }

        // 构建 keys + args 数组
        byte[][] keysAndArgs = new byte[1 + items.length][];
        keysAndArgs[0] = key.getBytes();  // KEYS[1]
        for (int i = 0; i < items.length; i++) {
            keysAndArgs[i + 1] = items[i].getBytes(); // ARGV[1..n]
        }

        // 调用 BF.MADD 命令
        return redisTemplate.execute((RedisCallback<List<Long>>) connection ->
                connection.scriptingCommands().eval(
                        ("return redis.call('BF.MADD', KEYS[1], unpack(ARGV))").getBytes(),
                        ReturnType.MULTI,
                        1,                // numKeys
                        keysAndArgs       // KEYS + ARGV
                )
        );
    }

    /**
     * 批量添加，如果BloomFilter不存在，则根据参数创建 BloomFilter，若已存在，则忽略 capacity 和 errorRate
     *
     * @param key       BloomFilter 名称
     * @param capacity  容量
     * @param errorRate 错误率
     * @param items     要添加的元素
     * @return 添加结果列表，成功 1，失败 0
     */
    public List<Long> insert(String key, long capacity, double errorRate, String... items) {

        if (items == null || items.length == 0) {
            return Collections.emptyList();
        }

        // 构建 keys + args 数组
        byte[][] keysAndArgs = new byte[6 + items.length][];
        keysAndArgs[0] = key.getBytes();  // KEYS[1]
        keysAndArgs[1] = "CAPACITY".getBytes();
        keysAndArgs[2] = String.valueOf(capacity).getBytes();
        keysAndArgs[3] = "ERROR".getBytes();
        keysAndArgs[4] = String.valueOf(errorRate).getBytes();
        keysAndArgs[5] = "ITEMS".getBytes();
        for (int i = 0; i < items.length; i++) {
            keysAndArgs[i + 6] = items[i].getBytes(); // ARGV[1..n]
        }

        // 调用 BF.INSERT 命令
        return redisTemplate.execute((RedisCallback<List<Long>>) connection ->
                connection.scriptingCommands().eval(
                        ("return redis.call('BF.INSERT', KEYS[1], unpack(ARGV))").getBytes(),
                        ReturnType.MULTI,
                        1,                // numKeys
                        keysAndArgs       // KEYS + ARGV
                )
        );
    }

    /**
     * 批量判断元素是否存在
     */
    public List<Long> mexists(String key, String... items) {
        if (items == null || items.length == 0) {
            return Collections.emptyList();
        }

        // 构建 keys + args 数组
        byte[][] keysAndArgs = new byte[1 + items.length][];
        keysAndArgs[0] = key.getBytes();  // KEYS[1]
        for (int i = 0; i < items.length; i++) {
            keysAndArgs[i + 1] = items[i].getBytes(); // ARGV[1..n]
        }

        // 调用 BF.MADD 命令
        return redisTemplate.execute((RedisCallback<List<Long>>) connection ->
                connection.scriptingCommands().eval(
                        ("return redis.call('BF.MEXISTS', KEYS[1], unpack(ARGV))").getBytes(),
                        ReturnType.MULTI,
                        1,                // numKeys
                        keysAndArgs       // KEYS + ARGV
                )
        );
    }


    /**
     * 获取元素数量
     */
    public Long card(String key) {
        return redisTemplate.execute((RedisCallback<Long>) connection ->
                connection.scriptingCommands().eval(
                        ("return redis.call('BF.CARD', KEYS[1])").getBytes(),
                        ReturnType.INTEGER,
                        1,
                        key.getBytes()
                )
        );
    }


    /**
     * 获取 Bloom Filter 元信息
     */
    public Map<String, Object> info(String key) {
        List<Object> result = redisTemplate.execute(
                (RedisCallback<List<Object>>) connection ->
                        connection.scriptingCommands().eval(
                                "return redis.call('BF.INFO', KEYS[1])".getBytes(),
                                ReturnType.MULTI,
                                1,
                                key.getBytes()
                        )
        );

        if (result == null || result.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, Object> infoMap = new LinkedHashMap<>();
        for (int i = 0; i < result.size(); i += 2) {
            String field = toString(result.get(i));
            Object value = result.get(i + 1);
            infoMap.put(field, value);
        }

        return infoMap;
    }

    private String toString(Object obj) {
        if (obj instanceof byte[]) {
            return new String((byte[]) obj);
        }
        return String.valueOf(obj);
    }


}

