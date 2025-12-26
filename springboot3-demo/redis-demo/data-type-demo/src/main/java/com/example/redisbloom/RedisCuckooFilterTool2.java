package com.example.redisbloom;

/**
 * 基于 RedisBloom 插件的 CuckooFilter 实现
 * https://github.com/RedisBloom/RedisBloom/releases
 * Created by hanqf on 2025/12/22 17:09.
 */

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class RedisCuckooFilterTool2 {

    private final StringRedisTemplate redisTemplate;

    public RedisCuckooFilterTool2(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 初始化 Cuckoo Filter
     * <p>
     * 不能重复创建
     *
     * @param key      Filter 名称
     * @param capacity 预计容量
     */
    public boolean reserve(String key, long capacity) {
        String script = "return redis.call('CF.RESERVE', KEYS[1], " + capacity + ")";
        try{
            redisTemplate.execute(
                    new DefaultRedisScript<>(script, String.class),
                    Collections.singletonList(key)
            );
            return true;
        } catch (Exception e) {
            log.error("RedisCuckooFilterTool reserve error:",e);
            return false;
        }

    }

    /**
     * 初始化 Cuckoo Filter（高级参数）
     *
     * 不能重复创建
     *
     * @param key           Filter 名称
     * @param capacity      预计容量
     * @param bucketSize    每个桶里最多能放多少个 fingerprint（指纹），默认 2(大多数情况下的最优解)
     * @param maxIterations 重排次数，越大成功率越高
     * @param expansion     扩容倍数，默认 1（不扩容）
     */
    public boolean reserve(String key, long capacity, int bucketSize, int maxIterations, int expansion) {

        String script = String.format(
                "return redis.call('CF.RESERVE', KEYS[1], %d, " +
                        "'BUCKETSIZE', %d, 'MAXITERATIONS', %d, 'EXPANSION', %d)",
                capacity, bucketSize, maxIterations, expansion
        );

        try{
            redisTemplate.execute(
                    new DefaultRedisScript<>(script, String.class),
                    Collections.singletonList(key)
            );
            return true;
        } catch (Exception e) {
            log.error("RedisCuckooFilterTool reserve error:",e);
            return false;
        }
    }

    /**
     * 添加元素（不去重）
     */
    public boolean add(String key, String value) {
        String script = "return redis.call('CF.ADD', KEYS[1], ARGV[1])";
        final Boolean execute = redisTemplate.execute(
                new DefaultRedisScript<>(script, Boolean.class),
                Collections.singletonList(key),
                value
        );
        return Boolean.TRUE.equals(execute);
    }

    /**
     * 添加元素（仅当不存在时）
     *
     * @return true 表示成功插入，false 表示已存在
     */
    public boolean addNx(String key, String value) {
        String script = "return redis.call('CF.ADDNX', KEYS[1], ARGV[1])";
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
        String script = "return redis.call('CF.EXISTS', KEYS[1], ARGV[1])";
        final Boolean execute = redisTemplate.execute(
                new DefaultRedisScript<>(script, Boolean.class),
                Collections.singletonList(key),
                value
        );
        return Boolean.TRUE.equals(execute);
    }

    /**
     * 批量判断是否存在
     */
    public List<Long> mexists(String key, String... items) {
        String script = "return redis.call('CF.MEXISTS', KEYS[1], unpack(ARGV))";
        if (items == null || items.length == 0) {
            return Collections.emptyList();
        }

        return redisTemplate.execute(
                new DefaultRedisScript<>(script, List.class),
                Collections.singletonList(key),
                items
        );
    }

    /**
     * 返回元素出现次数（近似）
     */
    public Long count(String key, String value) {
        String script = "return redis.call('CF.COUNT', KEYS[1], ARGV[1])";
        return redisTemplate.execute(
                new DefaultRedisScript<>(script, Long.class),
                Collections.singletonList(key),
                value
        );
    }

    /**
     * 删除元素
     *
     * @return true 删除成功，false 表示不存在
     */
    public boolean delete(String key, String value) {
        String script = "return redis.call('CF.DEL', KEYS[1], ARGV[1])";

        final Long execute = redisTemplate.execute(
                new DefaultRedisScript<>(script, Long.class),
                Collections.singletonList(key),
                value
        );
        return Boolean.TRUE.equals(execute);
    }

    /**
     * 批量插入，不去重
     */
    public List<Long> insert(String key, String... items) {
        String script = "return redis.call('CF.INSERT', KEYS[1], 'ITEMS', unpack(ARGV))";
        if (items == null || items.length == 0) {
            return Collections.emptyList();
        }
        return redisTemplate.execute(
                new DefaultRedisScript<>(script, List.class),
                Collections.singletonList(key),
                items
        );
    }

    /**
     * 批量插入，去重
     */
    public List<Boolean> insertNx(String key, String... items) {
        String script = "return redis.call('CF.INSERTNX', KEYS[1], 'ITEMS', unpack(ARGV))";
        if (items == null || items.length == 0) {
            return Collections.emptyList();
        }
        return redisTemplate.execute(
                new DefaultRedisScript<>(script, List.class),
                Collections.singletonList(key),
                items
        );
    }

    /**
     * 获取 Cuckoo Filter 元信息
     */
    public Map<String, Long> info(String key) {
        String script = "return redis.call('CF.INFO', KEYS[1])";
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

    /**
     * 字节数组转字符串
     * info 返回的 List
     * [
     * byte[]("Size"),                  Long(1080),
     * byte[]("Number of buckets"),     Long(512),
     * byte[]("Number of filters"),     Long(1),
     * ...
     * ]
     */
    private String toString(Object obj) {
        if (obj instanceof byte[]) {
            return new String((byte[]) obj);
        }
        return String.valueOf(obj);
    }
}

