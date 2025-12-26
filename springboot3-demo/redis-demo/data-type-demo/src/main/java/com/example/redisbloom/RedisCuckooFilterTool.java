package com.example.redisbloom;

/**
 * 基于 RedisBloom 插件的 CuckooFilter 实现
 * https://github.com/RedisBloom/RedisBloom/releases
 * Created by hanqf on 2025/12/22 17:09.
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
public class RedisCuckooFilterTool {

    private final StringRedisTemplate redisTemplate;

    public RedisCuckooFilterTool(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 初始化 Cuckoo Filter
     * <p>
     * 不能重复执行
     *
     * @param key      Filter 名称
     * @param capacity 预计容量
     */
    public void reserve(String key, long capacity) {
        redisTemplate.execute((RedisCallback<String>) connection ->
                connection.scriptingCommands().eval(
                        ("return redis.call('CF.RESERVE', KEYS[1], " + capacity + ")").getBytes(),
                        ReturnType.STATUS,
                        1,
                        key.getBytes()
                )
        );
    }

    /**
     * 初始化 Cuckoo Filter（高级参数）
     *
     * @param key           Filter 名称
     * @param capacity      预计容量
     * @param bucketSize    每个桶里最多能放多少个 fingerprint（指纹），默认 2(大多数情况下的最优解)
     * @param maxIterations 重排次数，越大成功率越高
     * @param expansion     扩容倍数，默认 1（不扩容）
     */
    public void reserve(String key, long capacity, int bucketSize, int maxIterations, int expansion) {

        String script = String.format(
                "return redis.call('CF.RESERVE', KEYS[1], %d, " +
                        "'BUCKETSIZE', %d, 'MAXITERATIONS', %d, 'EXPANSION', %d)",
                capacity, bucketSize, maxIterations, expansion
        );

        redisTemplate.execute((RedisCallback<String>) connection ->
                connection.scriptingCommands().eval(
                        script.getBytes(),
                        ReturnType.STATUS,
                        1,
                        key.getBytes()
                )
        );
    }

    /**
     * 添加元素（不去重）
     */
    public boolean add(String key, String value) {
        return Boolean.TRUE.equals(redisTemplate.execute((RedisCallback<Boolean>) connection ->
                connection.scriptingCommands().eval(
                        ("return redis.call('CF.ADD', KEYS[1], ARGV[1])").getBytes(),
                        ReturnType.BOOLEAN,
                        1,
                        key.getBytes(),
                        value.getBytes()
                )
        ));
    }

    /**
     * 添加元素（仅当不存在时）
     *
     * @return true 表示成功插入，false 表示已存在
     */
    public boolean addNx(String key, String value) {
        return Boolean.TRUE.equals(redisTemplate.execute((RedisCallback<Boolean>) connection ->
                connection.scriptingCommands().eval(
                        ("return redis.call('CF.ADDNX', KEYS[1], ARGV[1])").getBytes(),
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
                        ("return redis.call('CF.EXISTS', KEYS[1], ARGV[1])").getBytes(),
                        ReturnType.BOOLEAN,
                        1,
                        key.getBytes(),
                        value.getBytes()
                )
        ));
    }

    /**
     * 批量判断是否存在
     */
    public List<Boolean> mexists(String key, String... items) {
        if (items == null || items.length == 0) {
            return Collections.emptyList();
        }

        byte[][] keysAndArgs = new byte[1 + items.length][];
        keysAndArgs[0] = key.getBytes();
        for (int i = 0; i < items.length; i++) {
            keysAndArgs[i + 1] = items[i].getBytes();
        }

        return redisTemplate.execute((RedisCallback<List<Boolean>>) connection ->
                connection.scriptingCommands().eval(
                        ("return redis.call('CF.MEXISTS', KEYS[1], unpack(ARGV))").getBytes(),
                        ReturnType.MULTI,
                        1,
                        keysAndArgs
                )
        );
    }

    /**
     * 返回元素出现次数（近似）
     */
    public Long count(String key, String value) {
        return redisTemplate.execute((RedisCallback<Long>) connection ->
                connection.scriptingCommands().eval(
                        ("return redis.call('CF.COUNT', KEYS[1], ARGV[1])").getBytes(),
                        ReturnType.INTEGER,
                        1,
                        key.getBytes(),
                        value.getBytes()
                )
        );
    }

    /**
     * 删除元素
     *
     * @return true 删除成功，false 表示不存在
     */
    public boolean delete(String key, String value) {
        return Boolean.TRUE.equals(redisTemplate.execute((RedisCallback<Boolean>) connection ->
                connection.scriptingCommands().eval(
                        ("return redis.call('CF.DEL', KEYS[1], ARGV[1])").getBytes(),
                        ReturnType.BOOLEAN,
                        1,
                        key.getBytes(),
                        value.getBytes()
                )
        ));
    }

    /**
     * 批量插入，不去重
     */
    public List<Boolean> insert(String key, String... items) {
        if (items == null || items.length == 0) {
            return Collections.emptyList();
        }

        byte[][] keysAndArgs = new byte[1 + items.length][];
        keysAndArgs[0] = key.getBytes();
        for (int i = 0; i < items.length; i++) {
            keysAndArgs[i + 1] = items[i].getBytes();
        }

        return redisTemplate.execute((RedisCallback<List<Boolean>>) connection ->
                connection.scriptingCommands().eval(
                        ("return redis.call('CF.INSERT', KEYS[1], 'ITEMS', unpack(ARGV))").getBytes(),
                        ReturnType.MULTI,
                        1,
                        keysAndArgs
                )
        );
    }

    /**
     * 批量插入，去重
     */
    public List<Boolean> insertNx(String key, String... items) {
        if (items == null || items.length == 0) {
            return Collections.emptyList();
        }

        byte[][] keysAndArgs = new byte[1 + items.length][];
        keysAndArgs[0] = key.getBytes();
        for (int i = 0; i < items.length; i++) {
            keysAndArgs[i + 1] = items[i].getBytes();
        }

        return redisTemplate.execute((RedisCallback<List<Boolean>>) connection ->
                connection.scriptingCommands().eval(
                        ("return redis.call('CF.INSERTNX', KEYS[1], 'ITEMS', unpack(ARGV))").getBytes(),
                        ReturnType.MULTI,
                        1,
                        keysAndArgs
                )
        );
    }

    /**
     * 获取 Cuckoo Filter 元信息
     */
    public Map<String, Object> info(String key) {
        List<Object> result = redisTemplate.execute(
                (RedisCallback<List<Object>>) connection ->
                        connection.scriptingCommands().eval(
                                "return redis.call('CF.INFO', KEYS[1])".getBytes(),
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

    /**
     * 字节数组转字符串
     * info 返回的 List
     * [
     *   byte[]("Size"),                  Long(1080),
     *   byte[]("Number of buckets"),     Long(512),
     *   byte[]("Number of filters"),     Long(1),
     *   ...
     * ]
     */
    private String toString(Object obj) {
        if (obj instanceof byte[]) {
            return new String((byte[]) obj);
        }
        return String.valueOf(obj);
    }
}

