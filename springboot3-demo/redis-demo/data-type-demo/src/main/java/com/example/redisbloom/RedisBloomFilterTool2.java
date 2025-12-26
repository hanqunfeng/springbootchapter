package com.example.redisbloom;

/**
 * 基于 RedisBloom 插件的 BloomFilter 实现
 * https://github.com/RedisBloom/RedisBloom/releases
 * <p>
 * 不想安装插件也可以使用 Redission 的 BloomFilter
 */


import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Slf4j
public class RedisBloomFilterTool2 {

    private final StringRedisTemplate redisTemplate;

    public RedisBloomFilterTool2(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 初始化 BloomFilter
     * <p>
     * 不能重复创建
     *
     * @param key       BloomFilter 名称
     * @param errorRate 错误率，比如为0.01，即 1%
     * @param capacity  容量，比如为1000
     */
    public boolean reserve(String key, double errorRate, long capacity) {
        String script = "return redis.call('BF.RESERVE', KEYS[1], " + errorRate + ", " + capacity + ")";
        try {
            redisTemplate.execute(
                    new DefaultRedisScript<>(script, String.class),
                    Collections.singletonList(key)
            );
            return true;
        } catch (Exception e) {
            log.error("RedisBloomFilterTool reserve error:", e);
            return false;
        }
    }

    /**
     * 添加元素到 BloomFilter，BloomFilter 不存在会自动创建
     */
    public boolean add(String key, String value) {
        // 使用 RedisModule 提供的 BF.ADD 命令
        String script = "return redis.call('BF.ADD', KEYS[1], ARGV[1])";
        final Boolean execute = redisTemplate.execute(
                new DefaultRedisScript<>(script, Boolean.class),
                Collections.singletonList(key),
                value
        );
        return Boolean.TRUE.equals(execute);
    }

    /**
     * 判断元素是否存在
     */
    public boolean exists(String key, String value) {
        String script = "return redis.call('BF.EXISTS', KEYS[1], ARGV[1])";
        final Boolean execute = redisTemplate.execute(
                new DefaultRedisScript<>(script, Boolean.class),
                Collections.singletonList(key),
                value
        );
        return Boolean.TRUE.equals(execute);
    }

    /**
     * 批量添加
     * @return 添加结果列表，成功 1，失败 0
     */
    public List<Long> addBatch(String key, String... items) {
        String script = "return redis.call('BF.MADD', KEYS[1], unpack(ARGV))";
        if (items == null || items.length == 0) {
            return Collections.emptyList();
        }
        // 调用 BF.MADD 命令
        return redisTemplate.execute(
                new DefaultRedisScript<>(script, List.class),
                Collections.singletonList(key),
                items
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

        String script = "return redis.call('BF.INSERT', KEYS[1], 'CAPACITY', ARGV[1], 'ERROR', ARGV[2], 'ITEMS', unpack(ARGV, 3))";
        if (items == null || items.length == 0) {
            return Collections.emptyList();
        }

        List<Object> args = new ArrayList<>();
        args.add(String.valueOf(capacity));
        args.add(String.valueOf(errorRate));
        Collections.addAll(args, items);   // ✅ 关键点

        // 调用 BF.INSERT 命令
        return redisTemplate.execute(
                new DefaultRedisScript<>(script, List.class),
                Collections.singletonList(key),
                args.toArray()
        );
    }

    /**
     * 批量判断元素是否存在
     * @return 存在 1，不存在 0
     */
    public List<Long> mexists(String key, String... items) {

        String script = "return redis.call('BF.MEXISTS', KEYS[1], unpack(ARGV))";
        if (items == null || items.length == 0) {
            return Collections.emptyList();
        }

        // 调用 BF.MADD 命令
        return redisTemplate.execute(
                new DefaultRedisScript<>(script, List.class),
                Collections.singletonList(key),
                items
        );
    }


    /**
     * 获取元素数量
     */
    public Long card(String key) {
        String script = "return redis.call('BF.CARD', KEYS[1])";
        // 使用 RedisModule 提供的 BF.CARD 命令
        return redisTemplate.execute(
                new DefaultRedisScript<>(script, Long.class),
                Collections.singletonList(key)
        );
    }


    /**
     * 获取 Bloom Filter 元信息
     */
    public Map<String, Long> info(String key) {
        String script = "return redis.call('BF.INFO', KEYS[1])";

        List<Object> result = redisTemplate.execute(
                new DefaultRedisScript<>(script, List.class),
                Collections.singletonList(key)
        );

        if (result == null || result.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, Long> infoMap = new LinkedHashMap<>();
        for (int i = 0; i < result.size(); i += 2) {
            String field = toString(result.get(i));
            Long value = (Long) result.get(i + 1);
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

