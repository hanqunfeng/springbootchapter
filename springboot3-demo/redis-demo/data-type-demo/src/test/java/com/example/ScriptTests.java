package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * Lua 脚本测试类
 * Created by hanqf on 2026/1/11 15:33.
 */

@SpringBootTest
public class ScriptTests {
    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Test
    public void test() {
        String lua = """
                local rateKey = KEYS[1]
                local maxReq  = tonumber(ARGV[1])
                local window  = tonumber(ARGV[2])
                local now     = tonumber(ARGV[3])
                
                local minTime = now - window
                
                -- 1. 清理过期请求
                redis.call("ZREMRANGEBYSCORE", rateKey, 0, minTime)
                
                -- 2. 当前请求数
                local count = tonumber(redis.call("ZCARD", rateKey))
                
                if count >= maxReq then
                    return count
                end
                
                -- 3. 记录请求
                redis.call("ZADD", rateKey, now, now)
                
                -- 4. 设置自动过期
                redis.call("PEXPIRE", rateKey, window)
                
                return count+1
                """;

        final Long count = redisTemplate.execute((RedisCallback<Long>) connection ->
                connection.scriptingCommands().eval(
                        lua.getBytes(),
                        ReturnType.INTEGER,
                        1,
                        "rate:1001".getBytes(),
                        "5".getBytes(),
                        "60000".getBytes(),
                        "1705000003000".getBytes()
                )
        );
        System.out.println(count);

        // 预热脚本
        final String sha = redisTemplate.execute((RedisCallback<String>) connection ->
                connection.scriptingCommands().scriptLoad(lua.getBytes())
        );
        System.out.println(sha);
        // 执行脚本
        final Long count2 = redisTemplate.execute((RedisCallback<Long>) connection ->
                connection.scriptingCommands().evalSha(
                        sha.getBytes(),
                        ReturnType.INTEGER,
                        1,
                        "rate:1001".getBytes(),
                        "5".getBytes(),
                        "60000".getBytes(),
                        "1705000003000".getBytes()
                )
        );
        System.out.println(count2);

    }
}
