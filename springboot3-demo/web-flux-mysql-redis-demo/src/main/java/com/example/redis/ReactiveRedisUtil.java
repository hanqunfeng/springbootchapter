package com.example.redis;

/**
 * <h1>ReactiveRedisUtil</h1>
 * Created by hanqf on 2023/8/30 16:45.
 */

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.serializer.RedisElementReader;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * <h1>响应式redis工具类</h1>
 * Created by hanqf on 2020/11/19 14:49.
 */

@Component
@Slf4j
public class ReactiveRedisUtil {

    @Autowired
    private ReactiveRedisTemplate<String, Object> reactiveRedisTemplate;

    private static final String LOCK_KEY_PREFIX = "lock:";
    private static final String RELEASE_LOCK_SCRIPT = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";


    public Mono<Object> getValue(String key) {
        return reactiveRedisTemplate.opsForValue().get(key);
    }

    public Mono<Boolean> putValue(String key, Object value, long time, ChronoUnit timeUnit) {
        return reactiveRedisTemplate.opsForValue().set(key, value, Duration.of(time, timeUnit));
    }

    public Mono<Boolean> putValue(String key, Object value) {
        return reactiveRedisTemplate.opsForValue().set(key, value);
    }

    public Mono<Boolean> delete(String key) {
        return reactiveRedisTemplate.opsForValue().delete(key);
    }

    public <V> Mono<Boolean> putHashValue(String key, String hashKey, V hashValue) {
        ReactiveHashOperations<String, String, V> opsForHash = reactiveRedisTemplate.opsForHash();
        return opsForHash.put(key, hashKey, hashValue);
    }

    public <V> Mono<V> getHashValue(String key, String hashKey) {
        ReactiveHashOperations<String, String, V> opsForHash = reactiveRedisTemplate.opsForHash();
        return opsForHash.get(key, hashKey);
    }

    public <V> Mono<List<V>> getMultiHashValue(String key, Collection<String> hashKeys) {
        ReactiveHashOperations<String, String, V> opsForHash = reactiveRedisTemplate.opsForHash();
        return opsForHash.multiGet(key, hashKeys);
    }

    public <V> Flux<String> getHashKeys(String key) {
        ReactiveHashOperations<String, String, V> opsForHash = reactiveRedisTemplate.opsForHash();
        return opsForHash.keys(key);
    }

    public <V> Mono<Long> deleteHashKeys(String key, Collection<String> hashKeys) {
        if (CollectionUtils.isEmpty(hashKeys)) {
            return Mono.just(0L);
        }
        ReactiveHashOperations<String, String, V> opsForHash = reactiveRedisTemplate.opsForHash();
        return opsForHash.remove(key, hashKeys.toArray());
    }

    /**
     * 预加载lua脚本
     */
    public String luaScriptLoad(String luaScript) {
        return reactiveRedisTemplate.getConnectionFactory().getReactiveConnection().scriptingCommands()
                .scriptLoad(ByteBuffer.wrap(luaScript.getBytes(StandardCharsets.UTF_8))).block();
    }

    public Mono<Boolean> luaScriptExists(String luaScript) {
        RedisScript<Object> redisScript = RedisScript.of(luaScript);
        String sha = redisScript.getSha1();
        log.info("luaScript sha :" + sha);
        return reactiveRedisTemplate.getConnectionFactory().getReactiveConnection()
                .scriptingCommands()
                .scriptExists(sha);
    }

    /**
     * 执行lua脚本
     *
     * @param luaScript 脚本内容
     * @param keys      redis键列表
     * @param values    值列表
     * @return
     */
    public Flux<Object> executeLuaScript(String luaScript, List<String> keys, List<Object> values) {
        RedisScript<Object> redisScript = RedisScript.of(luaScript);
        log.info("luaScript sha :" + redisScript.getSha1());
        return reactiveRedisTemplate.execute(redisScript, keys, values);
    }

    /**
     * 执行lua脚本
     *
     * @param luaScript  脚本内容
     * @param keys       redis键列表
     * @param values     值列表
     * @param resultType 返回值类型  支持 Long
     * @return
     */
    public <T> Flux<T> executeLuaScript(String luaScript, List<String> keys, List<Object> values, Class<T> resultType) {
        RedisScript<T> redisScript = RedisScript.of(luaScript, resultType);
        log.info("luaScript sha :" + redisScript.getSha1());
        return reactiveRedisTemplate.execute(redisScript, keys, values);
    }

    /**
     * 执行lua脚本
     *
     * @param luaScript  脚本内容
     * @param keys       redis键列表
     * @param values     值列表
     * @param resultType 返回值类型
     * @param resultSerializer 返回值序列化器  返回值是什么类型，就用什么类型的序列化器进行转换
     * @return
     */
    public <T> Flux<T> executeLuaScript(String luaScript, List<String> keys, List<Object> values, Class<T> resultType, RedisSerializer<T> resultSerializer) {
        RedisScript<T> redisScript = RedisScript.of(luaScript, resultType);
        log.info("luaScript sha :" + redisScript.getSha1());
        return reactiveRedisTemplate.execute(redisScript, keys, values, reactiveRedisTemplate.getSerializationContext().getValueSerializationPair().getWriter(), RedisElementReader.from(resultSerializer));
    }

    /**
     * 获取分布式锁
     *
     * @param lockKey    锁key
     * @param requestId  锁体，可以是任意内容，但是释放锁时会要求提供锁体进行验证
     * @param expireTime 锁过期时间，单位秒
     * @return
     */
    public Mono<Boolean> tryGetDistributedLock(String lockKey, String requestId, long expireTime) {
        Assert.hasLength(lockKey, "lockKey must not be empty");
        Assert.hasLength(requestId, "requestId must not be empty");
        String innerLockKey = buildLockKey(lockKey);
        return reactiveRedisTemplate.opsForValue().setIfAbsent(innerLockKey, requestId, Duration.ofSeconds(expireTime));
    }

    /**
     * 释放分布式锁
     *
     * @param lockKey   锁key
     * @param requestId 锁体，可以是任意内容，但是释放锁时会要求提供锁体进行验证
     * @return
     */
    public Mono<Boolean> releaseDistributedLock(String lockKey, String requestId) {
        Assert.hasLength(lockKey, "lockKey must not be empty");
        Assert.hasLength(requestId, "requestId must not be empty");
        String innerLockKey = buildLockKey(lockKey);

        return this.executeLuaScript(RELEASE_LOCK_SCRIPT, Collections.singletonList(innerLockKey), Collections.singletonList(requestId), Long.class)
                .next()
                .map(count -> count == 1)
                .doOnError(t -> log.error("release lockkey: [{}] failure", innerLockKey, t))
                .onErrorResume(e -> Mono.just(false));
    }

    /**
     * 添加前缀
     *
     * @param lockKey
     * @return
     */
    private String buildLockKey(String lockKey) {
        Assert.hasLength(lockKey, "lockKey must not be empty");
        return LOCK_KEY_PREFIX + lockKey;
    }
}
