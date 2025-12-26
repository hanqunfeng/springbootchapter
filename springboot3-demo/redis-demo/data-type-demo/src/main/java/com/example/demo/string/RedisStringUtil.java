package com.example.demo.string;

import com.example.demo.CommonUtil;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Redis String 类型操作工具类
 * 提供了与 Redis 原生命令相对应的方法
 */
@Component
public class RedisStringUtil extends CommonUtil {


    /**
     * 设置指定 key 的值
     * 对应 Redis 命令: SET key value
     * 商品
     *
     * @param key   键
     * @param value 值
     */
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public void setKeepTtl(String key, Object value) {
        redisTemplate.execute((RedisCallback<Boolean>) connection ->
                connection.stringCommands().set(
                        Objects.requireNonNull(redisTemplate.getStringSerializer().serialize(key)),
                        Objects.requireNonNull(((RedisSerializer<Object>) redisTemplate.getValueSerializer()).serialize(value)),
                        Expiration.keepTtl(),
                        RedisStringCommands.SetOption.UPSERT
                )
        );
    }


    /**
     * 只在键不存在时设置
     * 对应 Redis 命令: SET key value NX
     *
     * @param key   键
     * @param value 值
     */
    public void setNx(String key, Object value) {
        redisTemplate.opsForValue().setIfAbsent(key, value);
    }

    /**
     * 只在键不存在时设置，并设置过期时间
     * 对应 Redis 命令: SET key value NX EX seconds
     *
     * @param key     键
     * @param value   值
     * @param seconds 过期时间（秒）
     */
    public void setNxEx(String key, Object value, long seconds) {
        redisTemplate.opsForValue().setIfAbsent(key, value, seconds, TimeUnit.SECONDS);
    }

    /**
     * 键存在时设置
     * 对应 Redis 命令: SET key value XX
     *
     * @param key   键
     * @param value 值
     */
    public void setXx(String key, Object value) {
        redisTemplate.opsForValue().setIfPresent(key, value);
    }

    /**
     * 键存在时设置，并设置过期时间
     * 对应 Redis 命令: SET key value XX EX seconds
     *
     * @param key     键
     * @param value   值
     * @param seconds 过期时间（秒）
     */
    public void setXxEx(String key, Object value, long seconds) {
        redisTemplate.opsForValue().setIfPresent(key, value, seconds, TimeUnit.SECONDS);
    }


    /**
     * 将值 value 关联到 key，并将 key 的过期时间设为 seconds (以秒为单位)
     * 对应 Redis 命令: SETEX key seconds value
     * SETEX 已经不推荐使用，请使用 SET key value EX seconds
     *
     * @param key     键
     * @param value   值
     * @param seconds 过期时间（秒E）
     */
    public void setEx(String key, Object value, long seconds) {
        redisTemplate.opsForValue().set(key, value, seconds, TimeUnit.SECONDS);
    }


    /**
     * 获取指定 key 的值
     * 对应 Redis 命令: GET key
     *
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 获取并设置指定 key 的值，并设置过期时间
     * 对应 Redis 命令: GETEX key EX seconds
     *
     * @param key     键
     * @param seconds 过期时间（秒E）
     */
    public Object getEx(String key, long seconds) {
        return redisTemplate.opsForValue().getAndExpire(key, seconds, TimeUnit.SECONDS);
    }

    /**
     * 获取并删除指定 key 的值
     * 对应 Redis 命令: GETDEL key
     *
     * @param key 键
     * @return 旧值
     */
    public Object getDel(String key) {
        return redisTemplate.opsForValue().getAndDelete(key);
    }

    /**
     * 获取指定 key 的值，并指定返回的字符串的起始位置和结束位置
     * 对应 Redis 命令: GETRANGE key start end
     *
     * @param key   键
     * @param start 起始位置
     * @param end   结束位置
     * @return 值
     */
    public Object getRange(String key, long start, long end) {
        return redisTemplate.opsForValue().get(key, start, end);
    }

    /**
     * 获取指定 key 的值长度
     * 对应 Redis 命令: STRLEN key
     *
     * @param key 键
     * @return 值长度
     */
    public Long getSize(String key) {
        return redisTemplate.opsForValue().size(key);
    }

    /**
     * 获取并设置指定 key 的值
     * 对应 Redis 命令: GETSET key value
     * GETSET 已经不推荐使用，请使用 SET key value GET
     *
     * @param key   键
     * @param value 值
     * @return 旧值
     */
    public Object getAndSet(String key, Object value) {
        return redisTemplate.opsForValue().getAndSet(key, value);
    }

    /**
     * 获取 key 的当前值，并移除该 key 的过期时间，使其变为永久键
     * 对应 Redis 命令: GETEX key PERSIST
     *
     * @param key 键
     * @return 值
     */
    public Object getAndPersist(String key) {
        return redisTemplate.opsForValue().getAndPersist(key);
    }

    /**
     * 将 key 中储存的数字值增一
     * 对应 Redis 命令: INCR key
     *
     * @param key 键
     * @return 执行 INCR 命令之后 key 的值
     */
    public Long incr(String key) {
        return redisTemplate.opsForValue().increment(key);
    }

    /**
     * 将 key 中储存的数字值增加 delta
     * 对应 Redis 命令: INCRBY key increment
     *
     * @param key   键
     * @param delta 要增加几
     * @return 执行 INCR 命令之后 key 的值
     */
    public Long incr(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 将 key 中储存的数字值减一
     * 对应 Redis 命令: DECR key
     *
     * @param key 键
     * @return 执行 DECR 命令之后 key 的值
     */
    public Long decr(String key) {
        return redisTemplate.opsForValue().decrement(key);
    }

    /**
     * 将 key 中储存的数字值减少 delta
     * 对应 Redis 命令: DECRBY key decrement
     *
     * @param key   键
     * @param delta 要减少几
     * @return 执行 DECR 命令之后 key 的值
     */
    public Long decr(String key, long delta) {
        return redisTemplate.opsForValue().decrement(key, delta);
    }


    /**
     * 将指定字符串追加到 key 原有值的末尾，并返回追加后的字符串长度
     * 对应 Redis 命令: APPEND key value
     *
     * @param key   键
     * @param value 值
     * @return 追加给定字符串后，字符串的长度
     */
    public Integer append(String key, String value) {
        return redisTemplate.opsForValue().append(key, value);
    }

    /**
     * 同时设置多个 key-value
     * 对应 Redis 命令: MSET key value [key value ...]
     *
     * @param map key-value 键值对
     */
    public void multiSet(Map<String, Object> map) {
        redisTemplate.opsForValue().multiSet(map);
    }

    /**
     * 获取多个 key 的值
     * 对应 Redis 命令: MGET key [key ...]
     *
     * @param keys 键
     * @return 值列表
     */
    public List<Object> multiGet(Collection<String> keys) {
        return redisTemplate.opsForValue().multiGet(keys);
    }

    /**
     * 同时设置多个 key-value，仅当所有 key 都不存在时
     * 对应 Redis 命令: MSETNX key value [key value ...]
     *
     * @param map key-value 键值对
     * @return 是否成功
     */
    public Boolean multiSetIfAbsent(Map<String, Object> map) {
        return redisTemplate.opsForValue().multiSetIfAbsent(map);
    }
}
